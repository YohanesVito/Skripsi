package com.example.mokuramqtt.remote.retrofit

import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.remote.response.InsertResponse
import com.example.mokuramqtt.remote.response.LoginResponse
import com.example.mokuramqtt.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//    @POST("data")
//    @Headers("Authorization: token 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")
//    fun getUser(
//        @Query("q") username: String
//    ): Call<SearchUserResponse>

//    @FormUrlEncoded
//    @POST("data")
//    fun sendData(
//        @Field("time_stamp") time_stamp: String,
//        @Field("speed") speed: String,
//        @Field("rpm") rpm: String,
//        @Field("battery") battery: String,
//        @Field("duty_cycle") duty_cycle: String,
//        @Field("compass") compass: String,
//        @Field("lat") lat: String,
//        @Field("lon") lon: String,
//    ): Call<DataModel>

    @FormUrlEncoded
    @POST("login")
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

    @Headers("Content-Type: application/json")
    @POST("datalist")
    fun sendDataList(@Body body: ArrayList<Mokura>): Call<InsertResponse>

    @FormUrlEncoded
    @POST("hardware")
    fun postMokura(
        @Field("hardware") hardware: String,
    ): Call<InsertResponse>

//
//    @Headers("Content-Type: application/json")
//    @POST("datalist")
//    fun sendDataList(@Body body: ArrayList<DataModel>): Call<DataModel>

//    @GET("users/{username}")
//    @Headers("Authorization: token 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")
//    fun getDetailUser(
//        @Path("username") username: String
//    ): Call<DetailUserResponse>
//
//    @GET("users/{username}/followers")
//    @Headers("Authorization: token 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")
//    fun getUserFollowers(
//        @Path("username") username: String
//    ): Call<ArrayList<ListFollowersResponseItem>>
//
//    @GET("users/{username}/following")
//    @Headers("Authorization: token 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")
//    fun getUserFollowing(
//        @Path("username") username: String
//    ): Call<ArrayList<ListFollowingResponseItem>>
}
