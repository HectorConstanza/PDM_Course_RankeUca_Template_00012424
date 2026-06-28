package com.pdmcourse2026.basictemplate.data.model

data class Option(
    val id: Int = 0,
    val name: String,
    val imageUrl: String? = null,
    val questionId: Int = 0,
    val votes: Int = 0,
)
