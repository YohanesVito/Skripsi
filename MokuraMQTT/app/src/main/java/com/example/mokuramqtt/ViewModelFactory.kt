package com.example.mokuramqtt

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.di.Injection
import com.example.mokuramqtt.viewmodel.*

class ViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MonitorViewModel::class.java) -> {
                MonitorViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(TestingViewModel::class.java) -> {
                TestingViewModel(Injection.provideRepository2(context)) as T
            }
            modelClass.isAssignableFrom(DetailsHttpViewModel::class.java) -> {
                DetailsHttpViewModel(Injection.provideRepository3(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}