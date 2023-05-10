import paho.mqtt.client as mqtt
import User, Hardware, Logging
import json

# Set up MQTT client
client = mqtt.Client()
broker_address = "35.171.206.57"
broker_port = 1883
qos = 0

# Define the MQTT topics
user_topic = "mokura/user"
hardware_topic = "mokura/hardware"
logging_topic = "mokura/logging"

# Publish message for user topic
user = User("testuser@example.com", "testuser", "password")
user_payload = json.dumps(user.__dict__)
client.publish(user_topic, user_payload, qos)

# Publish message for hardware topic
hardware = Hardware("test_hardware", "12:34:56:78:90:ab")
hardware_payload = json.dumps(hardware.__dict__)
client.publish(hardware_topic, hardware_payload, qos)

# Publish message for logging topic
logging = Logging("test1234", "5678", "50", "5000", "90", "1.234567", "5.678901", "180", "50")
logging_payload = json.dumps(logging.__dict__)
client.publish(logging_topic, logging_payload, qos)

# Disconnect from MQTT broker
client.disconnect()