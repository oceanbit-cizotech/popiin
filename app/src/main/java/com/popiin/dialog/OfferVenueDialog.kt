package com.popiin.dialog

import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.DialogChooseVenueOfferBinding
import com.popiin.res.VenueListRes
import java.util.ArrayList

class OfferVenueDialog(var venues: ArrayList<VenueListRes.Venue>) : BaseBottomSheetDialog<DialogChooseVenueOfferBinding>() {
    override fun getLayout(): Int {
        return R.layout.dialog_choose_venue_offer
    }

    override fun initViews() {

//        binding!!.title = getString(R.string.select_venue)
//
//        offerVenueAdapter = OfferVenueAdapter(venues)
//        binding!!.rvVenues.layoutManager = LinearLayoutManager(mActivity,
//            LinearLayoutManager.VERTICAL,false)
//        binding!!.rvVenues.adapter = offerVenueAdapter
//
//        binding!!.ivClose.setOnClickListener {
//            dialog!!.dismiss()
//        }
    }

    override fun onApiFailure(t: Throwable) {
        TODO("Not yet implemented")
    }


}