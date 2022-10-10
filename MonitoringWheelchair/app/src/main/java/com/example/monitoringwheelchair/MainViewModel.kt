package com.example.monitoringwheelchair

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monitoringwheelchair.gyroscope.GyroscopeData

class MainViewModel:ViewModel() {

    val dataLocation: MutableLiveData<Array<Double>> by lazy {
        MutableLiveData<Array<Double>>()
    }

    val dataBluetooth: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val dataGyroscope: MutableLiveData<GyroscopeData> by lazy {
        MutableLiveData<GyroscopeData>()
    }

    val dataCompass: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val dataDummy: MutableLiveData<Map<String,Int>> by lazy{
        MutableLiveData<Map<String,Int>>()
    }

}