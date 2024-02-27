package com.example.mathmind.service

import com.example.mathmind.models.ScoreBoardModel
import com.example.mathmind.models.UserModel
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


}