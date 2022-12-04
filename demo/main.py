from flask import Flask, jsonify, request
from flask_mysqldb import MySQL

app = Flask(__name__)

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'vito'
app.config['MYSQL_PASSWORD'] = '123'
app.config['MYSQL_DB'] = 'mokura'

mysql = MySQL(app)
   
@app.route('/data/',methods=['GET'])
def get_all_datas():

    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM data")
    data = cur.fetchall()
  
    return jsonify(data)
    # return jsonify({
    #     "time_stamp" : data.time_stamp,
    #     "speed" : data.speed,
    #     "rpm" : data.rpm,
    #     "battery": data.battery,
    #     "lat" : data.lat,
    #     "lon" : data.lon,
    #     "compass" : data.compass,
    #     "duty_cycle" : data.duty_cycle,
    #     "time_stamp_database" : data.time_stamp.database
    # })

@app.route('/users/',methods=['GET'])
def get_all_users():

    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM users")
    data = cur.fetchall()
  
    return jsonify(data)

@app.route('/mokuras/',methods=['GET'])
def get_all_mokura():

    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM mokuras")
    data = cur.fetchall()
  
    return jsonify(data)

# @app.route('/user/<user_id>',methods=['GET'])
# def get_one_users():
#     return ''

@app.route('/datalist/',methods=['POST'])
def insert_datalist():
    datas = request.get_json()
    
    # parsing data list
    for data in datas:
        time_stamp = data["time_stamp"]
        lat = data["lat"]
        lon = data["lon"]
        compass = data["compass"]
        speed = data["speed"]
        rpm = data["rpm"]
        battery = data["battery"]
        duty_cycle = data["duty_cycle"]   

        cur = mysql.connection.cursor()
        cur.execute("INSERT INTO data (time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",(time_stamp,speed,rpm,battery,lat,lon,compass,duty_cycle))
        mysql.connection.commit()
        cur.close()

    return jsonify({'message': 'data inserted!'})


# @app.route('/data/',methods=['POST'])
# def insert_data():

# # kalau datanya bentuk json
#     # data = request.get_json()
    
#     # time_stamp = data["time_stamp"]
#     # speed = data["speed"]
#     # rpm = data["rpm"]
#     # battery = data["battery"]


# # kalau datanya bentuk x-www-form-urlencoded
#     default_value = "null"

#     time_stamp = request.form.get('time_stamp', default_value)
#     speed = request.form.get('speed', default_value)
#     rpm = request.form.get('rpm', default_value)
#     battery = request.form.get('battery', default_value)

#     cur = mysql.connection.cursor()
#     cur.execute("INSERT INTO data (time_stamp,speed,rpm,battery) VALUES (%s,%s,%s,%s)",(time_stamp,speed,rpm,battery))
#     mysql.connection.commit()
#     cur.close()

#     return jsonify({'message': 'data inserted!'})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=6969,debug=True)

