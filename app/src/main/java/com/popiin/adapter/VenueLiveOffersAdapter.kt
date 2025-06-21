package com.popiin.adapter

import com.popiin.listener.OfferListener
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterVenueLiveOffersBinding
import com.popiin.res.VenueListRes

class VenueLiveOffersAdapter(
    private var offerslive: List<VenueListRes.Offerslive>,
    private var offerListener: OfferListener<VenueListRes.Offerslive>
) : BaseRVAdapter<AdapterVenueLiveOffersBinding>(){
    override fun getLayout(): Int {
        return R.layout.adapter_venue_live_offers
    }

    override fun getItemCount(): Int {
        return offerslive.size
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterVenueLiveOffersBinding>,
        position: Int) {
        holder.binding.title=offerslive.get(position).title
        holder.binding.btn.setOnClickListener {
            offerListener.onItemClick(offerslive.get(position),position)
        }
    }

}