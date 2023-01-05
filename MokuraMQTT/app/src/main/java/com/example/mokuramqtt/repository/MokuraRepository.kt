package com.example.mokuramqtt.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.database.MokuraDatabase
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.model.UserPreference
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

    fun insert(mokura: Mokura) {

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
                            MainScope().launch {
                                userPreference.login()
                            }
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

