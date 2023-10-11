package com.example.mokuramqtt.remote.retrofit

import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.remote.response.InsertHardwareResponse
import com.example.mokuramqtt.remote.response.InsertLoggingResponse
import com.example.mokuramqtt.remote.response.LoginResponse
import com.example.mokuramqtt.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<RegisterResponse>


    @POST("logging/datalist")
    fun sendDataList(@Body body: ArrayList<Mokura>): Call<InsertLoggingResponse>

    @FormUrlEncoded
    @POST("mokura/register")
    fun postMokura(
        @Field("hardware_serial") hardware_serial: String,
        @Field("hardware_name") hardware_name: String,
    ): Call<InsertHardwareResponse>

    @POST("data")
    fun sendDataListNew(@Body body: ArrayList<Mokura>): Call<InsertLoggingResponse>
}
