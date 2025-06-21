package com.popiin.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterMyWhatsonBookBinding
import com.popiin.listener.AdapterEventListner
import com.popiin.listener.AdapterMyBookingListener
import com.popiin.res.MyWhatsonBookRes
import com.popiin.util.Common

class MyWhatsOnBookAdapter(
    private var myWhatsonBookData: ArrayList<MyWhatsonBookRes.Data>,
    private var listener: AdapterMyBookingListener<MyWhatsonBookRes.Data>,
) :
    BaseRVAdapter<AdapterMyWhatsonBookBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_my_whatson_book
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterMyWhatsonBookBinding>,
        position: Int,
    ) {
        holder.binding.model = myWhatsonBookData[position]
        holder.binding.common = Common.instance
        setStatusTextWithColor(holder, position)

        holder.binding.btnViewBookCode.setOnClickListener {
            listener.onViewBookingCode(myWhatsonBookData[position], position)
        }

        holder.binding.tvGetDirections.setOnClickListener {
            listener.onDirectionClick(myWhatsonBookData[position], position)
        }
    }

    override fun getItemCount(): Int {
        return myWhatsonBookData.size
    }

    private fun setStatusTextWithColor(
        holder: BaseViewHolder<AdapterMyWhatsonBookBinding>,
        position: Int,
    ) {
        when (myWhatsonBookData[position].status) {
            0 -> {
                holder.binding.llCancelBook.visibility = View.VISIBLE
                holder.binding.ivDashLine.visibility = View.VISIBLE
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
                holder.binding.llCancelBook.visibility = View.VISIBLE
                holder.binding.ivDashLine.visibility = View.VISIBLE
                holder.binding.tvStatus.text =
                    holder.itemView.context.getString(R.string.status_accepted)
                holder.binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorSwitch
                    )
                )
            }
            2 -> {
                holder.binding.llCancelBook.visibility = View.GONE
                holder.binding.ivDashLine.visibility = View.GONE
                holder.binding.tvStatus.text =
                    holder.itemView.context.getString((R.string.txt_cancel))
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