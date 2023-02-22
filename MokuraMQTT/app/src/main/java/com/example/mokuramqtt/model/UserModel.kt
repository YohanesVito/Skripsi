package com.example.mokuramqtt.model

data class UserModel(
    var id_user: String,
    var id_hardware: String,
    var name: String,
    var email: String,
    var password: String,
    var isLogin: Boolean
)
