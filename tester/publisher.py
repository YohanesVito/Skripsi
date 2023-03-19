import paho.mqtt.client as mqtt

# Set up MQTT client
client = mqtt.Client()
broker_address = "35.171.206.57"
broker_port = 1883
# client.connect("mqtt://"+address+":"+"1883", 1883)
client.connect(broker_address, broker_port)

# Publish message
topic = "test/topic"
message = "Hello, world!"
client.publish(topic, message)

# Disconnect from MQTT broker
client.disconnect()
