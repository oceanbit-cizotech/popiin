package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterWhatsonAttendeesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes

class WhatsOnAttendeesAdapter(
    private var venueAttendeeList: ArrayList<VenueListRes.Venue>,
    var listener: AdapterItemClickListener<VenueListRes.Venue?>,
) : BaseRVAdapter<AdapterWhatsonAttendeesBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_whatson_attendees
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterWhatsonAttendeesBinding>,
        position: Int,
    ) {
        holder.binding.venue = venueAttendeeList[position]

        holder.binding.clMain.setOnClickListener {
            listener.onAdapterItemClick(venueAttendeeList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return venueAttendeeList.size
    }
}