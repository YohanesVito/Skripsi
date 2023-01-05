package com.example.mokuramqtt.di

import android.content.Context
import com.example.mokuramqtt.remote.retrofit.ApiConfig
import com.example.mokuramqtt.database.MokuraDatabase
import com.example.mokuramqtt.model.UserPreference
import com.example.mokuramqtt.repository.MokuraRepository
import com.example.mokuramqtt.ui.main.dataStore

object Injection {
    fun provideRepository(context: Context): MokuraRepository {
        val database = MokuraDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return MokuraRepository(database, apiService, userPreference)
    }
}