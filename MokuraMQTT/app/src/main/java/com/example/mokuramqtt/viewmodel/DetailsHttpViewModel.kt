package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mokuramqtt.database.HTTP
import com.example.mokuramqtt.repository.HTTPRepository
import kotlinx.coroutines.launch

class DetailsHttpViewModel(private val httpRepository: HTTPRepository): ViewModel(){

    val allData: LiveData<List<HTTP>> = httpRepository.getRow()

    private val _rowData = MutableLiveData<List<HTTP>>()
    val rowData: LiveData<List<HTTP>> = _rowData

    fun getHTTPLatency(){
        _rowData.value = httpRepository.getRow().value
    }

}