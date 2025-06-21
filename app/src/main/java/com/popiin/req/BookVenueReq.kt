package com.popiin.req

class BookVenueReq {
    var card_number: String? = null
    var cvc: String? = null
    var end_datetime: String
    var exp_month: String? = null
    var ticket_id: String
    var special_request: String
    var booking_datetime: String
    var exp_year: String? = null
    var number_of_people: Int

    constructor(
        special_request: String,
        end_datetime: String,
        ticket_id: String,
        booking_datetime: String,
        number_of_people: Int
    ) {
        this.special_request = special_request
        this.end_datetime = end_datetime
        this.ticket_id = ticket_id
        this.booking_datetime = booking_datetime
        this.number_of_people = number_of_people
    }

    constructor(
        card_number: String?,
        cvc: String?,
        end_datetime: String,
        exp_month: String?,
        ticket_id: String,
        special_request: String,
        booking_datetime: String,
        exp_year: String?,
        number_of_people: Int
    ) {
        this.card_number = card_number
        this.cvc = cvc
        this.end_datetime = end_datetime
        this.exp_month = exp_month
        this.ticket_id = ticket_id
        this.special_request = special_request
        this.booking_datetime = booking_datetime
        this.exp_year = exp_year
        this.number_of_people = number_of_people
    }
}