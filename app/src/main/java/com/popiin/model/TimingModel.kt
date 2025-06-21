package com.popiin.model

class TimingModel(
    var day: String,
    var open_time: String? = null,
    var close_time: String? = null,
    var is_24hours: Int,
    var isClear: Boolean,


){
    override fun toString(): String {
        return "TimingModel(day='$day', open_time=$open_time, close_time=$close_time, is_24hours=$is_24hours, isClear=$isClear)"
    }
}