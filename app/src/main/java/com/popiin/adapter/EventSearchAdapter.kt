package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventSearchBinding
import com.popiin.databinding.RowVenueSearchBinding
import com.popiin.listener.VenueSearchItemClickListener
import com.popiin.res.EventListRes
import com.popiin.util.Constant
import java.util.*
import kotlin.collections.ArrayList

class EventSearchAdapter(
    private var eventSearchList: ArrayList<EventListRes.Event?>,
    private var eventSearchListener: VenueSearchItemClickListener<EventListRes.Event?>,
) : BaseRVAdapter<AdapterEventSearchBinding>() {
    var currentQuery = ""
    override fun getLayout(): Int {
        return R.layout.adapter_event_search
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterEventSearchBinding>, position: Int) {
        if (eventSearchList[position]?.type.equals(Constant().whatson)) {
            holder.binding.lblTitle.text = eventSearchList[position]?.title
        }else{
            holder.binding.lblTitle.text = eventSearchList[position]?.name
        }
        holder.binding.root.setOnClickListener {
            eventSearchListener.onItemClick(eventSearchList[position])
        }
    }

    override fun getItemCount(): Int {
        return eventSearchList.size
    }

    fun filter(query: String) {
        currentQuery = query.lowercase(Locale.getDefault())
        notifyDataSetChanged()
    }

}