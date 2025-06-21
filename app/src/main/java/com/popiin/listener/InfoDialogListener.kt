package com.popiin.listener

import com.popiin.bottomDialogFragment.CommonInfoBottomFragment

abstract class InfoDialogListener {
    open fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {}
    open fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {}
    open fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {}
}