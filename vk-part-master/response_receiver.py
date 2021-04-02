import json
import logging
import sys
from threading import Thread
from datetime import datetime

import pika
from vk_api import VkApi
from vk_api.keyboard import VkKeyboard

from settings import MESSAGE_STATUS, BOT_MESSAGE
from utils import send_message

logging.basicConfig(format='%(asctime)s [%(levelname)s] : %(message)s', stream=sys.stdout, level=logging.INFO)


def create_keyboard(msgs):
    inline = False
    if msgs['message']['keyboard']['type'] == 'INLINE':
        inline = True
    one_time = msgs['message']['keyboard']['oneTime']
    keyboard = VkKeyboard(one_time=one_time, inline=inline)
    for i in range(len(msgs['message']['keyboard']['items'])):
        item = msgs['message']['keyboard']['items'][i]
        if item['type'] == 'text':
            keyboard.add_button(
                label=item['label'],
                payload=item['payload'],
                color=item['color'].lower()
            )
        elif item['type'] == 'open_link':
            keyboard.add_callback_button(
                label=item['label'],
                payload={
                    'type': 'open_link',
                    'link': item['link']
                },
                color=item['color'].lower()
            )
        else:
            keyboard.add_callback_button(
                label=item['label'],
                payload=item['payload'],
                color=item['color'].lower()
            )

        if i != len(msgs['message']['keyboard']['items']) - 1:
            keyboard.add_line()
    keyboard = keyboard.get_keyboard()
    return keyboard


class ResponseReceiver(Thread):
    def __init__(self, connection: pika.BlockingConnection, session: VkApi):
        Thread.__init__(self)
        self.connection = connection
        self.channel: pika.adapters.blocking_connection.BlockingChannel = self.connection.channel()
        self.vk = session
        self.api = self.vk.get_api()

    def callback(self, ch, method, properties, body):
        msgs = json.loads(body.decode())
        keyboard = None

        if 'keyboard' in msgs['message'] and msgs['message']['keyboard'] is not None:
            keyboard = create_keyboard(msgs)

        statuses = {
            'time': datetime.now().astimezone().replace(microsecond=0).isoformat(),
            'seed': msgs['seed'],
            'vk_users': {
                'success': [],
                'failure': []
            },
        }

        has_no_error = True
        logging.info('Mailing to ids: {} with text: "{}"'.format(msgs['vkIds'], msgs['message']['text']))
        for vkId in msgs['vkIds']:
            try:
                send_message(self.api, vkId, message=msgs['message']['text'], random_id=msgs['seed'],
                             keyboard=keyboard)
                statuses['vk_users']['success'].append(vkId)
            except Exception as e:
                logging.error(e)
                has_no_error = False
                statuses['vk_users']['failure'].append(vkId)
                continue

        self.channel.basic_publish(exchange=MESSAGE_STATUS, routing_key='',
                                   body=json.dumps(statuses).encode('UTF-8'))

        if has_no_error:
            ch.basic_ack(delivery_tag=method.delivery_tag)
        else:
            ch.basic_nack(delivery_tag=method.delivery_tag, multiple=False, requeue=False)

    def run(self):
        self.channel.basic_qos(prefetch_count=1)
        self.channel.basic_consume(queue=BOT_MESSAGE, on_message_callback=self.callback)

        self.channel.start_consuming()

    def stop(self):
        self.channel.close()
