package com.popiin.listener

import com.popiin.model.TicketBook

abstract class AdapterTagClickListener<T> {
    open fun onTagClick(item: T, position: Int) {}
}