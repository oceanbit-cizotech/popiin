package com.popiin.listener

abstract class AdapterWithdrawListener<T> {
    open fun onWithdrawEventClick(item: T, position: Int) {}
    open fun onWithdrawVenueClick(item: T, position: Int) {}
}