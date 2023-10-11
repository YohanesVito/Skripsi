package com.example.mokuramqtt.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.Constants.Companion.REQUEST_EXTERNAL_STORAGE_PERMISSION
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityHomeBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.model.Result
import com.example.mokuramqtt.ui.testing.DetailsHTTPActivity
import com.example.mokuramqtt.ui.authentication.LoginActivity
import com.example.mokuramqtt.ui.monitoring.PairActivity
import com.example.mokuramqtt.ui.test.SendDummyActivity
import com.example.mokuramqtt.ui.testing.DetailsMQTTActivity
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


    }

    private fun setupAction() {
        homeViewModel.getUser().observe(this) { user ->
            if (user.email != "") {
                binding.tvUser.text = getString(R.string.welcome, user.name)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

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

        binding.btStart.setOnClickListener {
            startActivity(Intent(this,PairActivity::class.java))
        }

        binding.btLoggingHttp.setOnClickListener {
            startActivity(Intent(this, DetailsHTTPActivity::class.java))
        }

        binding.btLoggingMqtt.setOnClickListener {
            startActivity(Intent(this, DetailsMQTTActivity::class.java))
        }

        binding.btSendDummy.setOnClickListener {
            startActivity(Intent(this, SendDummyActivity::class.java))
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


    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, so we can log data now
                return
            } else {
                // Permission has been denied
                Toast.makeText(this, "External storage permission has been denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}