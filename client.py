import socket

s = socket.socket()
s.connect(('192.168.2.2',6969))
while True:
    str = "HALAOPWLOAWKLOKAOWA"
    s.send(str.encode())
    if(str == "Bye" or str == "bye"):
        break
    print("N:",s.recv(1024).decode())
s.close()