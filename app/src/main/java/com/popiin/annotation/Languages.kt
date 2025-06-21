package com.popiin.annotation

import androidx.annotation.StringDef

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Languages {
    companion object {
        var ENGLISH = "en"
    }
}
