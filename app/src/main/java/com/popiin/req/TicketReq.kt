package com.popiin.req

class TicketReq {
    var is_deposite_required: Int
    var available_quantity = 0
    var quantity: Int
    var end_time: String
    var start_time: String
    var booking_text: String = ""
    var price: Int
    var name: String
    var ticket_id = 0

    constructor(
        is_deposite_required: Int,
        available_quantity: Int,
        quantity: Int,
        end_time: String,
        start_time: String,
        price: Int,
        name: String,
        booking_text: String,
    ) {
        this.is_deposite_required = is_deposite_required
        this.available_quantity = available_quantity
        this.quantity = quantity
        this.end_time = end_time
        this.start_time = start_time
        this.price = price
        this.name = name
        this.booking_text = booking_text
    }

    constructor(
        is_deposite_required: Int,
        quantity: Int,
        end_time: String,
        start_time: String,
        price: Int,
        name: String,
        ticket_id: Int,
        booking_text: String,
    ) {
        this.is_deposite_required = is_deposite_required
        this.quantity = quantity
        this.end_time = end_time
        this.start_time = start_time
        this.price = price
        this.name = name
        this.ticket_id = ticket_id
        this.booking_text = booking_text
    }
}