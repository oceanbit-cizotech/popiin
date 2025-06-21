package com.popiin.adapter

import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterVenueSummBinding

class VenueSumImageAdapter(private var venueImages: Array<String?>?) :
    BaseRVAdapter<AdapterVenueSummBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_venue_summ
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterVenueSummBinding>, position: Int) {
        Glide.with(holder.itemView.context).load(venueImages!![position])
            .into(holder.binding.ivSummImage)
    }

    override fun getItemCount(): Int {
        return venueImages!!.size
    }
}