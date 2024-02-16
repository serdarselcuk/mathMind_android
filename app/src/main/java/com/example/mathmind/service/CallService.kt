package com.example.mathmind.service

import com.example.mathmind.ui.scoreBoard.ScoreBoardData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallService{
    val retrofitData = RetrofitClient.getClient()
    val scoreApi: MathMindService = retrofitData.create(MathMindService::class.java)
    fun calling() {

       var call = scoreApi.getScore()
        call.enqueue(object : Callback<ScoreBoardData> {
            override fun onResponse(
                call: Call<ScoreBoardData>,
                response: Response<ScoreBoardData>
            ) {
                if(!response.isSuccessful){
                    println(response.message())
                }else{
                    println(response.message())
                }
            }

            override fun onFailure(call: Call<ScoreBoardData>, t: Throwable) {
                println(t.localizedMessage)
            }

        })

    }


}