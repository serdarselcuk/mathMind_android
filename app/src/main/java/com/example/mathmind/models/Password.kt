package com.example.mathmind.models

import android.app.Person

data class Password(
    val hashedPassword : String,
    val hashKey: String,
    val person_id: Int
)