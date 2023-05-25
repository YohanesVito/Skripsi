package com.example.mokuramqtt.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HTTPDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHTTP(mHTTP: HTTP)

    @Query("SELECT * FROM http")
    fun getAllData(): LiveData<List<HTTP>>
}