package com.popiin.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.annotation.Languages
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.ActivityForgotPasswordBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.InfoDialogListener
import com.popiin.req.ForgotPasswordReq
import com.popiin.res.CommonRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    lateinit var email: String
    override fun getLayout(): Int {
        return R.layout.activity_forgot_password
    }

    override fun initViews() {

        common.openErrorDialog(this,
            mActivity.supportFragmentManager,
            ContextCompat.getDrawable(mActivity, R.drawable.ic_offer_event_blue),
            getString(R.string.txt_alert),
            getString(R.string.txt_enter_email_to_reset_password))
//        val resetPasswordDialog = InfoPopupDialog(this, resources.getString(R.string.lbl_reset_psw))
//        resetPasswordDialog.setCancelable(false)
//        resetPasswordDialog.show()

        binding.btnSubmit.setOnClickListener {
            if (isValidate()) {
                callForgotPasswordApi()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        common.handleErrorView(binding.edtEmailAddress, binding.tvMsgEmail)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(mActivity, SignInActivity::class.java))
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        })

    }

    private fun isValidate(): Boolean {
        var isValid = true
        email = binding.edtEmailAddress.text.toString()

        if (email.isEmpty()) {
            isValid = false
            binding.tvMsgEmail.visibility = View.VISIBLE
            binding.tvMsgEmail.text = getString(R.string.txt_err_enter_email)
        } else if (!isValidEmailId(email)) {
            isValid = false
            binding.tvMsgEmail.visibility = View.VISIBLE
            binding.tvMsgEmail.text = getString(R.string.txt_err_valid_email)
        }

        return isValid
    }


    private fun callForgotPasswordApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? =
                apiInterface.doForgotPassword(ForgotPasswordReq(binding.edtEmailAddress.getText()
                    .toString(), Languages.ENGLISH))
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("LongLogTag")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    val commonDialog: CommonDialog
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            commonDialog = CommonDialog(
                                mActivity,
                                getString(R.string.lbl_ok),
                                "",
                                "",
                                getString(R.string.txt_check_inbox_password)
                            )
                            commonDialog.show()
                            commonDialog.binding.btnPositive.setOnClickListener {
                                commonDialog.dismiss()
                                startActivity(Intent(mActivity, SignInActivity::class.java))
                                finish()
                            }
                        } else {
                            commonDialog = CommonDialog(mActivity,
                                getString(R.string.lbl_ok),"","", response.body()!!.msg!!)
                            commonDialog.binding.btnPositive.setOnClickListener {
                                commonDialog.dismiss()
                            }
                            commonDialog.show()

                        }


                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


}