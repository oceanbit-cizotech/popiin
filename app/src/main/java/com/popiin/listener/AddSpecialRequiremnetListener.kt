package com.popiin.listener

import com.popiin.res.MyBookingRes

abstract class AddSpecialRequiremnetListener<T> {
    open fun onAddClick(item: T, position: Int, specialRequirement: String?) {}
    open fun onAddCaptionClick(specialRequirement: String = "") {}
}