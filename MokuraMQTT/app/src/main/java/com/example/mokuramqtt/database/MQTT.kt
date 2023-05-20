package com.example.mokuramqtt.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "MQTT")
@Parcelize
data class MQTT(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idMQTT")
    val idHTTP: Int? = null,

    @ColumnInfo(name = "idLogging")
    val idLogging: String,

    @ColumnInfo(name = "packetSize")
    val packetSize: String,

    @ColumnInfo(name = "sentTimeStamp")
    val sentTimeStamp: String,

    @ColumnInfo(name = "receivedTimeStamp")
    val receivedTimeStamp: String,

    @ColumnInfo(name = "timeDifference")
    val timeDifference: String,

) : Parcelable

