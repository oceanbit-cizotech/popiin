package com.popiin.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.popiin.R
import com.popiin.databinding.DialogInfoPopupBinding


class InfoPopupDialog(context: Context, message: String?) :
    AppCompatDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen),
    View.OnClickListener {
    var binding: DialogInfoPopupBinding
    private fun initView() {
        binding.tvOk.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ok -> dismiss()
        }
    }

    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.statusBarColor = ContextCompat.getColor(context, R.color.colorTransparent)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_info_popup,
            null,
            false)
        setContentView(binding.root)
        binding.tvMessage.text = message
        initView()
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.setDimAmount(0.0f)
        binding.tvOk.setOnClickListener {
            dismiss()
        }
    }
}