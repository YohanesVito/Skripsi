package com.example.monitoringwheelchair

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monitoringwheelchair.gyroscope.GyroscopeData

class MainViewModel:ViewModel() {

    val latitude: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val longitude: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val currentRandom: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val dataGyroscope: MutableLiveData<GyroscopeData> by lazy {
        MutableLiveData<GyroscopeData>()
    }
}