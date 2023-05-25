package com.example.mokuramqtt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MokuraMQTTDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMokura(mokura: MokuraMQTT)
}