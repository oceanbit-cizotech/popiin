package com.popiin.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.popiin.R
import com.popiin.databinding.DialogCommonBinding

class CommonDialog(
    context: Context,
    var positiveText: String,
    var negativeText: String,
    var title: String,
    var message: String,
) :
    AppCompatDialog(context, android.R.style.Theme_Translucent_NoTitleBar) {
    var binding: DialogCommonBinding

    private fun initView() {
        binding.message = message
        binding.title = title
        binding.negativeButton = negativeText
        binding.positiveButton = positiveText
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.statusBarColor = ContextCompat.getColor(context, R.color.colorTransparent)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_common,
            null,
            false)
        setContentView(binding.root)
        initView()
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.setDimAmount(0.0f)
    }
}