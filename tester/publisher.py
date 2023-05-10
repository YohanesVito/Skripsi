import paho.mqtt.client as mqtt
import User, Hardware, Logging
import json

# Set up MQTT client
client = mqtt.Client("1")
broker_address = "35.171.206.57"
broker_port = 1883
# broker_address = "broker.emqx.io"
# broker_port = 1883
qos = 0

client.connect(broker_address, broker_port)

# Define the MQTT topics
user_topic = "mokura/user"
hardware_topic = "mokura/hardware"
logging_topic = "mokura/logging"

# Publish message for user topic
user = User.User(222,"testuser@example.com", "testuser", "password")
user_payload = json.dumps(user.__dict__)
client.publish(user_topic, user_payload, qos)

# Publish message for hardware topic
hardware = Hardware.Hardware(222,"test_hardware", "12:34:56:78:90")
hardware_payload = json.dumps(hardware.__dict__)
client.publish(hardware_topic, hardware_payload, qos)

# Publish message for logging topic
logging = Logging.Logging("25","3","ppppp", "ppppp", "asd", "asd", "asd", "asd", "asd", "asd", "asd")
logging_payload = json.dumps(logging.__dict__)
client.publish(logging_topic, logging_payload, qos)
client.loop(timeout=2)

# Disconnect from MQTT broker
client.disconnect()