from dotenv import load_dotenv
import os

load_dotenv()

RABBIT_HOST = os.getenv("RABBIT_HOST")
RABBIT_PORT = os.getenv("RABBIT_PORT")

BOT_MESSAGE = os.getenv("BOT_MESSAGE")
MESSAGE_STATUS = os.getenv("MESSAGE_STATUS")
HUMAN_MESSAGE = os.getenv("HUMAN_MESSAGE")

VK_TOKEN = os.getenv("VK_TOKEN")
VK_GROUP_ID = os.getenv("VK_GROUP_ID")
