package com.popiin.util

import android.text.style.ClickableSpan
import android.view.View

class ClickSpan(private val url: String, private val listener: OnClickListener?) : ClickableSpan() {
    override fun onClick(widget: View) {
        listener?.onClick(url)
    }

    interface OnClickListener {
        fun onClick(url: String?)
    }
}