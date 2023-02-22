package com.example.mokuramqtt.remote.response

import com.google.gson.annotations.SerializedName

data class InsertLoggingResponse(

	@field:SerializedName("message")
	val message: String

)
