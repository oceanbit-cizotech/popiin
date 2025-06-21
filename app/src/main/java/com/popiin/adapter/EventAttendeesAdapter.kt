package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventAttendeesBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.EventListRes
import com.popiin.util.Common

class EventAttendeesAdapter(
    private var myEvents: ArrayList<EventListRes.Event?>,
    var listener: AdapterItemClickListener<EventListRes.Event?>,
) : BaseRVAdapter<AdapterEventAttendeesBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_event_attendees
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterEventAttendeesBinding>,
        position: Int,
    ) {
        holder.binding.model = myEvents[position]
        holder.binding.common = Common.instance

        holder.binding.clMain.setOnClickListener {
            listener.onAdapterItemClick(myEvents[position], position)
        }
    }

    override fun getItemCount(): Int {
        return myEvents.size
    }
}