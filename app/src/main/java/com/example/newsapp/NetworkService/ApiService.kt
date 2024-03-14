package com.example.newsapp.NetworkService

import com.example.newsapp.Model.MyData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    //3fac5516fb864f41b722f69c0c4b1ceb
    @GET("v2/top-headlines?country=in&apiKey=3fac5516fb864f41b722f69c0c4b1ceb")
    fun getHeadlines(
//        @Query("country")
//        countryCode: String = "in",
//        @Query("page")
//        pageNumber: Int = 1,
//        @Query("apiKey")
//        apiKey: String = "bdc75872219a4a3b9e0430d833ca356d"
    ): Call<MyData>

    @GET("v2/everything")
    fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = "3fac5516fb864f41b722f69c0c4b1ceb"
    ): Call<MyData>

    @GET("v2/everything?q=india&apiKey=3fac5516fb864f41b722f69c0c4b1ceb")
    fun getEverything(): Call<MyData>
}