package com.popiin.annotation

import androidx.annotation.IntDef
@Target(AnnotationTarget.CLASS,AnnotationTarget.ANNOTATION_CLASS)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class FILTER_VENUE() {
   companion object {
      var NONE = -1
//      var A_Z = 0
//      var Z_A = 1
      var OPEN= 0
      var NIGHTCLUB = 1
      var RESTAURANT = 2
      var PUB = 3
      var CAFE = 4
      var OFFER = 5
       var BAR= 6

   }
}
