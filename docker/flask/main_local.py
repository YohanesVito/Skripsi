from flask import Flask, jsonify, request
from flask_mysqldb import MySQL
import datetime

app = Flask(__name__)

# enable this when run on cloud 
app.config['MYSQL_HOST'] = 'mariadb'
app.config['MYSQL_USER'] = 'vito'
app.config['MYSQL_PASSWORD'] = '123'
app.config['MYSQL_DB'] = 'mokura'

#enable this when run in local
# app.config['MYSQL_HOST'] = 'localhost'
# app.config['MYSQL_USER'] = 'root'
# app.config['MYSQL_PASSWORD'] = ''
# app.config['MYSQL_DB'] = 'mokura'

mysql = MySQL(app)
   
default_value = "null"

#login user CLEAR
@app.route('/users/login/',methods=['POST'])
def login_user():

    email = request.form.get('email', default_value)
    password = request.form.get('password', default_value)

    cur = mysql.connection.cursor()
    cur.execute("SELECT EXISTS (SELECT * FROM users WHERE email = %s AND password = %s)",(email,password))
    data = cur.fetchall()

    if(data[0][0]==0):
        fail_response= jsonify({"error": True, "message": "login failed", "loginResult":{"email":email}})
        return fail_response
    else:
        cur.execute("SELECT * FROM users WHERE email = %s AND password = %s",(email,password))
        user = cur.fetchall()

        return jsonify({"error": False,"message": "login success!", "loginResult":{"idUser":user[0][0],"email":user[0][1],"username":user[0][2],"password":user[0][3]}})
    
#register user CLEAR
@app.route('/users/register/',methods=['POST'])
def register_user():
    response = ()
    email = request.form.get('email', default_value)
    username = request.form.get('username', default_value)
    password = request.form.get('password', default_value)
    cur = mysql.connection.cursor()

    cur.execute("SELECT * FROM users WHERE email = %s OR username = %s",(email,username))
    record = cur.fetchall()
    if len(record) > 0:
        #user sudah ada
        response = record

        return jsonify({"error": "true", "message": "email or username already registered","data":{"id_user":response[0][0],"email":response[0][1],"username":response[0][2],"password":response[0][3]}})
    else:
        cur.execute("INSERT INTO users (email,username,password) VALUES (%s,%s,%s)",(email,username,password))
        mysql.connection.commit()

        cur.execute("SELECT * FROM users WHERE email = %s",(email,))
        record2 = cur.fetchall()
        response = record2
    cur.close()

    return jsonify({"error": "false", "message": "user registered!", "data":{"id_user":response[0][0],"email":response[0][1],"username":response[0][2],"password":response[0][3]}})

#save hardware to db CLEAR
@app.route('/mokura/register',methods=['POST'])
def register_mokura():
    response = ()

    hardware_name = request.form.get('hardware_name', default_value)
    hardware_serial = request.form.get('hardware_serial', default_value)
    cur = mysql.connection.cursor()

    cur.execute("SELECT * FROM mokura WHERE hardware_serial = %s AND hardware_name = %s ",(hardware_serial,hardware_name))
    record = cur.fetchall()

    if len(record) > 0:
        cur.execute("SELECT * FROM mokura WHERE hardware_serial = %s ",(hardware_serial,))
        response = cur.fetchall()
    else:
        cur.execute("INSERT IGNORE INTO mokura (hardware_serial,hardware_name) VALUES(%s,%s)",(hardware_serial,hardware_name))
        mysql.connection.commit()
        cur.execute("SELECT * FROM mokura WHERE hardware_serial = %s AND hardware_name = %s ",(hardware_serial,hardware_name))
        response = cur.fetchall()

    cur.close()

    return jsonify({"error": "false","id_hardware":response[0][0],"hardware_serial":response[0][1],"hardware_name":response[0][2],"message": "hardware registered!"})

#logging
# @app.route('/logging/datalist', methods=['POST'])
# def insert_datalist():
#     if request.method == 'POST':
#         datas = request.get_json()
#         # parsing data list
#         for data in datas:
#             id_user = data["id_user"]
#             id_hardware = data["id_hardware"]
#             time_stamp = data["time_stamp"]
#             lat = data["lat"]
#             lon = data["lon"]
#             compass = data["compass"]
#             speed = data["speed"]
#             rpm = data["rpm"]
#             battery = data["battery"]
#             duty_cycle = data["duty_cycle"]   
#             cur = mysql.connection.cursor()
#             cur.execute("INSERT INTO logging (time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle,id_user,id_hardware) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",(time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle,id_user,id_hardware))
#             mysql.connection.commit()
#             cur.close()
#         return jsonify({'message': 'data inserted!'})

# @app.route('/logging/datalist',methods=['GET'])
# def get_datalist():
#     id_user = request.form.get('id_user', default_value)

#     if id_user != "null":
            
#         listlogging = []
#         cur = mysql.connection.cursor()
#         cur.execute("SELECT * FROM logging WHERE id_user = %s",(id_user,))
#         datas = cur.fetchall()

#         for data in datas:
#             id_logging = data[0]
#             id_hardware = data[1]
#             id_user = data[2]
#             time_stamp = data[3]
#             speed = data[4]
#             rpm = data[5]
#             battery = data[6]
#             lat = data[7]
#             lon = data[8]
#             compass = data[9]
#             duty_cycle = data[10]

#             listlogging.append({
#                 "id_logging": id_logging,
#                 "id_hardware": id_hardware,
#                 "id_user": id_user,
#                 "data":{
#                     "time_stamp":time_stamp,
#                     "speed":speed,
#                     "rpm":rpm,
#                     "battery":battery,
#                     "lat":lat,
#                     "lon":lon,
#                     "compass":compass,
#                     "duty_cycle":duty_cycle,
#                 }})
#         cur.close()
#         return jsonify({'logging': listlogging})


@app.route('/logging/datalist', methods=['GET', 'POST'])
def datalist():
    if request.method == 'POST':
        datas = request.get_json()
        # parsing data list
        for data in datas:
            cur = mysql.connection.cursor()
            cur.execute("INSERT INTO logging (id_user, id_hardware, time_stamp, lat, lon, compass, speed, rpm, battery, duty_cycle) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", tuple(data.values()))
            mysql.connection.commit()
            cur.close()
        return jsonify({'message': 'data inserted!'})
    elif request.method == 'GET':
        id_user = request.form.get('id_user')

        if id_user:
            cur = mysql.connection.cursor()
            cur.execute("SELECT * FROM logging WHERE id_user = %s", (id_user,))
            datas = cur.fetchall()

            listlogging = []
            for data in datas:
                id_logging, id_hardware, id_user, time_stamp, speed, rpm, battery, lat, lon, compass, duty_cycle = data
                listlogging.append({
                    "id_logging": id_logging,
                    "id_hardware": id_hardware,
                    "id_user": id_user,
                    "data":{
                        "time_stamp":time_stamp,
                        "speed":speed,
                        "rpm":rpm,
                        "battery":battery,
                        "lat":lat,
                        "lon":lon,
                        "compass":compass,
                        "duty_cycle":duty_cycle,
                    }})
            cur.close()
            return jsonify({'logging': listlogging})

#get user CLEAR
@app.route('/users',methods=['GET'])
def get_users():
    response = ()
    email = request.form.get('email', default_value)
    username = request.form.get('username', default_value)
    cur = mysql.connection.cursor()

    if email != "null":
        #get user by email
        cur.execute("SELECT * FROM users WHERE email = %s",(email,))
        response = cur.fetchall()
        return jsonify({"error":"false","message":"success","data":{"id_user":response[0][0],"email":response[0][1],"username":response[0][2],"password":response[0][3]}})

    elif username != "null":
        #get user by username
        cur.execute("SELECT * FROM users WHERE username = %s",(username,))
        response = cur.fetchall()
        return jsonify({"error":"false","message":"success","data":{"id_user":response[0][0],"email":response[0][1],"username":response[0][2],"password":response[0][3]}})
    else:
        listuser = []
        #get all user
        cur.execute("SELECT * FROM users")
        users = cur.fetchall()
        for user in users:
            id = user[0]
            email = user[1]
            username = user[2]
            password = user[3]
            listuser.append({"error":"false","message":"success","data":{"id_user":id,"email":email,"username":username,"password":password}})
    return jsonify(listuser)

#get all hardware from db CLEAR
@app.route('/mokura',methods=['GET'])
def get_all_mokura():
    response = ()
    hardware_name = request.form.get('hardware_name', default_value)
    cur = mysql.connection.cursor()

    if hardware_name != "null" :
        cur.execute("SELECT * FROM mokura WHERE hardware_name = %s",(hardware_name,))
        response = cur.fetchall()
        return jsonify({"error": "false", "message":"success","data":{"id_hardware":response[0][0],"hardware_serial":response[0][1],"hardware_name": response[0][2]}})
    else:
        listhardware = []
        cur.execute("SELECT * FROM mokura")
        response = cur.fetchall()
        for hardware in response:
            id = hardware[0]
            serial = hardware[1]
            name = hardware[2]
            listhardware.append({"error": "false", "message":"success","data":{"id_hardware":id,"hardware_serial":serial,"hardware_name":name}})
        
        return jsonify(listhardware)
    # return jsonify({"error": "false","id_hardware":response[0][0],"hardware_serial":response[0][1],"hardware_name": response[0][2]})


@app.route('/server/time', methods=['GET'])
def get_servertime():
    current_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S.%f')
    return jsonify({'server_time': current_time})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=6969,debug=True)

# commit dipake ketika modify table seperti insert
