package com.example.mathmind.models

import java.sql.Date

data class UserModel(

     val person_id: Int,
     val userName:  String,
     val savedDate:  Date,
     val firstName:  String,
     val secondName:  String
)
