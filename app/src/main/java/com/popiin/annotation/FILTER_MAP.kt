package com.popiin.annotation

import androidx.annotation.IntDef

@Target(AnnotationTarget.CLASS,AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FILTER_MAP() {
    companion object {
        var NONE = -1
        var BAR = 0
        var NIGHTCLUB = 1
        var RESTAURANT = 2
        var PUB = 3
        var CAFE = 4
        var OFFER = 5
        var TRENDING = 6
        var MOST_POPULAR = 7
    }
}
