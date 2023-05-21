package com.example.mokuramqtt.repository

import androidx.lifecycle.LiveData
import com.example.mokuramqtt.database.*


class HTTPRepository(
    private val mokuraDatabase: MokuraDatabase,
) {

    fun getRow(): LiveData<List<HTTP>> {
        return mokuraDatabase.httpDao().getAllData()
    }

}

