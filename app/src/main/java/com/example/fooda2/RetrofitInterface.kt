package com.example.fooda2

import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RetrofitInterface {
        // 프로필 이미지 보내기
    @Multipart
    @POST("/analyzeDiet")
    fun post_diet_image(
        @Part("userId") userId: String,
        @Part imageFile : MultipartBody.Part): Call<String>

    object RetrofitClient {
        private var instance : Retrofit? = null
        private val gson = GsonBuilder().setLenient().create()

        fun getInstnace() : Retrofit {
            if(instance == null){
                instance = Retrofit.Builder()
                    .baseUrl("http://18.219.20.247:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return instance!!
        }
    }
}