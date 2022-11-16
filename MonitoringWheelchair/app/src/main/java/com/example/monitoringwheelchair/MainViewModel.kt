package com.example.monitoringwheelchair

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monitoringwheelchair.gyroscope.GyroscopeData
import com.example.monitoringwheelchair.logging.Data

class MainViewModel:ViewModel() {

    var arrayDataModel = ArrayList<Data>()

    val currentArrayData : MutableLiveData<ArrayList<Data>> by lazy{
        MutableLiveData<ArrayList<Data>>()
    }

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





}