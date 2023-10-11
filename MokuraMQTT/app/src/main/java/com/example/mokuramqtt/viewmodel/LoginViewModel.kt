package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.model.LoginModel
import com.example.mokuramqtt.repository.MokuraRepository

class LoginViewModel(private val mokuraRepository: MokuraRepository): ViewModel(){
    fun getUser() = mokuraRepository.getUser()

    fun login(email: String, password: String) = mokuraRepository.login(email, password)

    fun loginNew(loginModel: LoginModel) = mokuraRepository.loginNew(loginModel)


}