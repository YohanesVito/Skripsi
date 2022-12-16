package com.example.mokuramqtt.ui.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityLoginBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.Result
import com.example.mokuramqtt.ui.home.HomeActivity
import com.example.mokuramqtt.viewmodel.LoginViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        binding.btLogin.setOnClickListener {
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

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intentToMain = Intent(this@LoginActivity, MainActivity::class.java)
//        startActivity(intentToMain)
//        finish()
//    }
}