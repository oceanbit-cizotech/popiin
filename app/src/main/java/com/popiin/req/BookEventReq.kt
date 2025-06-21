package com.popiin.req

class BookEventReq(
    lang: String,
    event_id: String,
    ticket_id: String,
    price: String,
    card_number: String,
    exp_month: String,
    exp_year: String,
    cvc: String,
    credit: String,
    rewards: String,
    booking_fees: String,
    fees: Double,
    booking_type: String,
    discount: String,
    address: String,
    postal_code: String,
    qty: Int,
    email :String,
    phone_number :String
) {
    var lang = "en"
    var event_id: String
    var ticket_id: String
    var price: String
    var card_number: String
    var exp_month: String
    var exp_year: String
    var cvc: String
    var credit: String
    var rewards: String
    var booking_fees: String
    var fees: Double
    var booking_type: String
    var discount: String
    var address: String
    var postal_code: String
    var qty: Int
    var email:String
    var phone_number:String
    init {
        this.lang = lang
        this.event_id = event_id
        this.ticket_id = ticket_id
        this.price = price
        this.card_number = card_number
        this.exp_month = exp_month
        this.exp_year = exp_year
        this.cvc = cvc
        this.credit = credit
        this.rewards = rewards
        this.booking_fees = booking_fees
        this.fees = fees
        this.booking_type = booking_type
        this.discount = discount
        this.address = address
        this.postal_code = postal_code
        this.qty = qty
        this.email =email
        this.phone_number=phone_number
    }
}