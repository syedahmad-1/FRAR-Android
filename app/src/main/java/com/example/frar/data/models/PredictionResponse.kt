package com.example.frar.data.models

import com.google.gson.annotations.SerializedName


data class Recommendation(
    val recommendation: RecommendationType,
    val result: FaceShapeResult
)

data class RecommendationType(
    @SerializedName("male")
    val maleRecommendation: List<ItemRecommendation>,
    @SerializedName("female")
    val femaleRecommendation: List<ItemRecommendation>,
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
