package com.src.mathmind.service

import android.util.Log
import com.src.mathmind.models.ScoreModel
import com.src.mathmind.models.UserModel
import com.src.mathmind.utils.IdlingTool
import com.src.mathmind.utils.LogTag
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallService private constructor(private val idlingTool: IdlingTool){
    val retrofitData = RetrofitClient.getClient()
    val mathMindService: MathMindService = retrofitData.create(MathMindService::class.java)

    companion object {
        private var instance: CallService? = null

        // Method to obtain the singleton instance
        fun getInstance(idlingTool: IdlingTool): CallService {
            // If the instance is null, create a new one
            if (instance == null) {
                instance = CallService(idlingTool)
            }
            return instance!!
        }

    }

    fun validateUserName(userName: String, callback: (Boolean?) -> Unit) {
        Log.d(LogTag.CALL_SERVICE,"User validation call fired")
        val call = mathMindService.validateUser(userName)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    Log.d(LogTag.CALL_SERVICE, "Call succeeded: response ${response.body()}")
                    callback(response.body())
                } else {
                    Log.e(LogTag.CALL_SERVICE, "Unsuccessful response ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e(LogTag.CALL_SERVICE, "User validation failed: ${t.message}")
                callback(null)
            }
        })
        idlingTool.setIdleState(true)
    }

    fun saveUser(user: UserModel, callback: (String?) -> Unit ) {
        Log.d(LogTag.CALL_SERVICE,"Save User call fired")

        val call = mathMindService.saveUser(user)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d(LogTag.CALL_SERVICE, "Call succeeded: response ${response.body()}")
                    callback(response.body())

                } else {
                    Log.e(LogTag.CALL_SERVICE, "Unsuccessful response ${response.message()}")
                    // Log the entire response body for more details
                    callback(null)
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(LogTag.CALL_SERVICE, "Failure ${t.message}")
                t.printStackTrace() // Log the full stack trace
                callback(null)

            }
        })
        idlingTool.setIdleState(true)
    }

    fun getUser(userName: String, callback: (ServiceResponse<UserModel>) -> Unit) {
        Log.d(LogTag.CALL_SERVICE,"Get user call fired")
        val call = mathMindService.getUser(userName)
        call.enqueue(object : Callback<ServiceResponse<UserModel>> {
            override fun onResponse(call: Call<ServiceResponse<UserModel>>, response: Response<ServiceResponse<UserModel>>) {
                if (response.isSuccessful) {
                    val serviceResponse = response.body()
                    if (serviceResponse != null && serviceResponse.isSuccess) {
                        callback(serviceResponse)

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
            }
        })
        idlingTool.setIdleState(true)
    }

    fun getScoreBoardList(callback: (List<ScoreModel>?) -> Unit) {
        Log.d(LogTag.CALL_SERVICE,"Get Score call fired")

        val call = mathMindService.getTopScoreList()
        call.enqueue(object : Callback<List<ScoreModel>> {
            override fun onResponse(
                call: Call<List<ScoreModel>>,
                response: Response<List<ScoreModel>>,
            ) {
                if(response.isSuccessful) {
                    callback(response.body())
                }else{
                    Log.e(LogTag.CALL_SERVICE, "Score list call failure $response")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<ScoreModel>>, t: Throwable) {
                Log.e(LogTag.CALL_SERVICE, "Score list call failed $t")
                callback(null)
            }
        })
        idlingTool.setIdleState(true)
    }

    fun saveScore(scoreModel: ScoreModel){
        Log.d(LogTag.CALL_SERVICE,"Save Score call fired")

        val call = mathMindService.saveScore(scoreModel)
        call.enqueue(object: Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(!response.isSuccessful)
                    Log.e(LogTag.CALL_SERVICE, "Score post call failed ${response.body()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(LogTag.CALL_SERVICE, "Score post call failed $t")
                throw t
            }

        })
        idlingTool.setIdleState(true)
    }
}