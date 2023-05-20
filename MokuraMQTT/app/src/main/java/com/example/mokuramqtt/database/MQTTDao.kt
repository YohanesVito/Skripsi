package com.example.mokuramqtt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MQTTDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMQTT(mMQTT: MQTT)
}