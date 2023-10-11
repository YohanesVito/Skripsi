package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.MokuraNew
import com.example.mokuramqtt.model.LoginModel
import com.example.mokuramqtt.repository.MokuraRepository

class DummyViewModel(private val mokuraRepository: MokuraRepository): ViewModel(){

    fun getUser() = mokuraRepository.getUser()

    fun sendDummy(dummyData: MokuraNew) = mokuraRepository.sendDummy(dummyData)

}