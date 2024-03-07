package com.src.mathmind.models

import java.sql.Date

data class ScoreBoardModel(
    val person_id: Int,
    val score: Int,
    val achived_date: Date
)