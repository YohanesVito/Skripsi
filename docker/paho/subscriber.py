import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import json
import MySQLdb
import Hardware, User, Logging
import datetime
import pytz

# MQTT broker settings
broker_address = "mqtt-broker"
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
            publish_response("user inserted! ")
            val = (user.email, user.username, user.password)
            cursor.execute(sql, val)
            db.commit()
        
        elif msg.topic == "mokura/hardware":
            # Deserialize the message payload into a Hardware object
            hardware_data = json.loads(msg.payload.decode())
            hardware = Hardware.Hardware(**hardware_data)
            publish_response("hardware inserted! ")
            # Insert the Hardware object into the database
            sql = "INSERT INTO mokura (hardware_name, hardware_serial) VALUES (%s, %s)"
            
            val = (hardware.hardware_name, hardware.hardware_serial)
            cursor.execute(sql, val)
            db.commit()

        elif msg.topic == "mokura/logging":
            timeStamp = getTimeStamp()
            server_time_int = timeStamp[0]
            server_time_str = timeStamp[1]

            # Deserialize the message payload
            logging_data = json.loads(msg.payload.decode())

            if isinstance(logging_data, dict):
                # Dictionary case: Access elements using keys
                id_hardware = logging_data.get('id_hardware')
                id_user = logging_data.get('id_user')
                time_stamp = logging_data.get('time_stamp')
                speed = logging_data.get('speed')
                rpm = logging_data.get('rpm')
                battery = logging_data.get('battery')
                lat = logging_data.get('lat')
                lon = logging_data.get('lon')
                compass = logging_data.get('compass')
                duty_cycle = logging_data.get('duty_cycle')

            elif isinstance(logging_data, list):
                # List case: Access elements using integer indices
                id_hardware = logging_data[0]
                id_user = logging_data[1]
                time_stamp = logging_data[2]
                speed = logging_data[3]
                rpm = logging_data[4]
                battery = logging_data[5]
                lat = logging_data[6]
                lon = logging_data[7]
                compass = logging_data[8]
                duty_cycle = logging_data[9]

            # Insert the Logging object into the database
            sql = "INSERT INTO logging (id_hardware, id_user, time_stamp, speed, rpm, battery, lat, lon, compass, duty_cycle) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
            val = (id_hardware, id_user, time_stamp, speed, rpm, battery, lat, lon, compass, duty_cycle)
            cursor.execute(sql, val)
            db.commit()

            publish_response("logging inserted!", len(msg.payload), server_time_int, server_time_str)

        else:
            print("Invalid topic")

    except MySQLdb.Error as err:
        print("MySQL Error: ", err)

    finally:
        # Close the cursor and database connection
        cursor.close()
        db.close()

def getTimeStamp():
    # get current timestamp in UTC timezone
    utc_timestamp = datetime.datetime.utcnow()
    
    # create UTC+7 timezone object
    timezone_offset = datetime.timedelta(hours=7)
    
    # convert to UTC+7 timezone
    timestamp = utc_timestamp + timezone_offset

    server_time_int = int(timestamp.timestamp())

    server_time_str = timestamp.strftime("%Y/%m/%d %H:%M:%S:%f")

    return (server_time_int,server_time_str)

def publish_response(message, packet_size, server_time_int, server_time_str):

    response = {
        'message': message,
        'packet_size': packet_size,
        'server_time_int':server_time_int,
        'server_time_str':server_time_str
    }

    # Convert the response object to JSON format
    payload = json.dumps(response)

    # Create MQTT client and connect to the broker
    mqtt_client = mqtt.Client()
    mqtt_client.connect(broker_address, broker_port)

    # Publish the JSON payload to "mokura/user_response" topic
    mqtt_client.publish("mokura/user_response", payload=payload, qos=1)

    # Disconnect from the MQTT broker
    mqtt_client.disconnect()


# Create the MQTT client for the user topic and subscribe to the topic
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(broker_address, broker_port, 60)

# Start the MQTT clients
client.loop_forever()
