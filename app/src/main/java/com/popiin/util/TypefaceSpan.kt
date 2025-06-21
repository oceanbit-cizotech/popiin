package com.popiin.util

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint

class TypefaceSpan(private val typeface: Typeface) : android.text.style.TypefaceSpan("") {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, typeface)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, typeface)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0
        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        paint.textSkewX = -0.25f
        paint.typeface = tf
    }
}