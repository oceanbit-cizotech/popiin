package com.popiin.model

import com.popiin.res.EventListRes
import com.popiin.res.VenueOfferRes
import com.popiin.res.VenueWhatsListRes

class CreateWhatsOnModel {
    var title =""
    var description =""
    var startTime =""
    var endTime =""
    var startDate =""
    var endDate =""
    var music: String = ""
    var musicOther: String = ""
    var entertainment: String = ""
    var entertainmentOther: String = ""
    var djLineUpName: String = ""
    var dressCode: String = ""
    var entryPolicy: String = ""
    var otherInfo: String = ""
    var venueOfferId: String = ""
    var venuesOfferRes: VenueOfferRes? = null
    var whatsOnImages: Array<String?>? = null
    var addTickets: ArrayList<WhatsReserveTicketModel> = ArrayList()
    var updateTickets: ArrayList<WhatsReserveTicketModel> = ArrayList()
    var tickets: ArrayList<WhatsReserveTicketModel> = ArrayList()
    var banner: String = ""

}