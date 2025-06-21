package com.popiin.adapter

import android.annotation.SuppressLint
import com.popiin.model.VenueOffer
import com.popiin.listener.OfferListener
import com.popiin.BaseRVAdapter
import com.popiin.R
import com.popiin.BaseViewHolder
import com.popiin.databinding.AdapterOfferBinding
import com.popiin.listener.SearchTextListener
import java.util.ArrayList

class OfferAdapter(
    var venueOffers: ArrayList<VenueOffer>,
    private var offerListener: OfferListener<VenueOffer?>,
) : BaseRVAdapter<AdapterOfferBinding?>() {
    override fun getLayout(): Int {
        return R.layout.adapter_offer
    }

    override fun getItemCount(): Int {
        return venueOffers.size
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterOfferBinding?>,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.binding!!.offer = venueOffers[position]
        holder.binding!!.edtName.text = venueOffers[position].title.replace("null", "").replace(",", "")
        holder.binding!!.edtName.addTextChangedListener(object : SearchTextListener() {
            override fun performSearch(searchText: String?) {
                if (searchText!!.isNotEmpty()) {
                    venueOffers[position].title = searchText
                } else {
                    venueOffers[position].title = ""
                }
            }
        })


        holder.binding!!.ivClose.setOnClickListener {
            offerListener.onCloseClick(venueOffers[position], position)
        }
    }
}