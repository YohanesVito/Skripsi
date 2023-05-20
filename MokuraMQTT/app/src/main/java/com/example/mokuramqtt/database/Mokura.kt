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
    @ColumnInfo(name = "id_logging")
    val id_logging: Int? = null,

    @ColumnInfo(name = "id_hardware")
    val id_hardware: String,

    @ColumnInfo(name = "id_user")
    val id_user: String,

    @ColumnInfo(name = "time_stamp")
    val time_stamp: String,

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

    @ColumnInfo(name = "duty_cycle")
    val duty_cycle: String,

    ) : Parcelable
