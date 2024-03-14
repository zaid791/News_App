package com.example.newsapp.Model

data class MyData(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)