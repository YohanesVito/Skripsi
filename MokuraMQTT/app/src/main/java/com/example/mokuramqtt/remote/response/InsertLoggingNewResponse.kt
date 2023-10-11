package com.example.mokuramqtt.remote.response

import com.google.gson.annotations.SerializedName

data class InsertLoggingNewResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("status_code")
	val statusCode: Int? = null
)
