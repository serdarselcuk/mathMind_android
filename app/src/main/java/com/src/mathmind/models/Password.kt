package com.src.mathmind.models

data class Password(
    val hashedPassword : String,
    val hashKey: String,
    val person_id: Int
)