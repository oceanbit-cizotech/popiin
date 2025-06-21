package com.popiin.adapter

import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterAmenSubBinding
import com.popiin.databinding.AdapterVenueTypeBinding
import com.popiin.model.AmensSubModel


class VenueTypeAdapter(private var venueTypeList: ArrayList<String?>) :
    BaseRVAdapter<AdapterVenueTypeBinding>() {

    override fun getLayout(): Int {
        return R.layout.adapter_venue_type
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterVenueTypeBinding>, position: Int) {
            holder.binding.tvTag.text= venueTypeList.get(position).toString()
    }

    override fun getItemCount(): Int {
       return venueTypeList.size
    }

}