package com.example.mokuramqtt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MokuraDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: Mokura)
}