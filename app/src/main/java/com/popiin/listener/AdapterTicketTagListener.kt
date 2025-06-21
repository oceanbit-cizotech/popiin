package com.popiin.listener

import com.popiin.model.TicketBook
abstract class AdapterTicketTagListener<T> {
    open fun onAddNewTag(ticketBook: TicketBook?) {}
    open fun onUpdateTag(ticketBook: TicketBook?, position: Int) {}
    open fun onTagClick(ticketBook: TicketBook?, position: Int) {}
    open fun onItemClick(ticketBook: TicketBook?, position: Int) {}
    open fun onItemDeleteClick(ticketBook: TicketBook?, position: Int) {}
}