import paho.mqtt.client as mqtt
import json
import MySQLdb
import Hardware
import User
import Logging

# MQTT broker settings
broker_address = "mqtt-broker"
broker_port = 1883

# Define the MQTT topics
user_topic = "mokura/user"
hardware_topic = "mokura/hardware"
logging_topic = "mokura/logging"

# MySQL database settings
db_host = "mariadb"
db_user = "vito"
db_password = "123"
db_name = "mokura"

def on_message(client, userdata, msg):
    print("Received message on topic "+msg.topic+": "+msg.payload.decode())

    try:
        # Connect to MySQL database
        db = MySQLdb.connect(host=db_host, user=db_user, passwd=db_password, db=db_name)
        cursor = db.cursor()


        # Process the message based on the topic
        if msg.topic == "mokura/user":
            # Deserialize the message payload into a User object
            user_data = json.loads(msg.payload.decode())
            user = User(**user_data)

            # Insert the user data into the MySQL database
            sql = "INSERT INTO users (email, username, password) VALUES (%s, %s, %s)"
            val = (user.email, user.username, user.password)
            cursor.execute(sql, val)
            db.commit()
        
        elif msg.topic == "mokura/hardware":
            # Deserialize the message payload into a Hardware object
            hardware_data = json.loads(msg.payload.decode())
            hardware = Hardware(**hardware_data)

            # Insert the Hardware object into the database
            sql = "INSERT INTO hardware (hardware_name, hardware_address) VALUES (%s, %s)"
            val = (hardware.hardware_name, hardware.hardware_address)
            cursor.execute(sql, val)
            db.commit()

        elif msg.topic == "mokura/logging":
            # Deserialize the message payload into a Logging object
            logging_data = json.loads(msg.payload.decode())
            logging = Logging(**logging_data)

            # Insert the Logging object into the database
            sql = "INSERT INTO logging (idHardware, idUser, timeStamp, speed, rpm, battery, lat, lon, compass, dutyCycle) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
            val = (logging.idHardware, logging.idUser, logging.timeStamp, logging.speed, logging.rpm, logging.battery, logging.lat, logging.lon, logging.compass, logging.dutyCycle)
            cursor.execute(sql, val)
            db.commit()

        else:
            print("Invalid topic")

    except MySQLdb.Error as err:
        print("MySQL Error: ", err)

    finally:
        # Close the cursor and database connection
        cursor.close()
        db.close()


# Create the MQTT client for the user topic and subscribe to the topic
user_client = mqtt.Client("user_client")
user_client.connect(broker_address, broker_port)
user_client.subscribe(user_topic)
user_client.on_message = on_message

# Create the MQTT client for the hardware topic and subscribe to the topic
hardware_client = mqtt.Client("hardware_client")
hardware_client.connect(broker_address, broker_port)
hardware_client.subscribe(hardware_topic)
hardware_client.on_message = on_message

# Create the MQTT client for the logging topic and subscribe to the topic
logging_client = mqtt.Client("logging_client")
logging_client.connect(broker_address, broker_port)
logging_client.subscribe(logging_topic)
logging_client.on_message = on_message

# Start the MQTT clients
user_client.loop_start()
hardware_client.loop_start()
logging_client.loop_start()
