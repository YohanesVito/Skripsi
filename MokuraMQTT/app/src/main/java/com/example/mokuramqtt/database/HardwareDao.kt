package com.example.mokuramqtt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface HardwareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHardware(hardware: Hardware)

//    @Query("SELECT * FROM hardware WHERE hardwareSerial = :hardwareSerial")
//    fun getHardwareId(hardwareSerial: String): Hardware?
}