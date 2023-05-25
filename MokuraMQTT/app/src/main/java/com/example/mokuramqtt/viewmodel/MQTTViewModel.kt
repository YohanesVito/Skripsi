package com.example.mokuramqtt.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mokuramqtt.database.Hardware
import com.example.mokuramqtt.database.MQTT
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.database.MokuraMQTT
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.MQTTRepository
import kotlinx.coroutines.launch

class MQTTViewModel(private val mqttRepository: MQTTRepository): ViewModel(){

    val allData: LiveData<List<MQTT>> = mqttRepository.getRow()

    fun connect(context: Context) = mqttRepository.connect(context)

    fun disconnect() = mqttRepository.disconnect()

    fun subscribe(topic: String, mqttViewModel: MQTTViewModel) = mqttRepository.subscribe(topic, mqttViewModel = mqttViewModel)

    fun unsubscribe(topic: String) = mqttRepository.unsubscribe(topic)
    fun publishArrayLogging(mArray: ArrayList<Mokura>) = mqttRepository.publishArrayLogging(mArray)

    fun publishUser(user: UserModel) = mqttRepository.publishUser(user)

    fun getUser() = mqttRepository.getUser()

    fun insertMQTT(mMQTT: MQTT)= mqttRepository.insertMQTT(mMQTT)

    fun saveDataMQTT(mUser: UserModel, mMokura: MokuraMQTT) {
        val idUser = mUser.id_user
        val idHardware = mUser.id_hardware
        val newMokura = MokuraMQTT(
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
            mqttRepository.insertMokura(newMokura)
        }
    }

    fun publishHardware(hardware: Hardware) = mqttRepository.publishHardware(hardware)

    fun publishLogging(mokura: Mokura) = mqttRepository.publishLogging(mokura)
}