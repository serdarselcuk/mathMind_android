package com.src.mathmind.service

import android.util.Log
import androidx.annotation.Nullable
import androidx.test.espresso.IdlingResource
import com.src.mathmind.models.UserModel
import com.src.mathmind.utils.IdlingTool
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallService {
    val retrofitData = RetrofitClient.getClient()
    val mathMindService: MathMindService = retrofitData.create(MathMindService::class.java)

    fun validateUserName(userName: String,  idlingResource: IdlingTool?, callback: (Boolean?) -> Unit) {

        idlingResource?.setIdleState(false)

        val call = mathMindService.validateUser(userName)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    println("call succeeded: response ${response.body()}")
                    callback(response.body())
                    idlingResource?.setIdleState(true)
                } else {
                    println("Unsuccessful response ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                println("Failure ${t.message}")
                callback(null)
                idlingResource?.setIdleState(true)
            }
        })
    }

    fun saveUser(user: UserModel,  idlingResource: IdlingTool?, callback: (String?) -> Unit ) {

        idlingResource?.setIdleState(false)

        val call = mathMindService.saveUser(user)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    println("call succeeded: response ${response.body()}")
                    callback(response.body())
                    idlingResource?.setIdleState(true)
                } else {
                    println("Unsuccessful response ${response.message()}")
                    // Log the entire response body for more details
                    println("Response body: ${response.errorBody()?.string()}")
                    callback(null)
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                println("Failure ${t.message}")
                t.printStackTrace() // Log the full stack trace
                callback(null)
                idlingResource?.setIdleState(true)
            }
        })
    }


    fun getUser(userName: String,  idlingResource: IdlingTool?, callback: (ServiceResponse<UserModel>) -> Unit) {
        idlingResource?.setIdleState(false)
        val call = mathMindService.getUser(userName)
        call.enqueue(object : Callback<ServiceResponse<UserModel>> {
            override fun onResponse(call: Call<ServiceResponse<UserModel>>, response: Response<ServiceResponse<UserModel>>) {
                if (response.isSuccessful) {
                    val serviceResponse = response.body()
                    if (serviceResponse != null && serviceResponse.isSuccess) {
                        callback(serviceResponse)
                        idlingResource?.setIdleState(true)
                    } else {
                        Log.e(LogTag.CALL_SERVICE, "Unsuccessful response: ${serviceResponse?.errorMessage}")
                        callback(ServiceResponse("Error occurred"))
                    }
                } else {
                    Log.e(LogTag.CALL_SERVICE, "Unsuccessful response ${response.message()}")
                    callback(ServiceResponse("Error occurred"))
                }
            }

            override fun onFailure(call: Call<ServiceResponse<UserModel>>, t: Throwable) {
                Log.e(LogTag.CALL_SERVICE, t.printStackTrace().toString())
                callback(ServiceResponse("Error occurred"))

                idlingResource?.setIdleState(true)
            }
        })
    }
}