package com.example.mathmind.service

import com.example.mathmind.models.ScoreBoardModel
import com.example.mathmind.ui.scoreBoard.UserData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.58:8080/"

    val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(RequestInterceptor)
        .build()
    fun getClient(): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

object RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        println("Outgoing request to ${request.url()}")
        return chain.proceed(request)
    }
}

interface MathMindService {

    @GET("/score")
    fun getScore() : Call<ScoreBoardModel>

    @POST("/user/validate")
    fun validateUser(@Query("param_1") param1:String) : Call<Boolean>

}