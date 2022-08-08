package com.example.speedometer


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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


}