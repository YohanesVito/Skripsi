package com.example.mokuramqtt.remote.response

import com.google.gson.annotations.SerializedName

data class InsertResponse(

	@field:SerializedName("message")
	val message: String
)
