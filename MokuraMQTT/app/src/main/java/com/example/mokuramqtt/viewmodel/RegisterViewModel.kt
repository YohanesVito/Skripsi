package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.model.RegisterModel
import com.example.mokuramqtt.repository.MokuraRepository

class RegisterViewModel(private val mokuraRepository: MokuraRepository): ViewModel() {
    fun register(email: String,username: String, password: String) = mokuraRepository.saveUser(email,username,password)

    fun registerNew(registerModel: RegisterModel) = mokuraRepository.registerNew(registerModel)

}