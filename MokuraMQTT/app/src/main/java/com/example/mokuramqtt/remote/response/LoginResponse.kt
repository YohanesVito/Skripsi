package com.example.mokuramqtt.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class LoginResponse(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class LoginResult(

	@field:SerializedName("username")
	val username: String,

	@field:SerializedName("email")
	val email: String,

) : Parcelable
