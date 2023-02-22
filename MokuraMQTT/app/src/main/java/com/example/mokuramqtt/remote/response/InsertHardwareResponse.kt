package com.example.mokuramqtt.remote.response

import com.google.gson.annotations.SerializedName

data class InsertHardwareResponse(

	@field:SerializedName("hardware_name")
	val hardwareName: String? = null,

	@field:SerializedName("hardware_serial")
	val hardwareSerial: String? = null,

	@field:SerializedName("id_hardware")
	val idHardware: Int? = null,

	@field:SerializedName("error")
	val error: String? = null
)
