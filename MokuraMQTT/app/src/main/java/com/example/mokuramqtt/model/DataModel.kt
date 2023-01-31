package com.example.mokuramqtt.model

data class DataModel(
    val idLogging: String? = null,
    val idHardware: String? = null,
    val idUser: String? = null,
    val timeStamp: String? = null,
    val speed: String? = null,
    val rpm: String? = null,
    val battery: String? = null,
    val dutyCycle: String? = null,
    val compass: String? = null,
    val lat: String? = null,
    val lon: String? = null
)
