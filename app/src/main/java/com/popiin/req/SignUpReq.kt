package com.popiin.req

data class SignUpReq(
    val dob: String,
    val email: String,
    val first_name: String,
    val gender: Int,
    val lang: String,
    val last_name: String,
    val marketing_optin: String,
    val name: String,
    val password: String
)