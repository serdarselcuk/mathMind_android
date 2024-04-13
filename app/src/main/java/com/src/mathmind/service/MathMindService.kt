package com.src.mathmind.service

import com.src.mathmind.models.ScoreModel
import com.src.mathmind.models.UserModel
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.58:8080/"
    val gson = GsonBuilder().setLenient().create()

    val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(RequestInterceptor)
        .build()

    fun getClient(): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    @GET("/topScoreList")
    fun getTopScoreList() : Call<List<ScoreModel>>
    @POST("/score")
    fun putScore(@Body user: ScoreModel):Call<ServiceResponse<String>>

    @POST("/user/validate")
    fun validateUser(@Query("param_1") param1:String) : Call<Boolean>

    @POST("/user/save")
    fun saveUser(@Body user: UserModel):Call<String>

    @GET("/user")
    fun getUser(@Query("param_1") param_1: String):Call<ServiceResponse<UserModel>>

}