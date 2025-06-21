package com.popiin.res
import com.google.gson.annotations.SerializedName

data class LoginRes(
    @SerializedName("data") var user: User?,
    @SerializedName("token") var token: String?,
    @SerializedName("message") var message: String?,
    @SerializedName("success") var success: Boolean
)

data class User(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("first_name") var firstName: String?,
    @SerializedName("last_name") var lastName: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("profile_pic") var profilePic: String?,
    @SerializedName("dob") var dob: String?,
    @SerializedName("gender") var gender: Int = 0,
    @SerializedName("device_token") var deviceToken: String?,
    @SerializedName("status") var status: Int = 0,
    @SerializedName("last_login_on") var lastLoginOn: String?,
    @SerializedName("reward_points") var rewardPoints: Int = 0,
    @SerializedName("marketing_optin") var marketingOptin: Int = 0,
    @SerializedName("login_type") var loginType: Int = 0,
    @SerializedName("has_external_resource") var hasExternalResource: Int = 0,
    @SerializedName("verification_token") var verificationToken: String?,
    @SerializedName("has_venue") var hasVenue: Int = 0,
    @SerializedName("has_event") var hasEvent: Int = 0,
    @SerializedName("min_version_ios") var minVersionIos: Int = 0,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("updated_at") var updatedAt: String?,
    @SerializedName("unread_count") var unreadCount: Int = 0,
    @SerializedName("is_stripe_verified") var is_stripe_verified: Int = 0,
    @SerializedName("stripe_customer_id") var stripe_customer_id: String?
)