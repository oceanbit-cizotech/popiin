package com.popiin.annotation

import androidx.annotation.StringDef


@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class CONSTANT {
    companion object {
        var SEPRATEOR = "^$^"
        var SEPRATEOR_OTHER = "%$%"
        var IMAGE = "image"
        var MENUIMAGE = "menuimage"
        var BARIMAGE = "https://popiin.s3.eu-west-2.amazonaws.com/icn_bar.png"
        var NIGHTCLUBIMAGE = "https://popiin.s3.eu-west-2.amazonaws.com/icn_nightclub.png"
        var POPIINIMAGE = "https://popiin.s3.eu-west-2.amazonaws.com/default_image.png"
    }
}