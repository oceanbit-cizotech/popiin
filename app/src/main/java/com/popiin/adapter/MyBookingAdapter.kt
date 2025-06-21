package com.popiin.adapter

import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterMyBookingPagerBinding
import com.popiin.listener.AdapterMyBookingListener
import com.popiin.res.MyBookingRes
import com.popiin.util.Common
import com.popiin.util.PrefManager

class MyBookingAdapter(
    private var myBooking: ArrayList<MyBookingRes.Data?>,
    private var adapterMyBookingListener: AdapterMyBookingListener<MyBookingRes.Data>,
) : BaseRVAdapter<AdapterMyBookingPagerBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_my_booking_pager
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterMyBookingPagerBinding>,
        position: Int,
    ) {
        holder.binding.model = myBooking[position]
        holder.binding.common = Common.instance
        holder.binding.username = PrefManager.getName()
            Common.instance!!.loadImageFromUrl(
            holder.binding.ivEvent,
            myBooking[position]!!.images!![0].image
        )

        if (myBooking[position]!!.special_request == null) {
            holder.binding.inclSpecialRequest.desc =
                holder.itemView.context.getString(R.string.txt_not_available)
            holder.binding.tvSpecialRequest.visibility = View.VISIBLE
        } else {
            holder.binding.inclSpecialRequest.desc = myBooking[position]!!.special_request
            holder.binding.tvSpecialRequest.visibility = View.GONE
        }

        holder.binding.tvGetDirections.setOnClickListener {
            adapterMyBookingListener.onDirectionClick(myBooking[position]!!, position)
        }

        holder.binding.tvViewBookingCode.setOnClickListener {
            adapterMyBookingListener.onViewBookingCode(myBooking[position]!!, position)
        }

        holder.binding.tvInviteFriends.setOnClickListener {
            adapterMyBookingListener.onInviteFriends(myBooking[position]!!, position)
        }

        holder.binding.tvSpecialRequest.setOnClickListener {
            adapterMyBookingListener.onAddSpecialRequirement(myBooking[position]!!, position)
        }

        holder.binding.tvAddToCalendar.setOnClickListener {
            adapterMyBookingListener.onAddToCalender(myBooking[position]!!, position)
        }
    }

    override fun getItemCount(): Int {
        return myBooking.size
    }
}