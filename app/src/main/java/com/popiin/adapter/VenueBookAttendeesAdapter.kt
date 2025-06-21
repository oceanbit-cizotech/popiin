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
import com.popiin.databinding.AdapterMyWhatsonBookBinding
import com.popiin.databinding.AdapterVenueBookAttendeesBinding
import com.popiin.listener.AdapterEventBookingAttendeesListener
import com.popiin.res.VenueBookingAttendeeRes
import com.popiin.util.Common
import com.popiin.views.IndexAdapter
import com.popiin.views.Indexable
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter

class VenueBookAttendeesAdapter(
    var bookingsData: ArrayList<VenueBookingAttendeeRes.Data>,
    private var adapterVenueBookingAttendeesListener: AdapterEventBookingAttendeesListener<VenueBookingAttendeeRes.Data>,
) :
    BaseRVAdapter<AdapterVenueBookAttendeesBinding>(),
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, IndexAdapter {
    var common = Common.instance
    override fun getLayout(): Int {
        return R.layout.adapter_venue_book_attendees
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterVenueBookAttendeesBinding>,
        position: Int
    ) {
        holder.binding.data = bookingsData[position]
        holder.binding.bc = common

        holder.binding.imgScanCode.setOnClickListener {
            if (bookingsData[position].is_scanned == 0) {
                adapterVenueBookingAttendeesListener.onScanCode(bookingsData[position], position)
            }
        }

        holder.binding.tvDecline.setOnClickListener {
            adapterVenueBookingAttendeesListener.onRejectClick(bookingsData[position], position)
        }

        holder.binding.btnAccept.setOnClickListener {
            adapterVenueBookingAttendeesListener.onAcceptClick(bookingsData[position], position)
        }

        setStatusTextWithColor(holder, position)
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
                get() = "" + bookingsData[position].user!!.first_name!![0]
        }
    }

    private fun setStatusTextWithColor(
        holder: BaseViewHolder<AdapterVenueBookAttendeesBinding>,
        position: Int,
    ) {
        when (bookingsData[position].status) {
            0 -> {
                holder.binding.llDeclineBook.visibility = View.VISIBLE
                holder.binding.tvStatus.text =
                    holder.itemView.context.getString(R.string.status_pending)
                holder.binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorValue
                    )
                )
            }
            1 -> {
                holder.binding.llDeclineBook.visibility = View.GONE
                holder.binding.tvStatus.text =
                    holder.itemView.context.getString(R.string.ba_status_confirm)
                holder.binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorSwitch
                    )
                )
            }
            2 -> {
                holder.binding.llDeclineBook.visibility = View.GONE
                holder.binding.tvStatus.text =
                    holder.itemView.context.getString((R.string.txt_declined))
                holder.binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorErrorText
                    )
                )
            }
        }
    }
}