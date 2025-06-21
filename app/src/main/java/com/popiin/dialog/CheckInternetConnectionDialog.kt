package com.popiin.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.popiin.R
import com.popiin.databinding.DialogCheckInternetConnectionBinding


class CheckInternetConnectionDialog(context: AppCompatActivity) :
    AppCompatDialog(context) {
    var binding: DialogCheckInternetConnectionBinding
    var activity: FragmentActivity

    init {
        activity = context
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.statusBarColor = ContextCompat.getColor(context, R.color.colorTransparent)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_check_internet_connection,
            null,
            false)
        setContentView(binding.root)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.setDimAmount(0.0f)
        binding.tvOk.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
            } else {
                TODO("VERSION.SDK_INT < P")
            }
            context.startActivity(intent)
            dismiss()
        }
    }
}