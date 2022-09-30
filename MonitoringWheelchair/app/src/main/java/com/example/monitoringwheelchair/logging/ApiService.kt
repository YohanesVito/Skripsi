package com.example.monitoringwheelchair.logging

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//    @POST("data")
//    @Headers("Authorization: token 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")
//    fun getUser(
//        @Query("q") username: String
//    ): Call<SearchUserResponse>

    @FormUrlEncoded
    @POST("data")
    fun sendData(
        @Field("time_stamp") time_stamp: String,
        @Field("speed") speed: String,
        @Field("rpm") rpm: String,
        @Field("battery") battery: String,
    ): Call<Data>

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
