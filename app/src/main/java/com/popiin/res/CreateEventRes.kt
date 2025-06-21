package com.popiin.res

import com.popiin.res.CreateEventRes.CreateEventData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateEventRes {
    @Expose
    @SerializedName("data")
    val eventData: CreateEventData? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class CreateEventData {
        @Expose
        @SerializedName("id")
        val id = 0

        @Expose
        @SerializedName("updated_at")
        val updated_at: String? = null

        @Expose
        @SerializedName("created_at")
        val created_at: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("other_information")
        val other_information: String? = null

        @Expose
        @SerializedName("has_ticket")
        val has_ticket = 0

        @Expose
        @SerializedName("is_public")
        val is_public = 0

        @Expose
        @SerializedName("is_draft")
        val is_draft = 0

        @Expose
        @SerializedName("longi")
        val longi = 0.0

        @Expose
        @SerializedName("lat")
        val lat = 0.0

        @Expose
        @SerializedName("total_capacity")
        val total_capacity = 0

        @Expose
        @SerializedName("last_entry")
        val last_entry: String? = null

        @Expose
        @SerializedName("entry_policy")
        val entry_policy: String? = null

        @Expose
        @SerializedName("dress_code")
        val dress_code: String? = null

        @Expose
        @SerializedName("entertainment")
        var entertainment: String? = null

        @Expose
        @SerializedName("category")
        val category: String? = null

        @Expose
        @SerializedName("music")
        val music: String? = null

        @Expose
        @SerializedName("end_date_time")
        val end_date_time: String? = null

        @Expose
        @SerializedName("start_date_time")
        val start_date_time: String? = null

        @Expose
        @SerializedName("postal_code")
        val postal_code: String? = null

        @Expose
        @SerializedName("state")
        val state: String? = null

        @Expose
        @SerializedName("city")
        val city: String? = null

        @Expose
        @SerializedName("address")
        val address: String? = null

        @Expose
        @SerializedName("venue")
        val venue: String? = null

        @Expose
        @SerializedName("age")
        val age: String? = null

        @Expose
        @SerializedName("description")
        val description: String? = null

        @Expose
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("name")
        val name: String? = null
    }
}