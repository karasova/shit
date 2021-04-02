import json
import logging
import sys
import time
import traceback
from typing import Optional

import pika
import vk_api
from pika.adapters.blocking_connection import BlockingChannel
from vk_api.bot_longpoll import VkBotLongPoll, VkBotEventType
from vk_api.keyboard import VkKeyboard, VkKeyboardColor

from response_receiver import ResponseReceiver
from settings import VK_TOKEN, RABBIT_HOST, RABBIT_PORT, VK_GROUP_ID, HUMAN_MESSAGE
from utils import callback_send_message

API_VERSION = '5.124'
CALLBACK_TYPES = ("show_snackbar", "open_link", "open_app")

logging.basicConfig(stream=sys.stdout, level=logging.INFO)

connection = pika.BlockingConnection(
    pika.ConnectionParameters(
        host=RABBIT_HOST,
        port=RABBIT_PORT,
        heartbeat=30
    )
)
connection_for_response = pika.BlockingConnection(
    pika.ConnectionParameters(
        host=RABBIT_HOST,
        port=RABBIT_PORT,
        heartbeat=30
    )
)


def listen_longpoll(longpoll: VkBotLongPoll):
    while True:
        connection.sleep(0.001)
        for event in longpoll.check():
            yield event


def main():
    vk_session = vk_api.VkApi(token=VK_TOKEN, api_version=API_VERSION)
    vk = vk_session.get_api()

    longpoll = VkBotLongPoll(vk_session, group_id=VK_GROUP_ID)

    response_receiver = ResponseReceiver(connection=connection_for_response, session=vk_session)
    response_receiver.start()

    keyboard = VkKeyboard(one_time=False, inline=True)
    keyboard.add_callback_button(
        label='Callback button',
        color=VkKeyboardColor.SECONDARY,
        payload={
            'type': 'text'
        }
    )
    keyboard.add_line()
    keyboard.add_button(
        label='text',
        color=VkKeyboardColor.SECONDARY,
        payload={
            'type': 'text',
            'label': 'label',
            'payload': 'payload1'
        }
    )
    keyboard.add_line()
    keyboard.add_callback_button(
        label='Callback button 2',
        color=VkKeyboardColor.PRIMARY,
        payload={
            'type': 'my_type'
        }
    )

    message_channel: Optional[BlockingChannel] = None

    logging.info('Bot start listen')
    while True:
        try:
            message_channel: BlockingChannel = connection.channel()
            for event in listen_longpoll(longpoll):
                logging.info('new event: {}'.format(event))
                if event.type == VkBotEventType.MESSAGE_NEW:
                    if event.from_user:
                        message = event.obj.message
                        logging.info(
                            'new message from {}: "{}"'.format(message['from_id'], message['text']))

                        msg = message['text']
                        user = message['from_id']
                        peer = message['peer_id']
                        date = message['date']
                        attachments = message['attachments']

                        payload = []
                        if 'payload' in message:
                            payload = message['payload']

                        body = {
                            'type': 'message_new',
                            'from_id': user,
                            'peer_id': peer,
                            'text': msg,
                            'date': date,
                            'attachments': attachments,
                            'payload': payload
                        }

                        message_channel.basic_publish(exchange='human_message', routing_key='',
                                                      body=json.dumps(body).encode('UTF-8'))

                if event.type == VkBotEventType.MESSAGE_EVENT:
                    event_object = event.object
                    obj = event_object
                    logging.info('callback {} from {}'.format(obj.payload.get('type'), obj.user_id))

                    payload = obj.payload
                    user = obj.user_id
                    peer = obj.peer_id

                    body = {
                        'type': 'message_event',
                        'user_id': user,
                        'peer_id': peer,
                        'payload': payload
                    }

                    obj.payload = json.dumps({})

                    callback_send_message(
                        vk,
                        event
                    )

                    message_channel.basic_publish(exchange=HUMAN_MESSAGE, routing_key='',
                                                  body=json.dumps(body).encode('UTF-8'))
        except KeyboardInterrupt:
            logging.info('Stopping bot...')
            time.sleep(2)
            response_receiver.stop()
        except Exception as e:
            logging.error(e)
            response_receiver.stop()
            logging.error('Error in VK event loop: %s', traceback.format_exc())
        finally:
            if message_channel is not None and message_channel.is_open:
                message_channel.close()


if __name__ == "__main__":
    main()
