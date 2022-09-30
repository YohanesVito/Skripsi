from enum import unique
from unicodedata import name
from flask import Flask, Request, jsonify, request
from flask_restful import Api, Resource
from flask_sqlalchemy import SQLAlchemy
import uuid
import os

app = Flask(__name__)
api = Api(app)

file_path = os.path.abspath(os.getcwd())+"\database.db"

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///'+file_path

app.config['SECRET_KEY'] = 'thisissecret'
# app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///mnt/c/Users/vitor/Documents/api_example/todo.db'

db = SQLAlchemy(app)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    public_id = db.Column(db.String(50), unique=True)
    name = db.Column(db.String(50))
    password = db.Column(db.String(80))
    admin = db.Column(db.Boolean)

class Todo(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String(50))
    complete = db.Column(db.Boolean)
    user_id = db.Column(db.Integer)
    
@app.route('/user',methods=['GET'])
def get_all_users():
    return ''

@app.route('/user/<user_id>',methods=['GET'])
def get_one_users():
    return ''

@app.route('/user/',methods=['POST'])
def create_user():
    data =request.get_json()
    new_user = User(public_id=str(uuid.uuid4()),name=data['name'],password=data['password'],admin=False)
    db.session.add(new_user)
    db.session.commit()
    return jsonify({'message': 'New user created!'})



# names = {"vito":{"name":"vito","age":19, "gender": "male"},
#         "rizki":{"age":22, "gender": "male"}
# }
# class HelloWorld(Resource):
#     def get(self,name):
#         return names[name]

#     def post(self):
#         return {"data": "Helloworld"}

# api.add_resource(HelloWorld,"/helloworld/<string:name>")

if __name__ == "__main__":
    app.run(debug=True)