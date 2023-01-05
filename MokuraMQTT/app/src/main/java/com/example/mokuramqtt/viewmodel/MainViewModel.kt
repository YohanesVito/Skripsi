package com.example.mokuramqtt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.repository.MokuraRepository

class MainViewModel(private val mokuraRepository: MokuraRepository): ViewModel() {
    fun getUser() = mokuraRepository.getUser()
}