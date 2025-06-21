package com.popiin.req

import com.popiin.req.TicketReq

class CreateVenueTicketsReq {
    var data: List<TicketReq>? = null
    var tickets: List<TicketReq>? = null
    var venue_id = 0

    constructor(data: List<TicketReq>?, venue_id: Int) {
        this.data = data
        this.venue_id = venue_id
    }

    constructor(venue_id: Int, tickets: List<TicketReq>?) {
        this.venue_id = venue_id
        this.tickets = tickets
    }

    constructor(tickets: List<TicketReq>?) {
        this.tickets = tickets
    }
}