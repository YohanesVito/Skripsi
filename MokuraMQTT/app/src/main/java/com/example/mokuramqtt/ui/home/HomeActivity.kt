package com.example.mokuramqtt.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mokuramqtt.R
import com.example.mokuramqtt.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

//        val model = ViewModelProvider(this).get(HomeViewModel::class.java)
//        model.users?.observe(this, users -> {
//            // Ketika ada data users, maka UI secara otomatis terupdate.
//        })
    }
}