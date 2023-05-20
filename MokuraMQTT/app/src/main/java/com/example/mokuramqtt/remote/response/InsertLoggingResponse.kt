package com.example.mokuramqtt.remote.response

import com.google.gson.annotations.SerializedName

data class InsertLoggingResponse(

	@field:SerializedName("packet_size")
	val packetSize: Int? = null,

	@field:SerializedName("server_time_str")
	val serverTimeStr: String? = null,

	@field:SerializedName("server_time_int")
	val serverTimeInt: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)
