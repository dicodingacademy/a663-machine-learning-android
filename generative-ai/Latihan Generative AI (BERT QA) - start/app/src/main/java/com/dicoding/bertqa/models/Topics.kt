package com.dicoding.bertqa.models

import com.google.gson.annotations.SerializedName

data class Topics(
    @SerializedName("titles")
    private val titles: List<List<String>>,
    @SerializedName("contents")
    private val contents: List<List<String>>,
    @SerializedName("questions")
    val questions: List<List<String>>
) {

    fun getTitles(): List<String> {
        return titles.map { it[0] }
    }

    fun getContents(): List<String> {
        return contents.map { it[0] }
    }

}
