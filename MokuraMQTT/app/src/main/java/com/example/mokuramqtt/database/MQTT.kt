package com.example.mokuramqtt.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "mqtt")
@Parcelize
data class MQTT(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idMQTT")
    val idHTTP: Int? = null,

    @ColumnInfo(name = "packetSize")
    val packetSize: String,

    @ColumnInfo(name = "sentTimeStamp")
    val sentTimeStamp: String,

    @ColumnInfo(name = "receivedTimeStamp")
    val receivedTimeStamp: String,

    //receivedTimeStamp - sentTimeStamp
    @ColumnInfo(name = "timeDifference")
    val timeDifference: String,

    //serverTimeStamp - sentTimeStamp
    @ColumnInfo(name = "timeTransmission")
    val timeTransmission: String,

) : Parcelable

