package com.popiin.req

class CreateWhatsOnReserveReq {
    var quantity: Int
    var venue_id: String
    var ticket_type: String
    var price: Int
    var whatson_id: String
    var id: String? = null

    constructor(
        quantity: Int,
        venue_id: String,
        ticket_type: String,
        price: Int,
        whatson_id: String,
    ) {
        this.quantity = quantity
        this.venue_id = venue_id
        this.ticket_type = ticket_type
        this.price = price
        this.whatson_id = whatson_id
    }

    constructor(
        quantity: Int,
        venue_id: String,
        ticket_type: String,
        price: Int,
        whatson_id: String,
        id: String?,
    ) {
        this.quantity = quantity
        this.venue_id = venue_id
        this.ticket_type = ticket_type
        this.price = price
        this.whatson_id = whatson_id
        this.id = id
    }
}