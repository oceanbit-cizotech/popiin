package com.popiin.req

class BookWhatsOnReq {
    var ticket_id: String? = null
    var number_of_people: String
    var cvc: String? = null
    var exp_month: String? = null
    var card_number: String? = null
    var exp_year: String? = null
    var booking_fees: Double? = 0.0

    constructor(ticket_id: String, number_of_people: String) {
        this.ticket_id = ticket_id
        this.number_of_people = number_of_people
    }

    constructor(
        ticket_id: String,
        number_of_people: String,
        cvc: String?,
        exp_month: String?,
        card_number: String?,
        exp_year: String?,
        booking_fees: Double?,
    ) {
        this.ticket_id = ticket_id
        this.number_of_people = number_of_people
        this.cvc = cvc
        this.exp_month = exp_month
        this.card_number = card_number
        this.exp_year = exp_year
        this.booking_fees = booking_fees
    }
}