package com.popiin.util

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.popiin.R

class GenericTextWatcher(private val currentView: EditText,nextView: EditText?) :
    TextWatcher {
    private val nextView: EditText?
    override fun afterTextChanged(editable: Editable) {
        // TODO Auto-generated method stub
        val text = editable.toString()
        if (nextView != null && text.length == 1) {
            nextView.requestFocus()
        }
        if (text.length > 1) {
            currentView.setText(text[text.length - 1].toString())
            currentView.setSelection(1)
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
        if (arg0.toString().isEmpty()){
            currentView.setBackgroundResource(R.drawable.bg_edt)
        }else{
            currentView.setBackgroundResource(R.drawable.bg_edt_selected)
        }

    }

    init {
        this.nextView = nextView
    }


    class GenericKeyEvent(
        private val currentView: EditText,
        private val previousView: EditText?
    ) :
        View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.text.toString()
                    .isEmpty()
            ) {
                previousView?.requestFocus()
                currentView.setBackgroundResource(R.drawable.bg_edt)
                return true
            }
            return false
        }
    }
}