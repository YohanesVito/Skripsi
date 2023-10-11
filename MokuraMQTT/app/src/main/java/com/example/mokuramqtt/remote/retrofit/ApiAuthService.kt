package com.example.mokuramqtt.remote.retrofit

import com.example.mokuramqtt.model.LoginModel
import com.example.mokuramqtt.model.RegisterModel
import com.example.mokuramqtt.remote.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiAuthService {

    @POST("login")
    fun loginNew(@Body body: LoginModel): Call<AuthResponse>

    @POST("register")
    fun registerNew(@Body body: RegisterModel): Call<AuthResponse>
}
