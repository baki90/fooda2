package com.example.fooda2

import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RetrofitInterface {
        // 프로필 이미지 보내기
    @Multipart
    @POST("/analyzeDiet")
    fun post_diet_image(
        @Part("token") token: String,
        @Part imageFile : MultipartBody.Part): Call<Nutrient>

    @FormUrlEncoded
    @POST("/uploadDiet")
    fun post_diet_upload(@Field("email") email : String, @Field("food_id") food_id : Int, @Field("day") day: Int) :Call<Message>

    @GET("/totalDiet")
    fun get_total_diet(@Query("email") email : String, @Query("datetime") datetime : String) : Call<Nutrient>

    @GET("/totalDietList")
    fun get_total_diet_list(@Query("email") email : String, @Query("datetime") datetime : String) : Call<List<foodlist>>

    @GET("/analyzePerson")
    fun get_analyze_person(@Query("email") email : String) : Call<Analyze>
    @GET("/analyzeImage")
    fun get_analyze_image(@Query("email") email : String) : Call<ResponseBody>

    @GET("/analyzeTotalDiet")
    fun get_analyze_total(@Query("email") email : String) : Call<Total>

    @GET("/recipe")
    fun get_recipe(@Query("foodname") foodname : String) : Call<Recipe>

    @FormUrlEncoded
    @POST("/register")
    fun post_register(@Field("email") email : String, @Field("password") password: String, @Field("name") name: String,
    @Field("age") age: Int, @Field("height") height: Int, @Field("weight") weight: Int, @Field("sex") sex: Char) :Call<Message>

    @FormUrlEncoded
    @POST("/login")
    fun post_login(@Field("email") email : String, @Field("password") password : String) :Call<Message>


    object RetrofitClient {
        var token : String = "1"
        private var instance : Retrofit? = null
        private val gson = GsonBuilder().setLenient().create()

        fun getInstnace() : Retrofit {
            if(instance == null){
                instance = Retrofit.Builder()
                    .baseUrl("http://3.21.154.125:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return instance!!
        }
    }

    data class Message(
        val result : String,
        val msg : String,
        val token: String
    )

    data class Nutrient(
        val result : String,
        val food_id : Int,
        val food_name : String,
        val calories: Float,
        val carbohydrate: Float,
        val protein: Float,
        val fat: Float,
        val gram: Float,
        val kicho: Float
    )

    data class Analyze(
        val result: String,
        val carbohydrate: Float,
        val protein: Float,
        val fat: Float,
        val tan: Float,
        val dan: Float,
        val ji: Float,
        val style: String
    )

    data class foodlist(
        val cal :Float,
        val day : Int,
        val food_name: String,
        val gram: Float
    )

    data class Recipe(
        val result: String,
        val title: String,
        val level: String,
        val source: List<String>,
        val step: List<String>
    )
    data class Total(
        val style: String,
        val mon: Int,
        val lun: Int,
        val din: Int,
        val sna: Int
    )
}