package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.repository.MokuraRepository
import com.example.mokuramqtt.utils.CSVLogger2

class MonitorViewModel(private val mokuraRepository: MokuraRepository): ViewModel() {

    val valArrayLogging = ArrayList<Mokura>()

    val arrayLogging: MutableLiveData<ArrayList<Mokura>> by lazy {
        MutableLiveData<ArrayList<Mokura>>()
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

    fun uploadData()= mokuraRepository.postLogging(valArrayLogging)

    fun saveHardware(hardwareSerial: String, hardwareName: String) = mokuraRepository.postHardware(hardwareSerial,hardwareName)

    val dataCompass: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    fun writeToCSV() {
        arrayLogging.value?.let { list ->
            for (mokura in list) {
                // Code to write Mokura object to CSV file
                CSVLogger2().logToCsv(mokura)
            }
        }
    }

}