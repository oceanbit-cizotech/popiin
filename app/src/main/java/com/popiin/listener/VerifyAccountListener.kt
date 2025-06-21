package com.popiin.listener

import com.popiin.bottomDialogFragment.VerifyAccountBottomFragment

abstract class VerifyAccountListener {
    open fun onResendClick() {}
    open fun onSubmitClick(verifyCode: String, bottomSheetDialog: com.popiin.bottomDialogFragment.VerifyAccountBottomFragment) {}
}