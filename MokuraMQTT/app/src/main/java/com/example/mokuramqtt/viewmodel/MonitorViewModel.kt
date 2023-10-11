package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.database.MokuraNew
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.MokuraRepository
import kotlinx.coroutines.launch

class MonitorViewModel(private val mokuraRepository: MokuraRepository): ViewModel() {

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


    fun uploadData2(data: MokuraNew) = mokuraRepository.postLogging2(data)

    fun getUser()= mokuraRepository.getUser()


    fun saveHardware(hardwareSerial: String, hardwareName: String) = mokuraRepository.postHardware(hardwareSerial,hardwareName)

    fun saveDataHTTP(mUser: UserModel, mMokura: Mokura) {
        val idUser = mUser.id_user
        val idHardware = mUser.id_hardware
        val newMokura = Mokura(
            id_user = idUser,
            id_hardware = idHardware,
            time_stamp = mMokura.time_stamp,
            speed = mMokura.speed,
            rpm = mMokura.rpm,
            battery = mMokura.battery,
            duty_cycle = mMokura.duty_cycle,
            compass = mMokura.compass,
            lat = mMokura.lat,
            lon = mMokura.lon,
        )
        viewModelScope.launch {
            mokuraRepository.insertMokura(newMokura)
        }

    }

}