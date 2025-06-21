package com.popiin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventBookingAttendeesBinding
import com.popiin.listener.AdapterEventBookingAttendeesListener
import com.popiin.res.EventBookingAttendeesRes
import com.popiin.util.Common
import com.popiin.views.IndexAdapter
import com.popiin.views.Indexable
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter

class EventBookAttendeesAdapter(
    var bookingsData: ArrayList<EventBookingAttendeesRes.Data>,
    private var adapterEventBookingAttendeesListener: AdapterEventBookingAttendeesListener<EventBookingAttendeesRes.Data>,
) : BaseRVAdapter<AdapterEventBookingAttendeesBinding>(),
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, IndexAdapter {
    var common = Common.instance
    override fun getLayout(): Int {
        return R.layout.adapter_event_booking_attendees
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterEventBookingAttendeesBinding>,
        position: Int
    ) {
        holder.binding.data = bookingsData[position]
        holder.binding.bc = common

        holder.binding.imgScanCode.setOnClickListener {
            if (bookingsData[position].is_scanned == 0) {
                adapterEventBookingAttendeesListener.onScanCode(bookingsData[position], position)
            }
        }

        if (bookingsData[position].is_scanned == 0) {
            holder.binding.imgScanCode.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.colorScanCodeTrans), android.graphics.PorterDuff.Mode.MULTIPLY)
        } else {
            holder.binding.imgScanCode.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.colorBlue), android.graphics.PorterDuff.Mode.MULTIPLY)
        }


    }

    override fun getItemCount(): Int {
        return bookingsData.size
    }

    override fun getHeaderId(i: Int): Long {
        return bookingsData[i].user!!.indexCharter.code.toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.adapter_contact_header, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val textView = holder!!.itemView as TextView
        val showValue: String = java.lang.String.valueOf(bookingsData[position].user!!.indexCharter)
        textView.text = showValue
    }

    override fun getItem(position: Int): Indexable {
        return object : Indexable {
            override val index: String
                get() = ""+ bookingsData[position].user!!.first_name!![0]
        }
    }
}