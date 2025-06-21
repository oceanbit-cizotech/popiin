package com.popiin.req

import com.google.gson.annotations.SerializedName

class LoginReq(
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
    @SerializedName("device_token") var device_token: String,
    @SerializedName("lang") var lang: String // Ensure API expects "language" instead of "lang"
)