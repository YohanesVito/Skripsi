package com.example.mokuramqtt.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.repository.MokuraRepository

class HomeViewModel(private val mokuraRepository: MokuraRepository): ViewModel(){
    fun insert(user: Mokura) {
        mokuraRepository.insert(user)
    }
    fun getUser() = mokuraRepository.getUser()
//    fun update(note: Note) {
//        mUserRepository.update(note)
//    }
//    fun delete(note: Note) {
//        mUserRepository.delete(note)
//    }
}