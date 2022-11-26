# python3.6

import random

from paho.mqtt import client as mqtt_client
import json
import MySQLdb

broker = 'broker.emqx.io'
port = 1883
topic = "vito/test"
# generate client ID with pub prefix randomly
client_id = f'python-mqtt-{random.randint(0, 100)}'
username = 'emqx'
password = 'public'


def connect_mqtt() -> mqtt_client:
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)

    client = mqtt_client.Client(client_id)
    client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client


def subscribe(client: mqtt_client):
    def on_message(client, userdata, msg):
        print(f"Received `{msg.payload.decode()}` from `{msg.topic}` topic")
        insertDB(JSONParser(msg.payload))

    client.subscribe(topic)
    client.on_message = on_message
    

def run():
    client = connect_mqtt()
    subscribe(client)
    client.loop_forever()

def insertDB(data):
    host = "44.194.169.154"
    user = "vito"
    password = "123"
    db = "mokura"
    
    db = MySQLdb.connect(host,user,password,db)
    cur = db.cursor()
    cur.execute("INSERT INTO data (time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",(data.time_stamp,data.speed,data.rpm,data.battery,data.lat,data.lon,data.compass,data.duty_cycle))

    db.commit() #commit the data insertion execution
    cur.close()
    db.close()
    print(data.time_stamp,data.speed,data.rpm,data.battery,data.lat,data.lon,data.compass,data.duty_cycle)

class JSONParser(object):
    def __init__(self, data):
        self.__dict__ = json.loads(data)

if __name__ == '__main__':
    run()
