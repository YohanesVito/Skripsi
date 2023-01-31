package com.example.mokuramqtt.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "mokura")
@Parcelize
data class Mokura(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idLogging")
    val idLogging: Int? = null,

    @ColumnInfo(name = "idHardware")
    var idHardware: Int? = null,

    @ColumnInfo(name = "idUser")
    var idUser: Int? = null,

    @ColumnInfo(name = "timeStamp")
    val timeStamp: String,

    @ColumnInfo(name = "speed")
    val speed: String,

    @ColumnInfo(name = "rpm")
    val rpm: String,

    @ColumnInfo(name = "battery")
    val battery: String,

    @ColumnInfo(name = "lat")
    val lat: String,

    @ColumnInfo(name = "lon")
    val lon: String,

    @ColumnInfo(name = "compass")
    val compass: String,

    @ColumnInfo(name = "dutyCycle")
    val dutyCycle: String,

) : Parcelable
