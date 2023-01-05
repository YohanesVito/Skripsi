package com.example.mokuramqtt.ui.authentication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityRegisterBinding
import com.example.mokuramqtt.repository.Result
import com.example.mokuramqtt.ui.home.HomeActivity

import com.example.mokuramqtt.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupViewModel()

        binding.btRegister.setOnClickListener{
            register()
        }
    }

    private fun register() {
        val email = binding.etEmail.text.toString()
        val username = binding.etFullname.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (password != confirmPassword){
            binding.etConfirmPassword.error = "Password tidak sama"
        }
        else{
            registerViewModel.register(email, username, password).observe(this) {
                when(it) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                        val intentToHome = Intent(this, HomeActivity::class.java)
                        startActivity(intentToHome)
                        finish()
                    }
                    is Result.Error -> Toast.makeText(this@RegisterActivity, "Gagal Daftar", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[RegisterViewModel::class.java]
    }

    private fun setupView() {
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