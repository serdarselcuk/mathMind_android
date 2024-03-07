package com.src.mathmind.models

data class UserModel(
     val person_id: Int?,
     val userName:  String,
     val savedDate:  String,
     val firstName:  String,
     val secondName:  String,
     val email: String,
     val password: String,
     val hashCode: String
)