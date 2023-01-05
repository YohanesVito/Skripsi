package com.example.mokuramqtt.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityHomeBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.ui.main.MainActivity
import com.example.mokuramqtt.ui.monitoring.PairActivity
import com.example.mokuramqtt.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupAction()

        binding.btStart.setOnClickListener {
            startActivity(Intent(this,PairActivity::class.java))
        }
    }

    private fun setupAction() {
        homeViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                binding.tvUser.text = getString(R.string.welcome, user.name)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupViewModel() {
        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[HomeViewModel::class.java]

        homeViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }
}