package com.example.mokuramqtt.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "hardware")
@Parcelize
data class Hardware(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idHardware")
    val idHardware: Int? = null,

    @ColumnInfo(name = "hardwareSerial")
    val hardwareSerial: String,

    @ColumnInfo(name = "hardwareName")
    val hardwareName: String,

) : Parcelable
