package com.popiin.bottomDialogFragment

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.SelectDurationAdapter
import com.popiin.databinding.BottomFragmentSelectVenueBinding
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.model.DurationModel

class SelectDurationFragment(val list :ArrayList<DurationModel>, var listener: AdapterMyVenuesListener<DurationModel>, var selectedVenue:Int) : BaseBottomSheetDialog<BottomFragmentSelectVenueBinding>() {
    lateinit var adapter : SelectDurationAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_venue
    }

    override fun initViews() {
        adapter= SelectDurationAdapter(list,selectVenue,selectedVenue)
        binding?.rvVenueList?.layoutManager= LinearLayoutManager(mActivity)
        binding?.rvVenueList?.adapter=adapter
        binding?.ivClose?.setOnClickListener{
            dismiss()
        }
        binding?.btnSelectVenue?.setOnClickListener{
            listener.onEventSelected(list[selectedVenue],selectedVenue)
            dismiss()
        }
    }

    override fun hideKeyBoard(context: Activity?) {
    }

    private var selectVenue: AdapterMyVenuesListener<DurationModel> =
        object : AdapterMyVenuesListener<DurationModel>() {
            override fun onEventSelected(item: DurationModel, position: Int) {
                super.onEventSelected(item, position)
                list[selectedVenue]
                selectedVenue = position
                list[position].isSelected = true
            }
        }
}