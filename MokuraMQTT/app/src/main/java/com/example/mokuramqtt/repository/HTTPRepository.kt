package com.example.mokuramqtt.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mokuramqtt.database.*
import com.example.mokuramqtt.model.UserPreference
import com.example.mokuramqtt.remote.retrofit.ApiService


class HTTPRepository(
    private val mokuraDatabase: MokuraDatabase,
) {

    fun getRow(): LiveData<List<HTTP>> {
        Log.d("getRow",mokuraDatabase.httpDao().getAllData().value.toString())
        return mokuraDatabase.httpDao().getAllData()
    }

}

