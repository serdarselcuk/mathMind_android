package com.src.mathmind.service

import com.src.mathmind.models.Password
import com.src.mathmind.models.ScoreBoardModel
import com.src.mathmind.models.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallService{
    val retrofitData = RetrofitClient.getClient()
    val mathMindService: MathMindService = retrofitData.create(MathMindService::class.java)

    fun calling() {

       val call = mathMindService.getScore()
        call.enqueue(object : Callback<ScoreBoardModel> {
            override fun onResponse(
                call: Call<ScoreBoardModel>,
                response: Response<ScoreBoardModel>
            ) {
                if(!response.isSuccessful){
                    println(response.message())
                }else{
                    println(response.message())
                }
            }

            override fun onFailure(call: Call<ScoreBoardModel>, t: Throwable) {
                println(t.localizedMessage)
            }

        })

    }

    fun validateUserName(userName: String, callback: (Boolean?) -> Unit) {

        val call = mathMindService.validateUser(userName)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    println("call succeeded: response ${response.body()}")
                    callback(response.body())
                } else {
                    println("Unsuccessful response ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                println("Failure ${t.message}")
                callback(null)
            }
        })
    }

    fun saveUser(user: UserModel, callback: (String?) -> Unit) {

        val call = mathMindService.saveUser(user)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    println("call succeeded: response ${response.body()}")
                    callback(response.body())
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
            }
        })
    }

    fun savePassword(password: Password, callback: (String?) -> Unit) {

        val call = mathMindService.savePassword(password)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    println("call succeeded: response ${response.body()}")
                    callback(response.body())
                } else {
                    println("Unsuccessful response ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                println("Failure ${t.message}")
                callback(null)
            }
        })
    }


}