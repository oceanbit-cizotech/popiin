package com.popiin.res

import com.google.gson.annotations.SerializedName

data class SubmitDocRes(
    @SerializedName("data")
    val documentData: DocumentData,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
) {
    data class DocumentData(
        @SerializedName("address")
        val address: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("comments")
        val comments: Any,
        @SerializedName("company_name")
        val companyName: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("personal_doc")
        val personalDoc: String,
        @SerializedName("postcode")
        val postcode: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("venue_doc")
        val venueDoc: String,
        @SerializedName("venue_id")
        val venueId: Int,
        @SerializedName("verification_status")
        val verificationStatus: Int,
    )
}