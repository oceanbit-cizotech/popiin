package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignUpRes {
    @Expose
    @SerializedName("message")
    var message: String? = null

    @Expose
    @SerializedName("data")
    var data: Data? = null

    @Expose
    @SerializedName("token")
    var token: String? = null

    @Expose
    @SerializedName("success")
    var success = false

    class Data {
        @Expose
        @SerializedName("unread_count")
        var unread_count = 0

        @Expose
        @SerializedName("min_version_ios")
        var min_version_ios = 0

        @Expose
        @SerializedName("updated_at")
        var updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        var created_at: String? = null

        @Expose
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("marketing_optin")
        var marketing_optin = 0

        @Expose
        @SerializedName("login_type")
        var login_type = 0

        @Expose
        @SerializedName("last_login_on")
        var last_login_on: String? = null

        @Expose
        @SerializedName("reward_points")
        var reward_points = 0

        @Expose
        @SerializedName("has_external_resource")
        var has_external_resource = 0

        @Expose
        @SerializedName("is_stripe_verified")
        var is_stripe_verified = 0

        @Expose
        @SerializedName("verification_token")
        var verification_token = 0

        @Expose
        @SerializedName("gender")
        var gender = 0

        @Expose
        @SerializedName("dob")
        var dob: String? = null

        @Expose
        @SerializedName("facebook_id")
        var facebook_id: String? = null

        @Expose
        @SerializedName("profile_pic")
        var profile_pic: String? = null

        @Expose
        @SerializedName("email")
        var email: String? = null

        @Expose
        @SerializedName("last_name")
        var last_name: String? = null

        @Expose
        @SerializedName("first_name")
        var first_name: String? = null

        @Expose
        @SerializedName("id")
        var id = 0
    }
}