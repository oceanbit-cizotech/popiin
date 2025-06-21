package com.popiin.bottomDialogFragment

import android.app.Dialog
import android.view.View
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.annotation.FILTER_VENUE
import com.popiin.databinding.BottomFragmentSelectVenueFilterBinding
import com.popiin.databinding.IncludeFilterBinding
import com.popiin.listener.FilterClickListener
import com.popiin.util.PrefManager

class SelectVenueFilterBottomFragment(var listener: FilterClickListener) :
    BaseBottomSheetDialog<BottomFragmentSelectVenueFilterBinding>() {
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_venue_filter
    }

    var isSelected=0
    override fun initViews() {
        isSelected= PrefManager.getOpenNow()

        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }
        if(isSelected==-1){
            binding!!.filterOpen.isSelected =false
        }else{
            binding!!.filterOpen.isSelected =true
        }

        binding!!.filterOpen.root.setOnClickListener {
            if(isSelected==-1){
                isSelected=0
                binding!!.filterOpen.isSelected =true
            }/*else{
                isSelected=-1
                binding!!.filterOpen.isSelected =false
            }*/
        }

        binding!!.tvApply.setOnClickListener {
            dialog!!.dismiss()
            PrefManager.setOpenNow(isSelected)
            listener.onFilterItemClick(binding!!, binding!!.selectedFilter, dialog, false)
        }

        binding!!.tvReset.setOnClickListener {
            dialog!!.dismiss()
            PrefManager.setOpenNow(-1)
            listener.onFilterItemClick(binding!!, FILTER_VENUE.NONE, dialog, true)
        }
    }

}