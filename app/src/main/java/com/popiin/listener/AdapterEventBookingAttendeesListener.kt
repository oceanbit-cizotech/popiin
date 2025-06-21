package com.popiin.listener

abstract class AdapterEventBookingAttendeesListener<T> {
    open fun onScanCode(item: T, position: Int) {}
    open fun onAcceptClick(item: T, position: Int) {}
    open fun onRejectClick(item: T, position: Int) {}
    open fun onCancel(item: T, position: Int) {}
}