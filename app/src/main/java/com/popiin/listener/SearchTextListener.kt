package com.popiin.listener

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

abstract class SearchTextListener : TextWatcher {
    private val handler: Handler = Handler()
    abstract fun performSearch(searchText: String?)
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            performSearch(s.toString())
        }, SEARCH_DELAY)
    }

    override fun afterTextChanged(s: Editable) {}

    companion object {
        val TAG = SearchTextListener::class.java.simpleName
        private const val SEARCH_DELAY: Long = 500
    }
}