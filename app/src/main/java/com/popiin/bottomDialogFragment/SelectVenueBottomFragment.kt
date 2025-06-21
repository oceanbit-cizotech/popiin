package com.popiin.bottomDialogFragment

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.OfferVenueAdapter
import com.popiin.databinding.BottomFragmentSelectVenueBinding
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.res.VenueListRes

class SelectVenueBottomFragment  (val venues :ArrayList<VenueListRes.Venue>, var listener:AdapterMyVenuesListener<VenueListRes.Venue>,var selectedVenue:Int):BaseBottomSheetDialog<BottomFragmentSelectVenueBinding>() {
    lateinit var adapter : OfferVenueAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_venue
    }
    override fun initViews() {
        adapter= OfferVenueAdapter(venues,selectVenue,selectedVenue)
        binding?.rvVenueList?.layoutManager=LinearLayoutManager(mActivity)
        binding?.rvVenueList?.adapter=adapter
        binding?.ivClose?.setOnClickListener{
            dismiss()
        }
        binding?.btnSelectVenue?.setOnClickListener{
            listener.onEventSelected(venues[selectedVenue],selectedVenue)
            dismiss()
        }
    }
    override fun hideKeyBoard(context: Activity?) {
    }

    private var selectVenue: AdapterMyVenuesListener<VenueListRes.Venue> =
        object : AdapterMyVenuesListener<VenueListRes.Venue>() {
            override fun onEventSelected(item: VenueListRes.Venue, position: Int) {
                super.onEventSelected(item, position)
                venues[selectedVenue]
                selectedVenue = position
                venues[position].isSelected = true
            }
        }
}