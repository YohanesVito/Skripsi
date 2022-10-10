# scp sendString.py pi@192.168.100.4:/home/pi/Desktop
# generate random integer values
from random import seed
from random import randint
import random
from time import sleep
# seed random number generator
seed(1)

def sendBt(data):
    print(data)
    with open("/dev/rfcomm0","w",1) as f:
        f.write(str(data))


def generateRandom():
    penanda = 0x02
    rpm = randint(0,400)
    speed = round(random.uniform(0,50), 2)
    battery = round(random.uniform(0,100), 2)
    duty_cycle = round(random.uniform(0,100), 2)

    value = f"{penanda}{rpm};{speed};{battery};{duty_cycle}#"

    return value
# generate some integers
while(True):
    data = generateRandom()
    sendBt(data)
    # print(generateRandom())
    sleep(0.2)