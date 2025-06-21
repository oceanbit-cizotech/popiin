package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterVenueOfferBinding

class VenueOfferAdapter(var offerList: ArrayList<String>) :
    BaseRVAdapter<AdapterVenueOfferBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_venue_offer
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterVenueOfferBinding>, position: Int) {
        holder.binding.tvTag.text = offerList[position]
    }

    override fun getItemCount(): Int {
        return offerList.size
    }
}