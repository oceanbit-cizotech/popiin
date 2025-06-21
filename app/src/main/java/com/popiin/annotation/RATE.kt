package com.popiin.annotation



@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class RATE {
    companion object {
        var VERY_COLD = "Very cold"
        var COLD = "Cold"
        var HOT = "Hot"
        var VERY_HOT = "Very hot"
    }
}