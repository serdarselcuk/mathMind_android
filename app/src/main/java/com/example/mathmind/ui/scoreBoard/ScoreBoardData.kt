package com.example.mathmind.ui.scoreBoard

import java.sql.Date

data class ScoreBoardData(
    val person_id: Int,
    val score: Int,
    val achived_date: Date
)