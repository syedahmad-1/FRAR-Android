package com.example.frar.data.models


data class Recommendation(
    val recommendation: List<ItemRecommendation>,
    val result: FaceShapeResult
)

data class ItemRecommendation(
    val gender: String,
    val name: String,
    val desc: String,
    val imgUrl: String,
    val title: String
)

data class FaceShapeResult(
    val Heart: Double,
    val Oblong: Double,
    val Oval: Double,
    val Round: Double,
    val Square: Double,
    val result: String
)
