package com.popiin.model

data class VenueModel(
    val id: Int,
    val venue_place_id: String?,
    val venue_name: String,
    val venue_description: String?,
    val venue_banner_image: String?,
    val venue_address: String?,
    val venue_city: String?,
    val venue_postal_code: String?,
    val venue_latitude: String?,
    val venue_longitude: String?,
    val venue_age_group: String?,
    val venue_category: String?,
    val venue_music: String?,
    val entertainment: String?,
    val venue_djline: String?,
    val venue_dress_code: String?,
    val venue_door_policy: String?,
    val venue_last_entry: String?,
    val venue_type: String?,
    val venue_other_information: String?,
    val total_views: Int,
    val venuetypes: List<VenueType>?,
    val venuecategories: List<VenueCategory>?,
    var venue_distance: Double? = null // ← Add this line

){
    override fun equals(other: Any?): Boolean {
        return other is VenueModel && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class VenueType(
    val id: Int,
    val venue_id: Int,
    val venue_type: String,
    val status: Int
)

data class VenueCategory(
    val id: Int,
    val venue_id: Int,
    val venue_type: String,
    val category_name: String,
    val status: Int
)



