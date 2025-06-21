package com.popiin.listener

import com.popiin.model.TicketBook

abstract class AdapterVenueTrendsListener<T> {
    open fun onEditVenueTrendsClick(item: T, position: Int) {}
    open fun onDeleteVenueTrendsClick(item: T, position: Int) {}
    open fun onSelectVenueTrendsClick(item: T, position: Int) {}
}