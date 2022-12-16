package com.example.mokuramqtt

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.di.Injection
import com.example.mokuramqtt.viewmodel.HomeViewModel
import com.example.mokuramqtt.viewmodel.LoginViewModel
import com.example.mokuramqtt.viewmodel.MainViewModel
import com.example.mokuramqtt.viewmodel.RegisterViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
//            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
//                SplashScreenViewModel(Injection.provideRepository(context)) as T
//            }
//            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
//                MainViewModel(Injection.provideRepository(context)) as T
//            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
//            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
//                HomeViewModel(Injection.provideRepository(context)) as T
//            }
//            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
//                RegisterViewModel(Injection.provideRepository(context)) as T
//            }
//            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
//                AddStoryViewModel(Injection.provideRepository(context)) as T
//            }
//            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
//                MapsViewModel(Injection.provideRepository(context)) as T
//            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}