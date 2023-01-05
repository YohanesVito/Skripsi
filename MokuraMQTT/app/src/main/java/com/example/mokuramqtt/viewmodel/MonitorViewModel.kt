package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.model.DataModel
import com.example.mokuramqtt.repository.MokuraRepository

class MonitorViewModel(private val mokuraRepository: MokuraRepository): ViewModel() {
    var arrayDataModel = ArrayList<DataModel>()

    val currentArrayData : MutableLiveData<ArrayList<DataModel>> by lazy{
        MutableLiveData<ArrayList<DataModel>>()
    }

    val dataLocation: MutableLiveData<Array<Double>> by lazy {
        MutableLiveData<Array<Double>>()
    }

    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val dataBluetooth: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

//    val dataGyroscope: MutableLiveData<GyroscopeData> by lazy {
//        MutableLiveData<GyroscopeData>()
//    }

    val dataCompass: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }
}