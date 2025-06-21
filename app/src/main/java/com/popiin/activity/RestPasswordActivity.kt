package com.popiin.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.databinding.ActivityResetPasswordBinding
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.util.GenericTextWatcher
import java.util.*
import java.util.concurrent.TimeUnit

class RestPasswordActivity : BaseActivity<ActivityResetPasswordBinding>() {
    lateinit var timer: CountDownTimer
    override fun getLayout(): Int {
        return R.layout.activity_reset_password
    }

    override fun initViews() {
        attachTextWatchersOTP()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.btnResetPassword.setOnClickListener {
            if (isValidate()){
                openBottomSheetDialog()
                timer.onFinish()
            }
        }

        common.handleEdtErrorView(binding.edtOtpOne,binding.tvMsgOtp)
        common.handleEdtErrorView(binding.edtOtpTwo,binding.tvMsgOtp)
        common.handleEdtErrorView(binding.edtOtpThree,binding.tvMsgOtp)
        common.handleEdtErrorView(binding.edtOtpFour,binding.tvMsgOtp)
        common.handleEdtErrorView(binding.edtOtpFive,binding.tvMsgOtp)
        common.handleEdtErrorView(binding.edtOtpSix,binding.tvMsgOtp)

        countTimer()

        binding.tvCodeResend.setOnClickListener {
            binding.tvTimer.visibility = View.VISIBLE
            binding.tvCodeResend.visibility = View.GONE
            binding.tvCodeNotReceive.visibility = View.GONE

            countTimer()
        }

    }

    fun countTimer(){

        timer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var timerText = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                binding.tvTimer.text = "Remaining : $timerText"

            }

            override fun onFinish() {
                binding.tvCodeResend.visibility = View.VISIBLE
                binding.tvCodeNotReceive.visibility = View.VISIBLE
                binding.tvTimer.visibility =View.GONE
            }
        }
        timer.start()
    }

    private fun isValidate(): Boolean {
        var isValid = true

        var otpOne = binding.edtOtpOne.text.toString()
        var otpTwo = binding.edtOtpTwo.text.toString()
        var otpThree = binding.edtOtpThree.text.toString()
        var otpFour = binding.edtOtpFour.text.toString()
        var otpFive = binding.edtOtpFive.text.toString()
        var otpSix = binding.edtOtpSix.text.toString()

        if (otpOne.isEmpty() || otpTwo.isEmpty() || otpThree.isEmpty() ||
            otpFour.isEmpty() || otpFive.isEmpty() || otpSix.isEmpty()){
            isValid =  false
            binding.tvMsgOtp.visibility = View.VISIBLE
            binding.tvMsgOtp.text = getString(R.string.txt_err_valid_otp)
        }

        return isValid

    }

    private fun attachTextWatchersOTP(){
        binding.edtOtpOne.addTextChangedListener(GenericTextWatcher(binding.edtOtpOne, binding.edtOtpTwo))
        binding.edtOtpTwo.addTextChangedListener(GenericTextWatcher(binding.edtOtpTwo, binding.edtOtpThree))
        binding.edtOtpThree.addTextChangedListener(GenericTextWatcher(binding.edtOtpThree, binding.edtOtpFour))
        binding.edtOtpFour.addTextChangedListener(GenericTextWatcher(binding.edtOtpFour, binding.edtOtpFive))
        binding.edtOtpFive.addTextChangedListener(GenericTextWatcher(binding.edtOtpFive, binding.edtOtpSix))
        binding.edtOtpSix.addTextChangedListener(GenericTextWatcher(binding.edtOtpSix, null))

        binding.edtOtpTwo.setOnKeyListener(GenericTextWatcher.GenericKeyEvent(binding.edtOtpTwo, binding.edtOtpOne))
        binding.edtOtpThree.setOnKeyListener(GenericTextWatcher.GenericKeyEvent(binding.edtOtpThree, binding.edtOtpTwo))
        binding.edtOtpFour.setOnKeyListener(GenericTextWatcher.GenericKeyEvent(binding.edtOtpFour, binding.edtOtpThree))
        binding.edtOtpFive.setOnKeyListener(GenericTextWatcher.GenericKeyEvent(binding.edtOtpFive, binding.edtOtpFour))
        binding.edtOtpSix.setOnKeyListener(GenericTextWatcher.GenericKeyEvent(binding.edtOtpSix, binding.edtOtpFive))
    }

    override fun onBackPressed() {
        startActivity(Intent(mActivity,SignInActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun openBottomSheetDialog() {
        val dialog = PopupWindow(this)
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.dialog_reset_password, null, false)

        binding.tvPassSuccess.text = getString(R.string.txt_password_reset)
        binding.tvSuccess.text = getString(R.string.txt_successfully)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(mActivity,HomeActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }
}