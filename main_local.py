from flask import Flask, jsonify, request
from flask_mysqldb import MySQL

app = Flask(__name__)

# app.config['MYSQL_HOST'] = '3.89.63.60'
# app.config['MYSQL_USER'] = 'vito'
# app.config['MYSQL_PASSWORD'] = '123'
# app.config['MYSQL_DB'] = 'mokura'

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = ''
app.config['MYSQL_DB'] = 'mokura'

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
    
#login user UNCLEAR, respon belum benar
@app.route('/users/register/',methods=['POST'])
def register_user():
    response = ()
    email = request.form.get('email', default_value)
    username = request.form.get('username', default_value)
    password = request.form.get('password', default_value)
    cur = mysql.connection.cursor()

    cur.execute("SELECT * FROM users WHERE email = %s",(email,))
    record = cur.fetchall()
    if len(record) > 0:
        #user sudah ada
        response = record
        return jsonify({"error": "true", "message": "email already registered","data":{"id_user":response[0][0],"email":response[0][1],"username":response[0][2],"password":response[0][3]}})
    else:
        cur.execute("INSERT INTO users (email,username,password) VALUES (%s,%s,%s)",(email,username,password))
        mysql.connection.commit()

        cur.execute("SELECT * FROM users WHERE email = %s",(email,))
        response = cur.fetchall()
    cur.close()

    return jsonify({"error": "false", "message": "user registered!", "data":{"id_user":response[0][0],"username":response[0][1],"email":response[0][2],"password":response[0][3]}})

@app.route('/users/',methods=['GET'])
def get_users():

    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM users")
    data = cur.fetchall()
  
    return jsonify(data)

@app.route('/users/',methods=['POST'])
def get_a_user_by_username():

    username = request.form.get('username', default_value)

    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM users WHERE username = %s",(username,))
    data = cur.fetchall()

    cur.close()

    return jsonify(data)

# save hardware to db CLEAR, not duplicate anymore
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

    return jsonify({"error": "false","id_hardware":response[0][0],"hardware_serial":response[0][1],"message": "hardware registered!"})

# get all hardware from db UNCLEAR, kurang return banyak data
@app.route('/mokura',methods=['GET'])
def get_all_mokura():
    response = ()

    hardware_name = request.form.get('hardware_name', default_value)
    cur = mysql.connection.cursor()

    if hardware_name != "null" :
        cur.execute("SELECT * FROM mokura WHERE hardware_name = %s",(hardware_name,))
        response = cur.fetchall()
    else:
        cur.execute("SELECT * FROM mokura")
        response = cur.fetchall()

    # return jsonify({"error": "false","id_hardware":response[0][0],"hardware_serial":response[0][1],"hardware_name": response[0][2]})

    return jsonify(response)

@app.route('/logging/datalist/',methods=['POST'])
def insert_datalist():
    datas = request.get_json()
    
    # parsing data list
    for data in datas:
        id_user = data["id_user"]
        id_hardware = data["id_hardware"]
        time_stamp = data["timeStamp"]
        lat = data["lat"]
        lon = data["lon"]
        compass = data["compass"]
        speed = data["speed"]
        rpm = data["rpm"]
        battery = data["battery"]
        duty_cycle = data["dutyCycle"]   

        cur = mysql.connection.cursor()
        cur.execute("INSERT INTO logging (time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle) VALUES (%s,%s,%s,%s,%s,%s,%s,%s) WHERE id_user = %s AND id_hardware = %s",(time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle,id_user,id_hardware))
        mysql.connection.commit()
        cur.close()

    return jsonify({'message': 'data inserted!'})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=6969,debug=True)

# commit dipake ketika modify table seperti insert