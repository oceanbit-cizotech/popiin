package com.popiin.listener

import android.app.Dialog
import com.popiin.databinding.BottomFragmentSelectEventFilterBinding
import com.popiin.databinding.BottomFragmentSelectMapFilterBinding

abstract class MapFilterListener {
    open fun onFilterItemClick(binding: BottomFragmentSelectMapFilterBinding, filter: Int) {}
    open fun onFilterEventItemClick(
        binding: BottomFragmentSelectEventFilterBinding,
        filter: String?,
        dialog: Dialog?,
        isReset: Boolean
    ) {
    }
}