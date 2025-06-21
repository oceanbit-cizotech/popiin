package com.popiin.bottomDialogFragment

import android.view.View
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentVerifyAccountBinding
import com.popiin.listener.VerifyAccountListener

class VerifyAccountBottomFragment(var verifyAccountListener: VerifyAccountListener) :
    BaseBottomSheetDialog<BottomFragmentVerifyAccountBinding>() {
    var bottomSheetDialog = this
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_verify_account
    }

    override fun initViews() {
        binding!!.tvReset.setOnClickListener {
            verifyAccountListener.onResendClick()
        }

        binding!!.ivClose.setOnClickListener {
            dismiss()
        }

        binding!!.tvApply.setOnClickListener {
            if (isValidate()) {
                verifyAccountListener.onSubmitClick(binding!!.edtVerificationCode.text.toString(),
                    bottomSheetDialog)
            }

        }
    }

    private fun isValidate(): Boolean {
        if (binding!!.edtVerificationCode.text.toString().isEmpty()) {
            binding!!.tvError.visibility = View.VISIBLE
            binding!!.tvError.text = getString(R.string.txt_please_enter_verify_code)
            return false
        }

        return true
    }


}