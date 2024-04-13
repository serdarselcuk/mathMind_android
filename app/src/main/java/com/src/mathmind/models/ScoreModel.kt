package com.src.mathmind.models

import java.sql.Date

data class ScoreModel(

    val order:Int,
    val userName: String,
    val score: Int,
    val achievedDate: Date
)