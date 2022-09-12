package com.example.monitoringwheelchair.gyroscope

data class GyroscopeData(
    val rotation: Float,
    val rotationX: Float,
    val rotationY: Float,
    val translationX: Float,
    val translationY: Float,
    val color: Int,
    val text: String
)