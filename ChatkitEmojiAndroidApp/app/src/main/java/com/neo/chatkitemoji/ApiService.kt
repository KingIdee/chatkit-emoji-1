package com.neo.chatkitemoji

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/users")
    fun createUser(@Body login: RequestBody): Call<String>

    @POST("/updateEmoji")
    fun updateEmoji(@Body login: RequestBody): Call<String>

}