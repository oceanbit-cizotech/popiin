package com.popiin.annotation

@Target(AnnotationTarget.CLASS,AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class SHARE_MESSAGE_TYPE (){
    companion object {
        var VENUE=1
        var EVENT=2
        var FRIENDS=3
    }
}