package com.popiin.req

import com.popiin.model.WhatsReserveTicketModel
import com.popiin.res.VenueWhatsListRes

class UpdateWhatsOnReq(
    var venue_whats_id: String? = null,
    var offer_id: String,
    var venue_id: String,
    var title: String,
    var description: String,
    var what_datetime: String,
    var end_time: String,
    var music: String,
    var whatson_djline_up: String,
    var entertainment: String,
    var whatson_dress_code: String,
    var whatson_entry_policy: String,
    var other_information: String,
    var images: Array<String?>? = null,
    var start_date_display: String,
    var start_time_display: String,
    var end_date_display: String,
    var end_time_display: String,
    var tickets: ArrayList<WhatsReserveTicketModel> = ArrayList(),
)