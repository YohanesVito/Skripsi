package com.example.mokuramqtt.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.mokuramqtt.database.*
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.model.UserPreference
import com.example.mokuramqtt.remote.response.InsertResponse
import com.example.mokuramqtt.remote.response.LoginResponse
import com.example.mokuramqtt.remote.response.RegisterResponse
import com.example.mokuramqtt.remote.retrofit.ApiService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MokuraRepository(
    private val mokuraDatabase: MokuraDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private var hardwareSerial: String = ""
    private var email: String = ""

    fun insertUser(mUser: User) {
        email = mUser.email
        mokuraDatabase.userDao().insertUser(mUser)
    }

    fun insertHardware(mHardwareSerial: String) {
        hardwareSerial = mHardwareSerial
        val mHardware = Hardware(hardwareSerial = mHardwareSerial)
        mokuraDatabase.hardwareDao().insertHardware(mHardware)
    }

    fun insertMokura(mMokura: Mokura) {
        mMokura.idUser = getUserId()
        mMokura.idHardware = getHardwareId()
        mokuraDatabase.mokuraDao().insertMokura(mMokura)
    }

    private fun getUserId(): Int{
        val rowUser = mokuraDatabase.userDao().getUserId(email)
        Log.d("email ",email)
        Log.d("rowUser ",rowUser.toString())
        return rowUser?.idUser ?: 0

    }

    private fun getHardwareId(): Int{
        val rowHardware = mokuraDatabase.hardwareDao().getHardwareId(hardwareSerial)

        return rowHardware?.idHardware ?: 0
    }


    fun getUser(): LiveData<UserModel> {
        return userPreference.getUser().asLiveData()
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
                        if (!responseBody.error) {
                            result.value = Result.Success(true)
                            val loginResult = responseBody.loginResult
                            val mUser = User(email = loginResult.email, username = loginResult.username, password = loginResult.password)
                            insertUser(mUser)
                            MainScope().launch {
                                userPreference.saveUser(
                                    name = loginResult.username,
                                    email = loginResult.email,
                                    password = loginResult.password
                                )
                                userPreference.login()
                            }
                            result.value = Result.Success(true)

                        } else {
                            result.value = Result.Error(responseBody.message)
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

    fun logout() {
        MainScope().launch {
            userPreference.logout()
        }
    }

    fun postMokura(hardware: String): LiveData<Result<Boolean>>{
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.postMokura(hardware).enqueue(object : Callback<InsertResponse> {
            override fun onResponse(
                call: Call<InsertResponse>,
                response: Response<InsertResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        insertHardware(hardware)
                        result.value = Result.Success(true)
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    fun postLogging(arrayLogging: ArrayList<Mokura>): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.sendDataList(arrayLogging).enqueue(object : Callback<InsertResponse> {
            override fun onResponse(
                call: Call<InsertResponse>,
                response: Response<InsertResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        result.value = Result.Success(true)
                    }
                }else {
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertResponse>, t: Throwable) {
                result.value = Result.Error("Can't Connect Retrofit")
            }
        })
        return result
    }

    fun saveUser(email: String, name: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        apiService.register(email, name, password).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        result.value = Result.Success(true)
                        MainScope().launch {
                            userPreference.saveUser(email, name, password)
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

