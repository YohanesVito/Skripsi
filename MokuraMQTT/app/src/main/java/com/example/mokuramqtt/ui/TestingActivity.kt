package com.example.mokuramqtt.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityTestingBinding
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.viewmodel.MQTTViewModel

class TestingActivity : AppCompatActivity() {
    private lateinit var mqttViewModel: MQTTViewModel
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
            mqttViewModel.connect(this)
        }

        binding.btSubscribe.setOnClickListener {
            mqttViewModel.subscribe("mokura/user_response", mqttViewModel = mqttViewModel)
        }

        //publish user
        binding.btPublish.setOnClickListener {
            val timestamp = DateHelper.getCurrentDate()
            Log.d("TimeStamp android",timestamp)
            mqttViewModel.publishUser(mUser)
        }
    }

    private fun setupViewModel(){
        mqttViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MQTTViewModel::class.java]

        mqttViewModel.getUser().observe(this){
            mUser = it
        }
    }

}