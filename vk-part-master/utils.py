import json


def send_message(vk, user_id, message, random_id, keyboard=None, attachment=None):
    random_id = random_id * 228 + user_id

    vk.messages.send(
        user_id=user_id,
        peer_id=user_id,
        keyboard=keyboard,
        random_id=random_id,
        message=message,
        attachment=attachment
    )


def callback_send_message(vk, event):
    vk.messages.sendMessageEventAnswer(
        event_id=event.object.event_id,
        user_id=event.object.user_id,
        peer_id=event.object.peer_id,
        event_data=json.dumps(event.object.payload),
    )


def edit_message(vk, event, message=None):
    vk.messages.edit(
        peer_id=event.obj.peer_id,
        message=message,
        conversation_message_id=event.obj.conversation_message_id
    )
