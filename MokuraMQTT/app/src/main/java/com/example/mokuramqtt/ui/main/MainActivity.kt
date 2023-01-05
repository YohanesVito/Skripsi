package com.example.mokuramqtt.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityMainBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.ui.authentication.LoginActivity
import com.example.mokuramqtt.ui.home.HomeActivity
import com.example.mokuramqtt.viewmodel.MainViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: UserModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        hideSystemUI()
        setupViewModel()
        setupAction()

    }

    private fun setupAction() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(user.isLogin) {
                    val intentToHome = Intent(this, HomeActivity::class.java)
                    startActivity(intentToHome)
                    finish()
                }else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            2000
        )
    }
    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}