
from email.policy import default
from flask import Flask, jsonify, request
from flask_mysqldb import MySQL

app = Flask(__name__)

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = ''
app.config['MYSQL_DB'] = 'mokura'

mysql = MySQL(app)
   
# @app.route('/user',methods=['GET'])
# def get_all_users():
#     return ''

# @app.route('/user/<user_id>',methods=['GET'])
# def get_one_users():
#     return ''

@app.route('/data/',methods=['POST'])
def insert_data():
    default_value = "null"
    
    time_stamp = request.form.get('time_stamp', default_value)
    speed = request.form.get('speed', default_value)
    rpm = request.form.get('rpm', default_value)
    battery = request.form.get('battery', default_value)
    cur = mysql.connection.cursor()

    cur.execute("INSERT INTO data (time_stamp,speed,rpm,battery) VALUES (%s,%s,%s,%s)",(time_stamp,speed,rpm,battery))
    mysql.connection.commit()
    cur.close()

    return jsonify({'message': 'data inserted!'})

if __name__ == "__main__":
    app.run(host="192.168.100.8", port=5000, debug=True)

