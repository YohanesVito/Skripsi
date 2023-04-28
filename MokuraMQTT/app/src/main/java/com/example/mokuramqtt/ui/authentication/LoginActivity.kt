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
import com.example.mokuramqtt.databinding.ActivityLoginBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.model.Result
import com.example.mokuramqtt.ui.home.HomeActivity
import com.example.mokuramqtt.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()

        binding.btLogin.setOnClickListener {
            login()
        }

        binding.btRegister.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    private fun login(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        loginViewModel.login(email, password).observe(this) {
            when(it) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Berhasil Masuk", Toast.LENGTH_SHORT).show()
                    val intentToHome = Intent(this, HomeActivity::class.java)
                    startActivity(intentToHome)
                    finish()
                }
                is Result.Error -> Toast.makeText(this@LoginActivity, "Gagal Masuk", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

}