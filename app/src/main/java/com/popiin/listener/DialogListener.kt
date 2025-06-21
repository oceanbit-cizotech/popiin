package com.popiin.listener

import com.popiin.model.TimingModel

abstract class DialogListener {
    open fun onSelectedTime(hours:String,minutes:String,ampms:String) {}
}