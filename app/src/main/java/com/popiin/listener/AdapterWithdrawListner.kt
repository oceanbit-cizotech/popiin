package com.popiin.listener

abstract class AdapterWithdrawListner<T> {
    open fun onWithdrawClick(item: T, position: Int) {}
    open fun onWithdrawVenueClick(item: T, position: Int) {}
}