import paho.mqtt.client as mqtt
import json
import MySQLdb
import Hardware
import User
import Logging

# MQTT broker settings
broker_address = "mqtt-broker"
broker_address = "emqx"
broker_port = 1883

# local MQTT broker settings
# broker_address = "broker.emqx.io"
# broker_port = 1883

# Define the MQTT topics
user_topic = "mokura/user"
hardware_topic = "mokura/hardware"
logging_topic = "mokura/logging"

# MySQL database settings
db_host = "mariadb"
db_user = "vito"
db_password = "123"
db_name = "mokura"

# local database settings
# db_host = 'localhost'
# db_user = 'root'
# db_password = ''
# db_name = "mokura"

def on_connect(client, userdata, flags, rc):
    print("Connected to MQTT broker with result code "+str(rc))
    client.subscribe(logging_topic)
    client.subscribe(hardware_topic)
    client.subscribe(user_topic)

def on_message(client,userdata,msg):

    print("Received message on topic "+msg.topic+": "+msg.payload.decode())

    try:
        # Connect to MySQL database
        db = MySQLdb.connect(host=db_host, user=db_user, passwd=db_password, db=db_name)
        cursor = db.cursor()


        # Process the message based on the topic
        if msg.topic == "mokura/user":
            # Deserialize the message payload into a User object
            user_data = json.loads(msg.payload.decode())
            user = User.User(**user_data)

            # Insert the user data into the MySQL database
            sql = "INSERT INTO users (email, username, password) VALUES (%s, %s, %s)"
            val = (user.email, user.username, user.password)
            cursor.execute(sql, val)
            db.commit()
        
        elif msg.topic == "mokura/hardware":
            # Deserialize the message payload into a Hardware object
            hardware_data = json.loads(msg.payload.decode())
            hardware = Hardware.Hardware(**hardware_data)

            # Insert the Hardware object into the database
            sql = "INSERT INTO mokura (hardware_name, hardware_serial) VALUES (%s, %s)"
            val = (hardware.hardware_name, hardware.hardware_serial)
            cursor.execute(sql, val)
            db.commit()

        elif msg.topic == "mokura/logging":
            # Deserialize the message payload into a Logging object
            logging_data = json.loads(msg.payload.decode())
            logging = Logging.Logging(**logging_data)

            # Insert the Logging object into the database
            sql = "INSERT INTO logging (id_hardware, id_user, time_stamp, speed, rpm, battery, lat, lon, compass, duty_cycle) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
            val = (logging.id_hardware, logging.id_user, logging.time_stamp, logging.speed, logging.rpm, logging.battery, logging.lat, logging.lon, logging.compass, logging.duty_cycle)
            cursor.execute(sql, val)
            db.commit()
            print("Inserted data into database")


        else:
            print("Invalid topic")

    except MySQLdb.Error as err:
        print("MySQL Error: ", err)

    finally:
        # Close the cursor and database connection
        cursor.close()
        db.close()


# Create the MQTT client for the user topic and subscribe to the topic
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(broker_address, broker_port,60)

# Start the MQTT clients
client.loop_forever()
