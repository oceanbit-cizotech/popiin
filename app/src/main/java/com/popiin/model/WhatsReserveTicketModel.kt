package com.popiin.model

class WhatsReserveTicketModel {
    var ticket_type: String = ""
    var price: Float? = null
    var quantity: Int = 0
    var venue_id: Int = 0
    var whatson_id: Int = 0
    var id: Int = 0

    constructor(bookingType: String, price: Float, quantity: Int, venue_id: Int) {
        this.ticket_type = bookingType
        this.price = price
        this.quantity = quantity
        this.venue_id = venue_id
    }

    constructor(
        bookingType: String,
        price: Float,
        quantity: Int,
        venue_id: Int,
        whatson_id: Int,
        id: Int,
    ) {
        this.ticket_type = bookingType
        this.price = price
        this.quantity = quantity
        this.venue_id = venue_id
        this.whatson_id = whatson_id
        this.id = id
    }

}