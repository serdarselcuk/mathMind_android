package com.example.mathmind.ui.scoreBoard

import java.sql.Date

data class UserData(
    val person_id: Int,
    val name: String,
    val sur_name: String,
    val sign_on_date:Date
)