package com.example.mokuramqtt.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MQTTDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMQTT(mMQTT: MQTT)

    @Query("SELECT * FROM mqtt")
    fun getAllData(): LiveData<List<MQTT>>
}