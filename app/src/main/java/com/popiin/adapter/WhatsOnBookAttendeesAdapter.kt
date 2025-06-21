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
import com.popiin.databinding.AdapterWhatsonBookAttendeesBinding
import com.popiin.listener.AdapterEventBookingAttendeesListener
import com.popiin.res.WhatsOnBookingAttendeesRes
import com.popiin.util.Common
import com.popiin.views.IndexAdapter
import com.popiin.views.Indexable
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter

class WhatsOnBookAttendeesAdapter(
    var bookingsData: ArrayList<WhatsOnBookingAttendeesRes.Data>,
    private var adapterVenueBookingAttendeesListener: AdapterEventBookingAttendeesListener<WhatsOnBookingAttendeesRes.Data>,
) : BaseRVAdapter<AdapterWhatsonBookAttendeesBinding>(),
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, IndexAdapter {
    var common = Common.instance
    override fun getLayout(): Int {
        return R.layout.adapter_whatson_book_attendees
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterWhatsonBookAttendeesBinding>,
        position: Int,
    ) {
        holder.binding.data = bookingsData[position]
        holder.binding.bc = common

        holder.binding.imgScanCode.setOnClickListener {
            if (bookingsData[position].is_scanned == 0) {
                adapterVenueBookingAttendeesListener.onScanCode(bookingsData[position], position)
            }
        }

        holder.binding.btnCancel.setOnClickListener {
            adapterVenueBookingAttendeesListener.onCancel(bookingsData[position], position)
        }

        if (bookingsData[position].status == 0) {
            holder.binding.tvStatus.text =
                holder.itemView.context.getString(R.string.txt_conformation_pending)
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorLightGrey))
            holder.binding.btnCancel.visibility = View.VISIBLE
        } else if (bookingsData[position].status == 1) {
            holder.binding.tvStatus.text =
                holder.itemView.context.getString(R.string.ba_status_confirm)
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorSwitch))
            holder.binding.btnCancel.visibility = View.VISIBLE
        } else if (bookingsData[position].status == 2) {
            holder.binding.tvStatus.text = holder.itemView.context.getString(R.string.txt_cancelled)
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorErrorText))
            holder.binding.btnCancel.visibility = View.GONE
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
                get() = "" + bookingsData[position].user!!.first_name!![0]
        }
    }
}