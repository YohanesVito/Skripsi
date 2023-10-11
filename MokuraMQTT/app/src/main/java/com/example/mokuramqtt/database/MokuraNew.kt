package com.example.mokuramqtt.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "mokura_new")
@Parcelize
data class MokuraNew(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: String? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "speed")
    val speed: String,

    @ColumnInfo(name = "throtle")
    val throtle: String,

    @ColumnInfo(name = "battery")
    val battery: String,

    @ColumnInfo(name = "lat")
    val lat: String,

    @ColumnInfo(name = "long")
    val long: String,

    @ColumnInfo(name = "status")
    val status: Int,

    ) : Parcelable
