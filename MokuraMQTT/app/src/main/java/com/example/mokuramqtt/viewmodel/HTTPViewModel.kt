package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.HTTP
import com.example.mokuramqtt.repository.HTTPRepository

class HTTPViewModel(httpRepository: HTTPRepository): ViewModel(){

    val allData: LiveData<List<HTTP>> = httpRepository.getRow()

}