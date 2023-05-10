class User:
    def __init__(self, id_user=None, email=None, username=None, password=None):
        self.id_user = id_user
        self.email = email
        self.username = username
        self.password = password
    
    def to_dict(self):
        return {
            "idUser": self.id_user,
            "email": self.email,
            "username": self.username,
            "password": self.password
        }
