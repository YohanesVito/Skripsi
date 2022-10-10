# scp mqtt_Test.py pi@192.168.100.7:/home/pi/Desktop/test
with open("/dev/rfcomm0","w",1) as f:
    f.write("hello from python!")