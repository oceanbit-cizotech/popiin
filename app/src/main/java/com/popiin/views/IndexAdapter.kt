package com.popiin.views

import com.popiin.views.Indexable

interface IndexAdapter {
    fun getItem(position: Int): Indexable?
}