package com.popiin.bottomDialogFragment

import android.app.Dialog
import android.view.View
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.annotation.FILTER_EVENT
import com.popiin.databinding.BottomFragmentSelectEventFilterBinding
import com.popiin.databinding.IncludeFilterBinding
import com.popiin.listener.MapFilterListener
import com.popiin.util.PrefManager

class SelectEventFilterBottomFragment(var listener: MapFilterListener) :
    BaseBottomSheetDialog<BottomFragmentSelectEventFilterBinding>() {
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_event_filter
    }

    override fun initViews() {
        initFilters(binding!!, dialog)

        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    private fun initFilters(binding: BottomFragmentSelectEventFilterBinding, dialog: Dialog?) {
        binding.selectedFilter = PrefManager.getEventFilterSelected()
        setSelectedFilter(binding, PrefManager.getEventFilterSelected())
        binding.llFilter.setOnClickListener(null)
        binding.root.setOnClickListener(null)
        setFilterClickListener(
            binding,
            binding.filterPriceLowHigh.root,
            FILTER_EVENT.PRICE_LOW_HIGH,
            dialog
        )
        setFilterClickListener(
            binding,
            binding.filterPriceHighLow.root,
            FILTER_EVENT.PRICE_HIGH_LOW,
            dialog
        )
        setFilterClickListener(binding, binding.filterThisWeek.root, FILTER_EVENT.THIS_WEEK, dialog)
        setFilterClickListener(binding, binding.filterNextWeek.root, FILTER_EVENT.NEXT_WEEK, dialog)
        setFilterClickListener(
            binding,
            binding.filterThisMonth.root,
            FILTER_EVENT.THIS_MONTH,
            dialog
        )
        setFilterClickListener(
            binding,
            binding.filterNextMonth.root,
            FILTER_EVENT.NEXT_MONTH,
            dialog
        )

        setFilterClickListener(
            binding,
            binding.filterChooseDate.root,
            FILTER_EVENT.CHOOSE_A_DATE,
            dialog
        )


    }

    private fun setFilterClickListener(
        binding: BottomFragmentSelectEventFilterBinding,
        filterRoot: View,
        filter: String,
        dialog: Dialog?
    ) {
        filterRoot.setOnClickListener {
            println("setFilterClickListener : FILTER $filter")

            binding.selectedFilter = filter
            PrefManager.setEventFilterSelected(filter)
            setSelectedFilter(binding, filter)
//            if (filter == PrefManager.getSelected()) {
//                println("same selected :filter $filter")
//                PrefManager.setSelected(FILTER_MAP.NONE)
//            } else {
//                binding!!.selectedFilter = filter
//                PrefManager.setSelected(filter)
//                setSelectedFilter(binding, filter)
//                println("diff selected :filter $filter")
//            }
        }

        binding.tvApply.setOnClickListener {
            dialog!!.dismiss()
            listener.onFilterEventItemClick(binding, binding.selectedFilter, dialog, false)
        }

        binding.tvReset.setOnClickListener {
            dialog!!.dismiss()
            PrefManager.setEventFilterSelected(FILTER_EVENT.NONE)
            if (binding.selectedFilter.equals(FILTER_EVENT.CHOOSE_A_DATE)) {
                listener.onFilterEventItemClick(binding, FILTER_EVENT.NONE, dialog, true)
            } else {
                listener.onFilterEventItemClick(binding, FILTER_EVENT.NONE, dialog, false)
            }

        }
    }

    private fun setSelectedFilter(binding: BottomFragmentSelectEventFilterBinding, filter: String) {
        if (filter.equals(FILTER_EVENT.PRICE_LOW_HIGH, true)) {
            setColorOnFilter(
                binding.filterPriceLowHigh,
                binding.filterPriceHighLow,
                binding.filterThisWeek,
                binding.filterNextWeek,
                binding.filterThisMonth,
                binding.filterNextMonth,
                binding.filterChooseDate
            )
        } else if (filter.equals(FILTER_EVENT.PRICE_HIGH_LOW, true)) {
            setColorOnFilter(
                binding.filterPriceHighLow,
                binding.filterPriceLowHigh,
                binding.filterThisWeek,
                binding.filterNextWeek,
                binding.filterThisMonth,
                binding.filterNextMonth,
                binding.filterChooseDate
            )
        } else if (filter.equals(FILTER_EVENT.THIS_WEEK, true)) {
            setColorOnFilter(
                binding.filterThisWeek,
                binding.filterPriceHighLow,
                binding.filterPriceLowHigh,
                binding.filterNextWeek,
                binding.filterThisMonth,
                binding.filterNextMonth,
                binding.filterChooseDate
            )
        } else if (filter.equals(FILTER_EVENT.NEXT_WEEK, true)) {
            setColorOnFilter(
                binding.filterNextWeek,
                binding.filterPriceHighLow,
                binding.filterThisWeek,
                binding.filterPriceLowHigh,
                binding.filterThisMonth,
                binding.filterNextMonth,
                binding.filterChooseDate
            )
        } else if (filter.equals(FILTER_EVENT.THIS_MONTH, true)) {
            setColorOnFilter(
                binding.filterThisMonth,
                binding.filterPriceHighLow,
                binding.filterThisWeek,
                binding.filterNextWeek,
                binding.filterPriceLowHigh,
                binding.filterNextMonth,
                binding.filterChooseDate
            )
        } else if (filter.equals(FILTER_EVENT.NEXT_MONTH, true)) {
            setColorOnFilter(
                binding.filterNextMonth,
                binding.filterPriceHighLow,
                binding.filterThisWeek,
                binding.filterNextWeek,
                binding.filterThisMonth,
                binding.filterPriceLowHigh,
                binding.filterChooseDate
            )
        } else if (filter.equals(FILTER_EVENT.CHOOSE_A_DATE, true)) {
            setColorOnFilter(
                binding.filterChooseDate,
                binding.filterPriceHighLow,
                binding.filterThisWeek,
                binding.filterNextWeek,
                binding.filterThisMonth,
                binding.filterPriceLowHigh,
                binding.filterNextMonth
            )
        }

    }

    private fun setColorOnFilter(
        filterOne: IncludeFilterBinding,
        filterTwo: IncludeFilterBinding,
        filterThree: IncludeFilterBinding,
        filterFour: IncludeFilterBinding,
        filterFive: IncludeFilterBinding,
        filterSix: IncludeFilterBinding,
        filterSeven: IncludeFilterBinding
    ) {
        filterOne.isSelected = true
        filterTwo.isSelected = false
        filterThree.isSelected = false
        filterFour.isSelected = false
        filterFive.isSelected = false
        filterSix.isSelected = false
        filterSeven.isSelected = false
    }

}