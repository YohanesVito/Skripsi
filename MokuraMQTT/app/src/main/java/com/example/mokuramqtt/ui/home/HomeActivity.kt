package com.example.mokuramqtt.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityHomeBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.Result
import com.example.mokuramqtt.ui.authentication.LoginActivity
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
//        homeViewModel.getUser().observe(this) { user ->
//            if (user.isLogin) {
//                binding.tvUser.text = getString(R.string.welcome, user.name)
//            } else {
//                startActivity(Intent(this, PairActivity::class.java))
//                finish()
//            }
//        }
        binding.btLogout.setOnClickListener {
            homeViewModel.logoutUser().observe(this){
                when(it) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@HomeActivity, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
                        val intentToHome = Intent(this, LoginActivity::class.java)
                        startActivity(intentToHome)
                        finish()
                    }
                    is Result.Error -> Toast.makeText(this@HomeActivity, "Gagal Keluar", Toast.LENGTH_SHORT).show()
                }
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