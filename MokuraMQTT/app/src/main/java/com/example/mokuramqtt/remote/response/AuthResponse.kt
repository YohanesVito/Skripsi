package com.example.mokuramqtt.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AuthResponse(

	@field:SerializedName("body")
	val body: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null
) : Parcelable
