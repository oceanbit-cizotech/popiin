package com.popiin.annotation

import androidx.annotation.StringDef

@Target(AnnotationTarget.CLASS,AnnotationTarget.ANNOTATION_CLASS)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class FILTER_EVENT() {
    companion object {
        var NONE = ""
        var PRICE_LOW_HIGH = "price_low_high"
        var PRICE_HIGH_LOW = "price_high_low"
        var THIS_WEEK = "this_week"
        var NEXT_WEEK = "next_week"
        var THIS_MONTH = "this_month"
        var NEXT_MONTH = "next_month"
        var CHOOSE_A_DATE = "date"
    }
}
