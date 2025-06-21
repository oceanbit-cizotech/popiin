package com.popiin.listener

import android.view.View
import com.popiin.util.Common

abstract class ClickListener : View.OnClickListener {
    private val c: Common = Common.instance!!

    //    public abstract void onClick(View v, int id);
    abstract fun OnClick(v: View?)

    /**
     * **Never Use This Method.**** This method is only for internal use.
     ** */
    override fun onClick(v: View) {
        if (!c.isAllowClick) {
            return
        }
        //        onClick(v, v.getId());
        OnClick(v)
    }

    companion object {
        val TAG = ClickListener::class.java.simpleName
    }
}