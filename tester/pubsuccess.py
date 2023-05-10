import paho.mqtt.client as mqtt

# Set up MQTT client
client = mqtt.Client()
broker_address = "localhost"
broker_port = 1883
# client.connect("mqtt://"+address+":"+"1883", 1883)
client.connect(broker_address, broker_port)

# Publish message
topic = "mokura/user"
message = "Hello, world!"
client.publish(topic, message)

# Disconnect from MQTT broker
client.disconnect()