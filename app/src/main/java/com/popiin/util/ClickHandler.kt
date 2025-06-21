package com.popiin.util

object ClickHandler {
    private var lastClickTime: Long = 0
    private const val CLICK_DELAY = 10 * 1000 // 10 seconds in milliseconds

    fun canClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if ((currentTime - lastClickTime) >= CLICK_DELAY) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }
}
