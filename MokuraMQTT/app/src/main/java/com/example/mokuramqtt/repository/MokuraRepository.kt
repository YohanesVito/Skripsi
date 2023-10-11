package com.example.mokuramqtt.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.room.withTransaction
import com.example.mokuramqtt.database.*
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.model.*
import com.example.mokuramqtt.remote.response.*
import com.example.mokuramqtt.remote.retrofit.ApiAuthService
import com.example.mokuramqtt.remote.retrofit.ApiService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MokuraRepository(
    private val mokuraDatabase: MokuraDatabase,
    private val apiService: ApiService,
    private val apiAuth: ApiAuthService,
    private val userPreference: UserPreference
) {
    suspend fun insertHTTP(mHTTP: HTTP) {
        mokuraDatabase.withTransaction {
            mokuraDatabase.httpDao().insertHTTP(mHTTP)
        }
    }

    fun insertUser(mUser: User) {
        mokuraDatabase.userDao().insertUser(mUser)
    }

    fun insertHardware(mHardware: Hardware) {
        mokuraDatabase.hardwareDao().insertHardware(mHardware)
    }

    suspend fun insertMokura(mMokura: Mokura) {
        mokuraDatabase.withTransaction {
            mokuraDatabase.mokuraDao().insertMokura(mMokura)
        }
    }


    fun getUser(): LiveData<UserModel> {
        return userPreference.getUser().asLiveData()
    }

    fun logoutUser(): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        MainScope().launch {
            userPreference.logout()
        }
        result.value = Result.Success(true)
        return result
    }

    fun sendDummy(dummyData: MokuraNew): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.sendData(dummyData).enqueue(object : Callback<InsertLoggingNewResponse>{
            override fun onResponse(
                call: Call<InsertLoggingNewResponse>,
                response: Response<InsertLoggingNewResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        Log.d("SendDummy","Sent: $responseBody")
                        result.value = Result.Success(true)
                    }
                }else {
                    Log.d("SendDummy","Failed: ${response.message()}")
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertLoggingNewResponse>, t: Throwable) {
                Log.d("SendDummy","Failed: ${t.message}")

                result.value = Result.Error("Can't Connect Retrofit")

            }
        })
        return result
    }

    fun login(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.login(email, password).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        if (!responseBody.error!!) {
                            result.value = Result.Success(true)
                            val loginResult = responseBody.loginResult

                            //insert to Room
                            val mUser = User(
                                idUser = loginResult!!.idUser,
                                email = loginResult.email,
                                username = loginResult.username,
                                password = loginResult.password)
                            insertUser(mUser)

                            //insert to SP
                            MainScope().launch {
                                userPreference.saveUser(
                                    idUser = loginResult.idUser.toString(),
                                    name = loginResult.username,
                                    email = loginResult.email,
                                    password = loginResult.password
                                )
                                userPreference.login()
                            }

                            result.value = Result.Success(true)

                        } else {
                            result.value = responseBody.message?.let { Result.Error(it) }
                        }
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    fun loginNew(loginModel: LoginModel): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiAuth.loginNew(loginModel).enqueue(object : Callback<AuthResponse>{
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        val statusCode = responseBody.statusCode
                        if (statusCode != null) {
                            if(statusCode == 200){
                                //insert to SP
                                MainScope().launch {
                                    userPreference.saveUser(
                                        idUser = "Random ID",
                                        name = loginModel.email.toString(),
                                        email = loginModel.email.toString(),
                                        password = loginModel.password.toString()
                                    )
                                    userPreference.login()
                                }
                                result.value = Result.Success(true)
                            }
                            else if(statusCode == 203){
                                Log.d("LoginMessage",responseBody.body.toString())
                                result.value = Result.Error(responseBody.body.toString())

                            }
                        }
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    fun registerNew(registerModel: RegisterModel): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiAuth.registerNew(registerModel).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        val statusCode = responseBody.statusCode
                        if (statusCode != null) {
                            if(statusCode == 200){
                                //insert to SP
                                MainScope().launch {
                                    userPreference.saveUser(
                                        idUser = "Random ID",
                                        name = registerModel.email.toString(),
                                        email = registerModel.email.toString(),
                                        password = registerModel.password.toString()
                                    )
                                    userPreference.login()
                                }
                                result.value = Result.Success(true)
                            }
                            else if(statusCode == 202){
                                Log.d("RegisterMessage",responseBody.body.toString())
                                result.value = Result.Error(responseBody.body.toString())

                            }
                        }
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    //http
    fun postHardware(hardwareSerial: String, hardwareName: String): LiveData<Result<Boolean>>{
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.postMokura(hardwareSerial,hardwareName).enqueue(object : Callback<InsertHardwareResponse> {
            override fun onResponse(
                call: Call<InsertHardwareResponse>,
                response: Response<InsertHardwareResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("Hardware", responseBody.toString())
                    if (responseBody != null){

                        //insert to room
                        val mHardware = Hardware(
                            hardwareName = responseBody.hardwareName!!,
                            hardwareSerial = responseBody.hardwareSerial!!,
                            idHardware = responseBody.idHardware!!
                        )
                        insertHardware(mHardware)

                        //insert to SP
                        MainScope().launch {
                            userPreference.saveIdHardware(responseBody.idHardware.toString())
                        }

                        result.value = Result.Success(true)
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertHardwareResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    //http
    fun postLogging(arrayLogging: ArrayList<Mokura>): LiveData<Result<Boolean>> {

        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading

        //get startTimeStamp
        val timeStart = DateHelper.getCurrentDate()

        apiService.sendDataList(arrayLogging).enqueue(object : Callback<InsertLoggingResponse> {
            override fun onResponse(
                call: Call<InsertLoggingResponse>,
                response: Response<InsertLoggingResponse>
            ) {
                if (response.isSuccessful) {
                    //get endTimeStamp
                    val timeEnd = DateHelper.getCurrentDate()

                    val responseBody = response.body()
                    if (responseBody != null){

                        result.value = Result.Success(true)
                        val timeDiff = DateHelper.calculateTimeDifference(timeStart, timeEnd)
                        Log.d("timeDiff",timeDiff.toString())

                        val timeTrans = DateHelper.calculateTimeDifference(timeStart,responseBody.serverTimeStr!!)
                        Log.d("timeTrans",timeTrans.toString())

                       //insert to http dao
                        val mHTTP = HTTP(
                            packetSize = responseBody.packetSize.toString(),
                            sentTimeStamp = timeStart,
                            receivedTimeStamp = timeEnd,
                            timeDifference = timeDiff.toString(),
                            timeTransmission = timeTrans.toString()
                        )

                        MainScope().launch {
                            insertHTTP(mHTTP)
                        }

                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertLoggingResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }
    fun postLogging2(logging: MokuraNew): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.sendData(logging).enqueue(object : Callback<InsertLoggingNewResponse> {
            override fun onResponse(
                call: Call<InsertLoggingNewResponse>,
                response: Response<InsertLoggingNewResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        Log.d("postLogging2","berhasil: $responseBody")

                        result.value = Result.Success(true)
                    }
                }else {
                    Log.d("postLogging2","gagal")
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertLoggingNewResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    fun postLoggingNew(arrayLogging: ArrayList<Mokura>): LiveData<Result<Boolean>> {

        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading

        //get startTimeStamp
        val timeStart = DateHelper.getCurrentDate()

        apiService.sendDataListNew(arrayLogging).enqueue(object : Callback<InsertLoggingResponse> {
            override fun onResponse(
                call: Call<InsertLoggingResponse>,
                response: Response<InsertLoggingResponse>
            ) {
                if (response.isSuccessful) {
                    //get endTimeStamp
                    val timeEnd = DateHelper.getCurrentDate()

                    val responseBody = response.body()
                    if (responseBody != null){

                        result.value = Result.Success(true)
                        val timeDiff = DateHelper.calculateTimeDifference(timeStart, timeEnd)
                        Log.d("timeDiff",timeDiff.toString())

                        val timeTrans = DateHelper.calculateTimeDifference(timeStart,responseBody.serverTimeStr!!)
                        Log.d("timeTrans",timeTrans.toString())

                        //insert to http dao
                        val mHTTP = HTTP(
                            packetSize = responseBody.packetSize.toString(),
                            sentTimeStamp = timeStart,
                            receivedTimeStamp = timeEnd,
                            timeDifference = timeDiff.toString(),
                            timeTransmission = timeTrans.toString()
                        )

                        MainScope().launch {
                            insertHTTP(mHTTP)
                        }

                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertLoggingResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    //SP
    fun saveUser(email: String, name: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.register(email, name, password).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!.data
                    if (responseBody != null){
                        result.value = Result.Success(true)
                        MainScope().launch {
                            userPreference.saveUser(
                                idUser = responseBody.idUser.toString(),
                                email = responseBody.email,
                                name = responseBody.username,
                                password = responseBody.password)
                        }
                        result.value = Result.Success(true)
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

}

