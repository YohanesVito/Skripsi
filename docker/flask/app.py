from flask import Flask, jsonify, request
from flask_mysqldb import MySQL

app = Flask(__name__)

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'vito'
app.config['MYSQL_PASSWORD'] = '123'
app.config['MYSQL_DB'] = 'mokura'

mysql = MySQL(app)
   

@app.route('/login/',methods=['POST'])
def login_user():

    
    default_value = "null"
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

@app.route('/register/',methods=['POST'])
def register_user():

    default_value = "null"
    email = request.form.get('email', default_value)
    username = request.form.get('username', default_value)
    password = request.form.get('password', default_value)

    cur = mysql.connection.cursor()
    cur.execute("INSERT INTO users (email,username,password) VALUES (%s,%s,%s)",(email,username,password))
    mysql.connection.commit()
    cur.close()

    return jsonify({"error": "false", "message": "user registered!"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=6969,debug=True)

