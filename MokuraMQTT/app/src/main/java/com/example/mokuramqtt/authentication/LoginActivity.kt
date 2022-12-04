package com.example.mokuramqtt.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mokuramqtt.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}