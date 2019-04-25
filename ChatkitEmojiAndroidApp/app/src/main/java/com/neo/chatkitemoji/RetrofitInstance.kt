package com.neo.chatkitemoji

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(
                GsonConverterFactory.create(
                GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder()
                    .build())
            .build()
            .create(ApiService::class.java)
    }

}