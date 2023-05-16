package com.example.mokuramqtt.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.mokuramqtt.database.Hardware
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.database.MokuraDatabase
import com.example.mokuramqtt.model.*
import com.example.mokuramqtt.utils.MQTTService

class MQTTRepository(
    private val mokuraDatabase: MokuraDatabase,
    private val userPreference: UserPreference){

    private val mqttService =  MQTTService()

    fun connect(context: Context){
        mqttService.connect(context)
    }

    fun publishUser(user: UserModel) {
        mqttService.publishUser(user)
    }

    fun publishHardware(hardware: Hardware){
        mqttService.publishHardware(hardware)
    }

    fun publishLogging(mokura: Mokura){
        mqttService.publishLogging(mokura)
    }

    fun getUser(): LiveData<UserModel> {
        return userPreference.getUser().asLiveData()
    }
}


