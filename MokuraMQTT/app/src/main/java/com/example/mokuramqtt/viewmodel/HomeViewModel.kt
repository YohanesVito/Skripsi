package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.User
import com.example.mokuramqtt.repository.MokuraRepository

class HomeViewModel(private val mokuraRepository: MokuraRepository): ViewModel(){

    fun getUser() = mokuraRepository.getUser()

    fun logoutUser() = mokuraRepository.logoutUser()

}