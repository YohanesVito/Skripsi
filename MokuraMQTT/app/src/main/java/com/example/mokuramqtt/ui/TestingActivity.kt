package com.example.mokuramqtt.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityHomeBinding
import com.example.mokuramqtt.databinding.ActivityTestingBinding
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.viewmodel.MonitorViewModel
import com.example.mokuramqtt.viewmodel.TestingViewModel

class TestingActivity : AppCompatActivity() {
    private lateinit var testingViewModel: TestingViewModel
    private lateinit var binding: ActivityTestingBinding
    private lateinit var mUser: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        binding = ActivityTestingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupAction() {

        //connect
        binding.btConnect.setOnClickListener{
            testingViewModel.connect(this)
        }

        binding.btSubscribe.setOnClickListener {
            testingViewModel.subscribe("mokura/user_response")
        }

        //publish user
        binding.btPublish.setOnClickListener {
            testingViewModel.publishUser(mUser)
        }
    }

    private fun setupViewModel(){
        testingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[TestingViewModel::class.java]

        testingViewModel.getUser().observe(this){
            mUser = it
        }
    }

}