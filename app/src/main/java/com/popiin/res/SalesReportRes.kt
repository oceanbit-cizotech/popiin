package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SalesReportRes {
    @Expose
    @SerializedName("summary")
    val summary: Summary? = null

    @Expose
    @SerializedName("demographics_reports")
    val demographics_reports: ArrayList<Demographics_reports>? = null

    @Expose
    @SerializedName("ticket_types")
    val ticket_types: List<Ticket_types>? = null

    @Expose
    @SerializedName("average_ratings")
    val average_ratings: Average_ratings? = null

    @Expose
    @SerializedName("data")
    val reportData: List<ReportData>? = null

    @Expose
    @SerializedName("message")
    val message: String? = null

    @Expose
    @SerializedName("success")
    val success = false

    class Summary {
        @Expose
        @SerializedName("Tickets")
        val tickets: List<Tickets>? = null

        @Expose
        @SerializedName("Sales Summary")
        val sales_Summary: List<Sales_Summary>? = null

        @Expose
        @SerializedName("Booking Summary")
        val booking_Summary: List<Booking_Summary>? = null

        @Expose
        @SerializedName("User Summary")
        val user_Summary: List<User_Summary>? = null
    }

    class Tickets {
        @Expose
        @SerializedName(value = "Tickets", alternate = ["Tables"])
        val tickets = 0

        @Expose
        @SerializedName("Income")
        val income = 0.0
    }

    class Sales_Summary {
        @Expose
        @SerializedName(value = "Bookings", alternate = ["Attendees"])
        val bookings = 0

        @Expose
        @SerializedName("Income")
        val income = 0.0
    }

    class Booking_Summary {
        @Expose
        @SerializedName("Bookings")
        val bookings = 0

        @Expose
        @SerializedName("Income")
        val income = 0.0
    }

    class User_Summary {
        @Expose
        @SerializedName("Attendees")
        val attendees = 0

        @Expose
        @SerializedName("Income")
        val income = 0.0
    }

    class Demographics_reports : Comparable<Demographics_reports> {
        @Expose
        @SerializedName("Attendees")
        val attendees = 0

        @Expose
        @SerializedName("gender")
        val gender: String? = null

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("total")
        val total = 0.0
        override fun compareTo(reports: Demographics_reports): Int {
            return if (gender == null || gender.isEmpty()) {
                1
            } else if (reports.gender == null || reports.gender.isEmpty()) {
                -1
            } else if (gender.equals(reports.gender, ignoreCase = true)) {
                0
            } else {
                Integer.compare(
                    Integer.valueOf(gender),
                    Integer.valueOf(reports.gender)
                )
            }
        }
    }

    class Ticket_types {
        @Expose
        @SerializedName("event_id")
        val event_id = 0

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("total")
        val total = 0.0

        @Expose
        @SerializedName("total_num")
        val total_num = 0

        @Expose
        @SerializedName("booking_type")
        val booking_type: String? = null

        @Expose
        @SerializedName("ticket_name")
        val ticket_name: String? = null
    }

    class Average_ratings {
        @Expose
        @SerializedName("ave_ratings")
        val ave_ratings = 0.0
    }

    class ReportData {
        @Expose
        @SerializedName("ticket")
        val ticket: Ticket? = null

        @Expose
        @SerializedName("user")
        val user: User? = null

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
        @SerializedName("is_paid")
        val is_paid = 0

        @Expose
        @SerializedName("special_request")
        val special_request: String? = null

        @Expose
        @SerializedName("promo_code")
        val promo_code: String? = null

        @Expose
        @SerializedName("discounts")
        val discounts = 0

        @Expose
        @SerializedName("booking_fees")
        val booking_fees = 0.0

        @Expose
        @SerializedName("number_of_people")
        val number_of_people = 0

        @Expose
        @SerializedName("is_scanned")
        val is_scanned = 0

        @Expose
        @SerializedName("end_datetime")
        val end_datetime: String? = null

        @Expose
        @SerializedName("booking_datetime")
        val booking_datetime: String? = null

        @Expose
        @SerializedName("ratings")
        val ratings = 0

        @Expose
        @SerializedName("ref_number")
        val ref_number: String? = null

        @Expose
        @SerializedName("stripe_trans_id")
        val stripe_trans_id: String? = null

        @Expose
        @SerializedName("booking_type")
        val booking_type: String? = null

        @Expose
        @SerializedName("price")
        val price = 0.0

        @Expose
        @SerializedName("ticket_id")
        val ticket_id = 0

        @Expose
        @SerializedName("user_id")
        val user_id = 0

        @Expose
        @SerializedName("event_id")
        val event_id = 0

        @Expose
        @SerializedName("venue_id")
        val venue_id = 0

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class Ticket {
        @Expose
        @SerializedName("name")
        val name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }

    class User {
        @Expose
        @SerializedName("gender")
        val gender = 0

        @Expose
        @SerializedName("dob")
        val dob: String? = null

        @Expose
        @SerializedName("first_name")
        val name: String? = null

        @Expose
        @SerializedName("id")
        val id = 0
    }
}