package com.popiin.res
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class EditEventRes(
    @SerializedName("data")
    @Expose
    var editEventData: EditEventData,
    @SerializedName("message")
    @Expose
    var message: String,
    @SerializedName("success")
    @Expose
    var success: Boolean
) {
    data class EditEventData(
        @SerializedName("address")
        @Expose
        var address: String,
        @SerializedName("age")
        @Expose
        var age: String,
        @SerializedName("category")
        @Expose
        var category: String,
        @SerializedName("city")
        @Expose
        var city: String,
        @SerializedName("created_at")
        @Expose
        var createdAt: String,
        @SerializedName("description")
        @Expose
        var description: String,
        @SerializedName("dress_code")
        @Expose
        var dressCode: String,
        @SerializedName("end_date_time")
        @Expose
        var endDateTime: String,
        @SerializedName("entertainment")
        @Expose
        var entertainment: String,
        @SerializedName("entry_policy")
        @Expose
        var entryPolicy: String,
        @SerializedName("has_ticket")
        @Expose
        var hasTicket: Int,
        @SerializedName("id")
        @Expose
        var id: Int,
        @SerializedName("is_boosted")
        @Expose
        var isBoosted: Int,
        @SerializedName("is_draft")
        @Expose
        var isDraft: Int,
        @SerializedName("is_promoted")
        @Expose
        var isPromoted: Int,
        @SerializedName("is_public")
        @Expose
        var isPublic: Int,
        @SerializedName("last_entry")
        @Expose
        var lastEntry: String,
        @SerializedName("lat")
        @Expose
        var lat: Double,
        @SerializedName("longi")
        @Expose
        var longi: Double,
        @SerializedName("music")
        @Expose
        var music: String,
        @SerializedName("music_djlineup")
        @Expose
        var musicDjlineup: String,
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("other_information")
        @Expose
        var otherInformation: String,
        @SerializedName("postal_code")
        @Expose
        var postalCode: String,
        @SerializedName("ratings")
        @Expose
        var ratings: Int,
        @SerializedName("share_link")
        @Expose
        var shareLink: String,
        @SerializedName("start_date_time")
        @Expose
        var startDateTime: String,
        @SerializedName("state")
        @Expose
        var state: String,
        @SerializedName("status")
        @Expose
        var status: Int,
        @SerializedName("total_capacity")
        @Expose
        var totalCapacity: Int,
        @SerializedName("total_ratings")
        @Expose
        var totalRatings: Int,
        @SerializedName("updated_at")
        @Expose
        var updatedAt: String,
        @SerializedName("user_id")
        @Expose
        var userId: Int,
        @SerializedName("venue")
        @Expose
        var venue: String,
    )
}