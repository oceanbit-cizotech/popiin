package com.popiin.res
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.popiin.res.VenueListRes.Venue
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.DecimalFormatter
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class VenueListRes {
    @Expose
    @SerializedName("total")
    val total = 0

    @Expose
    @SerializedName("data")
    var venues: ArrayList<Venue> = ArrayList()

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false
    fun setVenues(venue: Venue) {
        venues.add(venues.size, venue)
        if (venues.size > 10) {
            venues.removeAt(0)
        }
    }



    class Venue : Serializable {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Venue) return false
            return id == other.id // Assuming `id` uniquely identifies a Venue
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }

        var isOpen = ""
        var isFavorite = -1

        @Expose
        @SerializedName("venue_other_information")
        var venue_other_information: String? = null

        @Expose
        @SerializedName("venue_last_entry")
        var venue_last_entry: String? = null

        @Expose
        @SerializedName("tickets")
        var tickets: ArrayList<Tickets>? = null

        @Expose
        @SerializedName("trending")
        var trending: ArrayList<VenueTrend> = arrayListOf()

        @Expose
        @SerializedName("whatson")
        var whatsons: ArrayList<Whatson>? = null

        @Expose
        @SerializedName("venuecategories")
        val venuecategories: List<Venuecategories>? = null

        @Expose
        @SerializedName("venuetypes")
        val venuetypes: List<Venuetypes>? = null

        @Expose
        @SerializedName("liveratings")
        val liveratings: List<String>? = null

        @Expose
        @SerializedName("offerslive")
        var offerslive: List<Offerslive>? = null

        @Expose
        @SerializedName("offertickets")
        val offertickets: List<String>? = null

        @Expose
        @SerializedName("menus")
        val menus: List<Menus>? = null

        @Expose
        @SerializedName("timings")
        val timings: List<TimingsEntity>? = null

        @Expose
        @SerializedName("images")
        val images: List<ImagesEntity>? = null

        @Expose
        @SerializedName("document")
        val document: Document? = null

        @Expose
        @SerializedName("offerstatic")
        var offers: ArrayList<Offer> = ArrayList()

        @Expose
        @SerializedName("distance")
        val distance = 0.0

        @Expose
        @SerializedName("venue_updated_at")
        val venue_updated_at: String? = null

        @Expose
        @SerializedName("venue_created_at")
        val venue_created_at: String? = null

        @Expose
        @SerializedName("venue_djline")
        val venue_djline: String? = null

        @Expose
        @SerializedName("entertainment")
        val entertainment: String? = null

        @Expose
        @SerializedName("venue_total_capacity")
        val venue_total_capacity = 0

        @Expose
        @SerializedName("venue_status")
        val venue_status = 0

        @Expose
        @SerializedName("total_views")
        val total_views:Int = 0

        @Expose
        @SerializedName("is_outdoor_area")
        var is_outdoor_area = 0

        @Expose
        @SerializedName("free_wifi")
        var free_wifi = 0

        @Expose
        @SerializedName("venue_approved")
        val venue_approved = 0

        @Expose
        @SerializedName("reservation_enabled")
        val reservation_enabled = 0

        @Expose
        @SerializedName("need_booking_confirmation")
        var need_booking_confirmation = 0

        @Expose
        @SerializedName("venue_is_public")
        val venue_is_public = 0

        @Expose
        @SerializedName("venue_has_ticket")
        val venue_has_ticket = 0

        @Expose
        @SerializedName("venue_is_draft")
        val venue_is_draft = 0

        @Expose
        @SerializedName("venue_total_ratings")
        val venue_total_ratings = 0

        @Expose
        @SerializedName("venue_ratings")
        val venue_ratings = 0

        @Expose
        @SerializedName("venue_type")
        val venue_type: String? = null

        @Expose
        @SerializedName("venue_door_policy")
        val venue_door_policy: String? = null

        @Expose
        @SerializedName("venue_dress_code")
        val venue_dress_code: String? = null

        @Expose
        @SerializedName("venue_music")
        val venue_music: String? = null

        @Expose
        @SerializedName("venue_category")
        val venue_category: String? = null

        @Expose
        @SerializedName("venue_age_group")
        val venue_age_group: String? = null

        @Expose
        @SerializedName("venue_price")
        val venue_price = 0

        @Expose
        @SerializedName("venue_longitude")
        val venue_longitude: String? = null

        @Expose
        @SerializedName("venue_latitude")
        val venue_latitude: String? = null

        @Expose
        @SerializedName("venue_postal_code")
        val venue_postal_code: String? = null

        @Expose
        @SerializedName("venue_city")
        val venue_city: String? = null

        @Expose
        @SerializedName("venue_address")
        val venue_address: String? = null

        @Expose
        @SerializedName("venue_banner_image")
        val venue_banner_image: String? = null

        @Expose
        @SerializedName("venue_description")
        val venue_description: String? = null

        @Expose
        @SerializedName("venue_name")
        val venue_name: String? = null

        @Expose
        @SerializedName("venue_user_id")
        val venue_user_id = 0

        @Expose
        @SerializedName("venue_place_id")
        val venue_place_id: String? = null

        @Expose
        @SerializedName("venue_share_link")
        var venue_share_link: String? = null

        @Expose
        @SerializedName("id")
        var id = 0
        var isSelected = false
        override fun toString(): String {
            return "{" +
                    "isOpen='" + isOpen + '\'' +
                    ", isFavorite=" + isFavorite +
                    ", venue_other_information='" + venue_other_information + '\'' +
                    ", venue_last_entry='" + venue_last_entry + '\'' +
                    ", tickets=" + tickets +
                    ", whatsons=" + whatsons +
                    ", venuecategories=" + venuecategories +
                    ", venuetypes=" + venuetypes +
                    ", liveratings=" + liveratings +
                    ", offerslive=" + offerslive +
                    ", offertickets=" + offertickets +
                    ", menus=" + menus +
                    ", timings=" + timings +
                    ", images=" + images +
                    ", offers=" + offers +
                    ", distance=" + distance +
                    ", venue_updated_at='" + venue_updated_at + '\'' +
                    ", venue_created_at='" + venue_created_at + '\'' +
                    ", venue_djline='" + venue_djline + '\'' +
                    ", venue_total_capacity=" + venue_total_capacity +
                    ", venue_status=" + venue_status +
                    ", is_outdoor_area=" + is_outdoor_area +
                    ", venue_approved=" + venue_approved +
                    ", reservation_enabled=" + reservation_enabled +
                    ", need_booking_confirmation=" + need_booking_confirmation +
                    ", venue_is_public=" + venue_is_public +
                    ", venue_has_ticket=" + venue_has_ticket +
                    ", venue_is_draft=" + venue_is_draft +
                    ", venue_total_ratings=" + venue_total_ratings +
                    ", venue_ratings=" + venue_ratings +
                    ", venue_type='" + venue_type + '\'' +
                    ", venue_door_policy='" + venue_door_policy + '\'' +
                    ", venue_dress_code='" + venue_dress_code + '\'' +
                    ", venue_music='" + venue_music + '\'' +
                    ", venue_category='" + venue_category + '\'' +
                    ", venue_age_group='" + venue_age_group + '\'' +
                    ", venue_price=" + venue_price +
                    ", venue_longitude='" + venue_longitude + '\'' +
                    ", venue_latitude='" + venue_latitude + '\'' +
                    ", venue_postal_code='" + venue_postal_code + '\'' +
                    ", venue_city='" + venue_city + '\'' +
                    ", venue_address='" + venue_address + '\'' +
                    ", venue_banner_image='" + venue_banner_image + '\'' +
                    ", venue_description='" + venue_description + '\'' +
                    ", venue_name='" + venue_name + '\'' +
                    ", venue_user_id=" + venue_user_id +
                    ", venue_place_id='" + venue_place_id + '\'' +
                    ", venue_share_link='" + venue_share_link + '\'' +
                    ", id=" + id +
                    ", isSelected=" + isSelected +
                    '}'
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun getOpenCloseText(): String {
            return if (isVenueOpen(timings)) "Open Now" else "Close Now"
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun isVenueOpen(timings: List<TimingsEntity>?, date: LocalDateTime = LocalDateTime.now()): Boolean {
            if (timings.isNullOrEmpty()) return false

         //   val date1: LocalDateTime = LocalDateTime.of(2025, 6, 21, 6, 0, 0)
            println("@@@@ "+date.toString())
            val currentDay = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() } // e.g., "Monday"
            val currentDate = date.toLocalDate()
            val currentDateTime = date

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            // 1. Check current day timings
            timings.filter { it.working_day.equals(currentDay, ignoreCase = true) }.forEach { timing ->
                val openStr = timing.open_time ?: return@forEach
                val closeStr = timing.close_time ?: return@forEach

                val openTime = LocalTime.parse(openStr, timeFormatter)
                val closeTime = LocalTime.parse(closeStr, timeFormatter)

                var openDateTime = LocalDateTime.of(currentDate, openTime)
                var closeDateTime = LocalDateTime.of(currentDate, closeTime)

                // Overnight logic
                if (closeDateTime.isBefore(openDateTime)) {
                    closeDateTime = closeDateTime.plusDays(1)
                }

                if (currentDateTime.isAfter(openDateTime) && currentDateTime.isBefore(closeDateTime)) {
                    return true
                }
            }

            // 2. If not open, check previous day's overnight timing
            val prevDay = date.minusDays(1).dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            val previousDate = currentDate.minusDays(1)

            timings.filter { it.working_day.equals(prevDay, ignoreCase = true) }.forEach { timing ->
                val openStr = timing.open_time ?: return@forEach
                val closeStr = timing.close_time ?: return@forEach

                val openTime = LocalTime.parse(openStr, timeFormatter)
                val closeTime = LocalTime.parse(closeStr, timeFormatter)

                var openDateTime = LocalDateTime.of(previousDate, openTime)
                var closeDateTime = LocalDateTime.of(previousDate, closeTime)

                // Overnight logic
                if (closeDateTime.isBefore(openDateTime)) {
                    closeDateTime = closeDateTime.plusDays(1)

                    if (currentDateTime.isAfter(openDateTime) && currentDateTime.isBefore(closeDateTime)) {
                        return true
                    }
                }
            }

            return false
        }
        private fun parseDateTime(dateStr: String, formatter: SimpleDateFormat): Date? {
            return try {
                formatter.parse(dateStr)
            } catch (e: Exception) {
                null
            }
        }

        fun isOpenVenue(): Boolean {
            val formatter = DecimalFormatter.instance ?: return false
            val currentDay = formatter.getWeekDayInWord()

            // Get current time (HH:mm:ss) using Calendar
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentSecond = calendar.get(Calendar.SECOND)



            // Convert current time to seconds for easy comparison
            val currentTimeInSeconds = currentHour * 3600 + currentMinute * 60 + currentSecond

            if (timings.isNullOrEmpty()) {
                return false
            }

            return timings.any { timing ->
                if (timing.working_day.equals(currentDay, ignoreCase = true)) {

                    val openTimeInSeconds = parseTimeToSeconds(timing.open_time.toString())
                    val closeTimeInSeconds = parseTimeToSeconds(timing.close_time.toString())

                    // Handle overnight shift (e.g., 22:00 to 02:00)
                    if (closeTimeInSeconds < openTimeInSeconds) {
                        currentTimeInSeconds >= openTimeInSeconds || currentTimeInSeconds <= closeTimeInSeconds
                    } else {
                        currentTimeInSeconds in openTimeInSeconds..closeTimeInSeconds
                    }
                } else {
                    false
                }
            }
        }

        fun parseTimeToSeconds(timeStr: String): Int {
            // Assumes format: "HH:mm:ss"
            val parts = timeStr.split(":")
            if (parts.size != 3) return 0
            val hour = parts[0].toIntOrNull() ?: 0
            val minute = parts[1].toIntOrNull() ?: 0
            val second = parts[2].toIntOrNull() ?: 0
            return hour * 3600 + minute * 60 + second
        }


        @RequiresApi(Build.VERSION_CODES.O)
        private fun isTimeWithinRange(timing: TimingsEntity, currentTime: LocalTime): Boolean {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            val openTime = LocalTime.parse(timing.open_time, timeFormatter)
            val closeTime = LocalTime.parse(timing.close_time, timeFormatter)

            return if (timing.is_24hours == 1) {
                true
            } else {
                if (openTime.isAfter(closeTime)) {
                    currentTime.isAfter(openTime) || currentTime.isBefore(closeTime)
                } else {
                    currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)
                }
            }
        }
    }

    inner class TimingsEntity : Serializable {
        @Expose
        @SerializedName("is_24hours")
        val is_24hours = 0

        @Expose
        @SerializedName("close_time")
        val close_time: String? = null

        @Expose
        @SerializedName("open_time")
        val open_time: String? = null

        @Expose
        @SerializedName("working_day")
        val working_day: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    inner class ImagesEntity : Serializable {
        @Expose
        @SerializedName("image")
        val image: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Offer : Serializable {

        constructor(title: String) {
            this.title = title
        }

        @Expose
        @SerializedName("title")
        var title: String? = null

        @Expose
        @SerializedName("status")
        val status = 0

        @Expose
        @SerializedName("id")
        val id = 0


        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("created_at")
        val created_at = ""

        @Expose
        @SerializedName("updated_at")
        val updated_at = ""
    }

    class Venuecategories : Serializable {
        @Expose
        @SerializedName("category_name")
        val category_name: String? = null

        @Expose
        @SerializedName("venue_type")
        val venue_type: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Venuetypes : Serializable {
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
        @SerializedName("venue_type")
        val venue_type: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Offerslive : Serializable {
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
        @SerializedName("redeem_allowed_time")
        val redeem_allowed_time = 0

        @Expose
        @SerializedName("is_unique")
        val is_unique = 0

        @Expose
        @SerializedName("close_time")
        val close_time: String? = null

        @Expose
        @SerializedName("open_time")
        val open_time: String? = null

        @Expose
        @SerializedName("working_day")
        var working_day: String? = null

        @Expose
        @SerializedName("title")
        val title: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("total_scanned")
        var total_scanned = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Menus : Serializable {
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
        @SerializedName("image")
        val image: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class VenueCategory : Serializable {
        @Expose
        @SerializedName("venue_type")
        var venue_type: String? = null

        @Expose
        @SerializedName("category_name")
        var category_name: String? = null
    }

    class Tickets : Serializable, Comparable<EventListRes.Tickets> {
        @Expose
        @SerializedName("ticket_type")
        var ticket_type: String? = null

        @Expose
        @SerializedName("booking_text")
        var booking_text: String = ""

        @Expose
        @SerializedName("available_quantity")
        var available_quantity = 0

        @Expose
        @SerializedName("start_time")
        var start_time: String? = null

        @Expose
        @SerializedName("end_time")
        var end_time: String? = null

        @Expose
        @SerializedName("created_at")
        var created_at: String? = null

        @Expose
        @SerializedName("updated_at")
        var updated_at: String? = null

        @Expose
        @SerializedName("quantity")
        var quantity = 0

        @Expose
        @SerializedName("is_deposite_required")
        var is_deposite_required = 0

        @Expose
        @SerializedName("status")
        var status = 0

        @Expose
        @SerializedName("price")
        var price = 0.0

        @Expose
        @SerializedName("name")
        var name: String? = null

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
        var isBookingType = true
        private var isQuantity = true
        var isStartData = true
        var isEndTime = true
        var isDepositAmount = true
        var isSelected = false
        fun isQuantity(): Boolean {
            return isQuantity
        }

        fun setQuantity(quantity: Boolean) {
            isQuantity = quantity
        }

        override fun toString(): String {
            return name!!
        }

        constructor(
            ticket_type: String?,
            available_quantity: Int,
            quantity: Int,
            price: Double,
            name: String?,
            venue_id: Int,
            id: Int,
        ) {
            this.ticket_type = ticket_type
            this.available_quantity = available_quantity
            this.quantity = quantity
            this.price = price
            this.name = name
            this.venue_id = venue_id
            this.id = id
        }

        constructor()

        override fun compareTo(t2: EventListRes.Tickets): Int {
            return if (price > t2.price) {
                1
            } else if (price < t2.price) {
                -1
            } else {
                0
            }
        }
    }

    class Whatson : Serializable {
        @Expose
        @SerializedName("whatsonimages")
        val whatsonimages: List<Whatsonimages>? = null

        @Expose
        @SerializedName("whatsonticket")
        val whatsonticket: ArrayList<WhatsonTicket> = ArrayList()

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
        @SerializedName("end_time")
        val end_time: String? = null

        @Expose
        @SerializedName("what_datetime")
        val what_datetime: String? = null

        @Expose
        @SerializedName("description")
        val description: String? = null

        @Expose
        @SerializedName("title")
        val title: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0

        @Expose
        @SerializedName("music")
        val music: String? = null

        @Expose
        @SerializedName("entertainment")
        val entertainment: String? = null

        @Expose
        @SerializedName("other_music")
        val other_music: String? = null

        @Expose
        @SerializedName("whatson_dress_code")
        val whatson_dress_code: String? = null

        @Expose
        @SerializedName("whatson_entry_policy")
        val whatson_entry_policy: String? = null

        @Expose
        @SerializedName("whatson_djline_up")
        val whatson_djline_up: String? = null
    }

    class Whatsonimages : Serializable {
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
        @SerializedName("image_url")
        val image_url: String? = null

        @Expose
        @SerializedName("venue_whats_on_id")
        val venue_whats_on_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Document {
        @Expose
        @SerializedName("address")
        val address: String? = null

        @Expose
        @SerializedName("city")
        val city: String? = null

        @Expose
        @SerializedName("comments")
        val comments: String? = null

        @Expose
        @SerializedName("company_name")
        val companyName: String? = null

        @Expose
        @SerializedName("created_at")
        val createdAt: String? = null

        @Expose
        @SerializedName("email")
        val email: String? = null

        @Expose
        @SerializedName("first_name")
        val firstName: String? = null

        @Expose
        @SerializedName("id")
        val id: Int = 0

        @Expose
        @SerializedName("last_name")
        val lastName: String? = null

        @Expose
        @SerializedName("personal_doc")
        val personalDoc: String? = null

        @Expose
        @SerializedName("postcode")
        val postcode: String? =null

        @Expose
        @SerializedName("status")
        val status: Int = 0

        @Expose
        @SerializedName("updated_at")
        val updatedAt: String? = null

        @Expose
        @SerializedName("user_id")
        val userId: Int = 0

        @Expose
        @SerializedName("venue_doc")
        val venueDoc: String? = null

        @Expose
        @SerializedName("venue_id")
        val venueId: Int = 0

        @Expose
        @SerializedName("verification_status")
        val verificationStatus: Int = 0
    }

    class WhatsonTicket : Serializable, Comparable<EventListRes.Tickets> {
        @Expose
        @SerializedName("quantity")
        var quantity = 0

        @Expose
        @SerializedName("price")
        var price = 0.0

        @Expose
        @SerializedName("ticket_type")
        var ticket_type: String? = null

        @Expose
        @SerializedName("whatson_id")
        val whatson_id = 0

        @Expose
        @SerializedName("venue_id")
        var venue_id = 0

        @Expose
        @SerializedName("id")
        var id = 0
        var isBookingType = true
        private var isQuantity = true
        var isStartData = true
        var isEndTime = true
        var isDepositAmount = true
        var isSelected = false
        fun isQuantity(): Boolean {
            return isQuantity
        }

        fun setQuantity(quantity: Boolean) {
            isQuantity = quantity
        }

        override fun toString(): String {
            return ticket_type!!
        }

        constructor(
            ticket_type: String?,
            available_quantity: Int,
            quantity: Int,
            price: Double,
            name: String?,
            venue_id: Int,
            id: Int,
        ) {
            this.ticket_type = ticket_type
            this.quantity = quantity
            this.price = price
            this.ticket_type = ticket_type
            this.venue_id = venue_id
            this.id = id
        }

        constructor()

        override fun compareTo(t2: EventListRes.Tickets): Int {
            return if (price > t2.price) {
                1
            } else if (price < t2.price) {
                -1
            } else {
                0
            }
        }
    }
}

class VenueByOpenFlag : Comparator<Venue> {
    override fun compare(o1: Venue?, o2: Venue?): Int {
        if (o1 == null || o2 == null) {
            return 0
        }

        val firstSmallerThanCurrent = o1.isOpenVenue()
        val secondSmallerThanCurrent = o2.isOpenVenue()

        return if (firstSmallerThanCurrent == secondSmallerThanCurrent) {
            0
        } else if (firstSmallerThanCurrent && !secondSmallerThanCurrent) {
            -1
        } else {
            1
        }
    }
}