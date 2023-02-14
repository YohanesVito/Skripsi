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

@app.route('/users/register/',methods=['POST'])
def register_user():

    email = request.form.get('email', default_value)
    username = request.form.get('username', default_value)
    password = request.form.get('password', default_value)

    cur = mysql.connection.cursor()
    cur.execute("INSERT INTO users (email,username,password) VALUES (%s,%s,%s)",(email,username,password))
    mysql.connection.commit()
    cur.close()

    return jsonify({"error": "false", "message": "user registered!"})

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
    mysql.connection.commit()
    data = cur.fetchall()

    cur.close()

    return jsonify(data)

@app.route('/mokura/register',methods=['POST'])
def register_mokura():

    hardware = request.form.get('hardware', default_value)
    cur = mysql.connection.cursor()
    cur.execute("INSERT INTO mokura(hardware_serial) VALUES(%s) WHERE NOT EXISTS (SELECT * FROM mokura WHERE hardware_serial = %s)",(hardware,hardware))
    mysql.connection.commit()
    cur.close()

    return jsonify({"error": "false", "message": "hardware registered!"})

@app.route('/mokura',methods=['GET'])
def get_all_mokura():
    hardware_name = request.form.get('hardware_name', default_value,type=str)
    
    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM mokura WHERE hardware_name = %s",(hardware_name))
    data = cur.fetchall()
  
    return jsonify(data)

@app.route('/mokuras/',methods=['GET'])
def get_mokura_by_user_email():

    email = request.form.get('email', default_value)
    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM mokuras WHERE email = %s",(email))
    data = cur.fetchall()

    if(data[0][0]==0):
        fail_response= jsonify({"error": True, "message": "email not found"})
        return fail_response
    else:
        cur.execute("SELECT * FROM mokuras WHERE email = %s",(email))
        data = cur.fetchall()

        return jsonify({"error": False,"message": "success", "loginResult":{"data":data[0][0]}})

@app.route('/datalist/',methods=['POST'])
def insert_datalist():
    datas = request.get_json()
    
    # parsing data list
    for data in datas:
        time_stamp = data["timeStamp"]
        lat = data["lat"]
        lon = data["lon"]
        compass = data["compass"]
        speed = data["speed"]
        rpm = data["rpm"]
        battery = data["battery"]
        duty_cycle = data["dutyCycle"]   

        cur = mysql.connection.cursor()
        cur.execute("INSERT INTO logging (time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",(time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle))
        mysql.connection.commit()
        cur.close()

    return jsonify({'message': 'data inserted!'})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=6969,debug=True)

