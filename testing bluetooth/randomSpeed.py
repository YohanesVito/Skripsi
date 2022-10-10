# generate random integer values
from random import seed
from random import randint
from time import sleep
# seed random number generator
seed(1)

def sendBt(data):
    with open("/dev/rfcomm0","w",1) as f:
        f.write(str(data))

# generate some integers
while(True):
	value = randint(0, 70)
	sendBt(value)
    sleep(1)