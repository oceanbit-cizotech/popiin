package com.popiin.listener

import android.app.Dialog
import com.popiin.databinding.BottomFragmentSelectMapFilterBinding
import com.popiin.databinding.BottomFragmentSelectVenueFilterBinding

abstract class FilterClickListener {
    open fun onFilterItemClick(
        binding: BottomFragmentSelectVenueFilterBinding, filter: Int, dialog: Dialog?,
        isReset: Boolean
    ) {
    }
}