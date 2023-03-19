import paho.mqtt.client as mqtt
import MySQLdb

# MQTT broker settings
broker_address = "mqtt-broker"
broker_port = 1883
topic = "test/topic"
#topic = "mokura/logging/logging-vito-m03" 
#topic1= "mokura/user/email/vitorizki37@gmail.com"
#topic2= "mokura/mokura/hardwareaddress/{}"
#topic2= "mokura/logging/id_logging/{}"

# MySQL database settings
db_host = "mariadb"
db_user = "vito"
db_password = "123"
db_name = "mokura"

# Connect to MySQL database
db = MySQLdb.connect(host=db_host, user=db_user, passwd=db_password, db=db_name)
cursor = db.cursor()

# Define MQTT callbacks
def on_connect(client, userdata, flags, rc):
    print("Connected to MQTT broker with result code "+str(rc))
    client.subscribe(topic)

def on_message(client, userdata, msg):
    print("Received message on topic "+msg.topic+": "+msg.payload.decode())

    # if(msg.topic.start"mokura/user/email/")
    # Insert message into MySQL database
    sql = "INSERT INTO messages (topic, payload) VALUES (%s, %s)"
    values = (msg.topic, msg.payload.decode())
    cursor.execute(sql, values)
    db.commit()

# Create MQTT client and connect to broker
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(broker_address, broker_port, 60)

# Start MQTT loop
client.loop_forever()
