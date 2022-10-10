package com.example.monitoringwheelchair.logging

data class Data(
    val timeStamp: String? = null,
    val speed: String? = null,
    val rpm: String? = null,
    val battery: String? = null,
    val dutyCycle: String? = null,
    val compass: String? = null,
    val lat: String? = null,
    val lon: String? = null
)
