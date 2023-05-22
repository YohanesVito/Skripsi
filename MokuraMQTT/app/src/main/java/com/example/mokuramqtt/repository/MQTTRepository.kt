package com.example.mokuramqtt.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.mokuramqtt.database.*
import com.example.mokuramqtt.model.*
import com.example.mokuramqtt.utils.MQTTService
import com.example.mokuramqtt.viewmodel.MQTTViewModel

class MQTTRepository(
    private val mokuraDatabase: MokuraDatabase,
    private val userPreference: UserPreference){

    private val mqttService =  MQTTService()

    fun connect(context: Context){
        mqttService.connect(context)
    }

    fun disconnect(){
        mqttService.disconnect()
    }

    fun subscribe(topic: String,mqttViewModel: MQTTViewModel){
        mqttService.subscribe(topic,mqttViewModel = mqttViewModel)
    }

    fun unsubscribe(topic: String){
        mqttService.unsubscribe(topic)
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

    fun publishArrayLogging(mArrayList: ArrayList<Mokura>){
        mqttService.publishArrayLogging(mArrayList)
    }

    fun getUser(): LiveData<UserModel> {
        return userPreference.getUser().asLiveData()
    }

    //DB
    fun getRow(): LiveData<List<MQTT>> {
        return mokuraDatabase.mqttDao().getAllData()
    }

    fun insertMQTT(mMQTT: MQTT) {
        mokuraDatabase.mqttDao().insertMQTT(mMQTT)
    }
}


