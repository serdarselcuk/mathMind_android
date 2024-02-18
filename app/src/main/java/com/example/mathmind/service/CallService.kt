package com.example.mathmind.service

import com.example.mathmind.models.ScoreBoardModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallService{
    val retrofitData = RetrofitClient.getClient()
    val scoreApi: MathMindService = retrofitData.create(MathMindService::class.java)
    fun calling() {

       val call = scoreApi.getScore()
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


}