package com.popiin.adapter

import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterMyEventsBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.EventListRes
import com.popiin.util.Common

class MyEventsAdapter(
    private var myEvents: ArrayList<EventListRes.Event?>,
    var listener: AdapterItemClickListener<EventListRes.Event?>,
) : BaseRVAdapter<AdapterMyEventsBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_my_events
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterMyEventsBinding>, position: Int) {
        holder.binding.model = myEvents[position]
        holder.binding.common = Common.instance

        if (myEvents[position]!!.is_draft == 0 && myEvents[position]!!.is_public == 0) {
            holder.binding.tvPrivate.visibility = View.VISIBLE
        } else {
            holder.binding.tvPrivate.visibility = View.GONE
        }

        if (myEvents[position]!!.is_draft == 0 && myEvents[position]!!.id > 0) {
            holder.binding.clSavedEvent.visibility = View.GONE
            holder.binding.clCopyLink.visibility = View.VISIBLE
            holder.binding.frVenueCopy.visibility = View.VISIBLE
        } else {
            holder.binding.clSavedEvent.visibility = View.VISIBLE
            holder.binding.clCopyLink.visibility = View.GONE
            holder.binding.frVenueCopy.visibility = View.GONE
        }

        holder.binding.ivEditVenue.setOnClickListener {
            listener.onAdapterItemClick(myEvents[position], position)
        }

        holder.binding.ivDeleteVenue.setOnClickListener {
            listener.onEventDeleteClick(myEvents[position], position)
        }

        holder.binding.inclVenueCopyLink.root.setOnClickListener {
            listener.onEventCopyClick(myEvents[position], position)
        }

        holder.binding.inclShareFriends.root.setOnClickListener {
            listener.onEventShareClick(myEvents[position], position)
        }

        holder.binding.frVenueCopy.setOnClickListener {
            myEvents[position]!!.id = 0
            listener.onEventItemCopyClick(myEvents[position], position)
        }
    }

    override fun getItemCount(): Int {
        return myEvents.size
    }
}