package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.MokuraRepository

class MonitorViewModel(private val mokuraRepository: MokuraRepository): ViewModel() {

    private val valArrayLogging = ArrayList<Mokura>()

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

    val dataCompass: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    fun uploadData()= mokuraRepository.postLogging(valArrayLogging)

    fun getUser()= mokuraRepository.getUser()


    fun saveHardware(hardwareSerial: String, hardwareName: String) = mokuraRepository.postHardware(hardwareSerial,hardwareName)

    fun saveData(mUser: UserModel, mMokura: Mokura) {
        val idUser = mUser.id_user
        val idHardware = mUser.id_hardware
        val newMokura = Mokura(
            idUser = idUser.toInt(),
            idHardware = idHardware.toInt(),
            timeStamp = mMokura.timeStamp,
            speed = mMokura.speed,
            rpm = mMokura.rpm,
            battery = mMokura.battery,
            dutyCycle = mMokura.dutyCycle,
            compass = mMokura.compass,
            lat = mMokura.lat,
            lon = mMokura.lon,
        )
        mokuraRepository.insertMokura(newMokura)
    }

}