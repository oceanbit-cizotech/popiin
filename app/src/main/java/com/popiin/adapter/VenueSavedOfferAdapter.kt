package com.popiin.adapter

import android.annotation.SuppressLint
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterVenueSavedOfferBinding
import com.popiin.listener.AdapterOfferListener
import com.popiin.res.VenueListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import java.util.ArrayList

class VenueSavedOfferAdapter(var offers: ArrayList<VenueListRes.Offerslive>, var listener:AdapterOfferListener<VenueListRes.Offerslive>) : BaseRVAdapter<AdapterVenueSavedOfferBinding>() {
    var c = Common.instance
    override fun getLayout(): Int {
        return R.layout.adapter_venue_saved_offer
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterVenueSavedOfferBinding>, position: Int) {
        holder.binding.model = offers[position]

        val startTime =
            c!!.convertDateInFormat(offers[position].open_time, Constant().HHmmss24hrs, Constant().hhMmA)
        val endTime = c!!.convertDateInFormat(offers[position].close_time,
            Constant().HHmmss24hrs,
            Constant().hhMmA)
        holder.binding.tvTime.text = "$startTime - $endTime"

        holder.binding.clMain.setOnClickListener {
            listener.onSavedOfferClick(offers[position], position)
        }

        holder.binding.ivDeleteOffers.setOnClickListener {
            listener.onSavedOfferDeleteClick(offers[position], position)
        }

    }

    override fun getItemCount(): Int {
        return offers.size
    }
}