package com.popiin.model

import java.io.Serializable

class CreateEventModel : Serializable {
    var eventName: String = ""
    var venueName: String = ""
    var eventType: String = ""
    var age: String = ""
    var description: String = ""
    var imagePath: Array<String?>? = null
    var address: String = ""
    var city: String = ""
    var postCode: String = ""
    var startDateTime: String = ""
    var endDateTime: String = ""
    var music: String = ""
    var entertainment: String = ""
    var category: String = ""
    var dressCode: String = ""
    var entryPolicy: String = ""
    var lastEntryPolicy: String = ""
    var totalCapacity: Int = 0
    var is_public: Int = 1
    var is_draft: Int = 0
    var latitude : String = ""
    var longitude: String = ""
    var musicOther: String = ""
    var eventMusicName: String = ""
    var eventEntertainmentNamr :String=""
    var otherInfo: String = ""
    var ticketBooks: ArrayList<TicketBook> = ArrayList()
    var isBasicInfoVerified = false
    var isAddressVerfied = false
    var isEventTimeVerified = false
    var isOtherInfoVerified = false
    var id:Int=0

    constructor()

    constructor(
        eventName: String,
        venueName: String,
        eventType: String,
        age: String,
        description: String,
        imagePath: Array<String?>?,
        address: String,
        city: String,
        postCode: String,
        startDateTime: String,
        endDateTime: String,
        music: String,
        entertainment: String,
        category: String,
        dressCode: String,
        entryPolicy: String,
        lastEntryPolicy: String,
        totalCapacity: Int,
        is_public: Int,
        latitude: String,
        longitude: String,
        eventMusicName: String,
        eventEntertainmentNamr: String,
        otherInfo: String,
        ticketBooks: ArrayList<TicketBook>,
        isBasicInfoVerified: Boolean,
        isAddressVerfied: Boolean,
        isEventTimeVerified: Boolean,
        isOtherInfoVerified: Boolean,is_draft: Int,
        id:Int)
    {
        this.eventName = eventName
        this.venueName = venueName
        this.eventType = eventType
        this.age = age
        this.description = description
        this.imagePath = imagePath
        this.address = address
        this.city = city
        this.postCode = postCode
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
        this.music = music
        this.entertainment = entertainment
        this.category = category
        this.dressCode = dressCode
        this.entryPolicy = entryPolicy
        this.lastEntryPolicy = lastEntryPolicy
        this.totalCapacity = totalCapacity
        this.is_public = is_public
        this.latitude = latitude
        this.longitude = longitude
        this.eventMusicName = eventMusicName
        this.eventEntertainmentNamr = eventEntertainmentNamr
        this.otherInfo = otherInfo
        this.ticketBooks = ticketBooks
        this.isBasicInfoVerified = isBasicInfoVerified
        this.isAddressVerfied = isAddressVerfied
        this.isEventTimeVerified = isEventTimeVerified
        this.isOtherInfoVerified = isOtherInfoVerified
        this.is_draft = is_draft
        this.id=id
    }
}