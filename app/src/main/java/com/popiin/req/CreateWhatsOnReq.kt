package com.popiin.req

import com.popiin.model.WhatsReserveTicketModel
import com.popiin.res.VenueWhatsListRes

class CreateWhatsOnReq {
    var offer_id: String = ""
    var venue_id: String = ""
    var title: String = ""
    var description: String = ""
    var what_datetime: String = ""
    var end_time: String = ""
    var music: String = ""
    var whatson_djline_up: String = ""
    var entertainment: String = ""
    var whatson_dress_code: String = ""
    var whatson_entry_policy: String = ""
    var other_information: String = ""
    var images: Array<String?>? = null
    var start_date_display: String = ""
    var start_time_display: String = ""
    var end_date_display: String = ""
    var end_time_display: String = ""
    var tickets: ArrayList<WhatsReserveTicketModel> = ArrayList()
    var venue_whats_id: String? = null


    constructor(
        offer_id: String,
        venue_id: String,
        title: String,
        description: String,
        what_datetime: String,
        end_time: String,
        music: String,
        whatson_djline_up: String,
        entertainment: String,
        whatson_dress_code: String,
        whatson_entry_policy: String,
        other_information: String,
        images: Array<String?>?,
        start_date_display: String,
        start_time_display: String,
        end_date_display: String,
        end_time_display: String,
        tickets: ArrayList<WhatsReserveTicketModel>,
    ) {
        this.offer_id = offer_id
        this.venue_id = venue_id
        this.title = title
        this.description = description
        this.what_datetime = what_datetime
        this.end_time = end_time
        this.music = music
        this.whatson_djline_up = whatson_djline_up
        this.entertainment = entertainment
        this.whatson_dress_code = whatson_dress_code
        this.whatson_entry_policy = whatson_entry_policy
        this.other_information = other_information
        this.images = images
        this.start_date_display = start_date_display
        this.start_time_display = start_time_display
        this.end_date_display = end_date_display
        this.end_time_display = end_time_display
        this.tickets = tickets
    }

    constructor(
        offer_id: String,
        venue_id: String,
        title: String,
        description: String,
        what_datetime: String,
        end_time: String,
        music: String,
        whatson_djline_up: String,
        entertainment: String,
        whatson_dress_code: String,
        whatson_entry_policy: String,
        other_information: String,
        images: Array<String?>?,
        start_date_display: String,
        start_time_display: String,
        end_date_display: String,
        end_time_display: String,
        tickets: ArrayList<WhatsReserveTicketModel>,
        venue_whats_id: String?,
    ) {
        this.offer_id = offer_id
        this.venue_id = venue_id
        this.title = title
        this.description = description
        this.what_datetime = what_datetime
        this.end_time = end_time
        this.music = music
        this.whatson_djline_up = whatson_djline_up
        this.entertainment = entertainment
        this.whatson_dress_code = whatson_dress_code
        this.whatson_entry_policy = whatson_entry_policy
        this.other_information = other_information
        this.images = images
        this.start_date_display = start_date_display
        this.start_time_display = start_time_display
        this.end_date_display = end_date_display
        this.end_time_display = end_time_display
        this.tickets = tickets
        this.venue_whats_id = venue_whats_id
    }

    constructor()


}


