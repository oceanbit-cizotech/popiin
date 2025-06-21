package com.popiin.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterMyVenueBookBinding
import com.popiin.listener.AdapterMyVenueBookListener
import com.popiin.res.VenueAttendeesRes
import com.popiin.util.Common
import com.popiin.util.PrefManager


class MyVenueBookAdapter(
    private var venusList: ArrayList<VenueAttendeesRes.Data?>,
    var adapterMyVenuesListener: AdapterMyVenueBookListener<VenueAttendeesRes.Data>,
) : BaseRVAdapter<AdapterMyVenueBookBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_my_venue_book
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterMyVenueBookBinding>,
        position: Int
    ) {
        holder.binding.model = venusList[position]
        holder.binding.common = Common.instance
        holder.binding.username =PrefManager.getName()
        if (venusList[position]!!.venue != null) {
            if (venusList[position]!!.venue!!.venue_banner_image != null) {
                Common.instance!!.loadImageFromUrl(
                    holder.binding.ivEvent,
                    venusList[position]!!.venue!!.venue_banner_image
                )
            }
        }


        holder.binding.tvGetDirections.setOnClickListener {
            adapterMyVenuesListener.onDirectionClick(venusList[position]!!, position)
        }

        holder.binding.tvViewBookingCode.setOnClickListener {
            adapterMyVenuesListener.onViewBookingCode(venusList[position]!!, position)
        }

        holder.binding.tvInviteFriends.setOnClickListener {
            adapterMyVenuesListener.onInviteFriends(venusList[position]!!, position)
        }

        holder.binding.tvAddToCalendar.setOnClickListener {
            adapterMyVenuesListener.onAddToCalender(venusList[position]!!, position)
        }

        holder.binding.tvCancel.setOnClickListener {
            adapterMyVenuesListener.onCancelClick(venusList[position]!!, position)
        }

        holder.binding.tvSpecialRequest.setOnClickListener{
           // adapterMyVenuesListener.onAddSpecialRequirement(venusList[position]!!, position)

        }

        setStatusTextWithColor(holder, position)
    }

    override fun getItemCount(): Int {
        return venusList.size
    }

    private fun setStatusTextWithColor(
        holder: BaseViewHolder<AdapterMyVenueBookBinding>,
        position: Int
    ) {
        when (venusList[position]!!.status) {
            0 -> {
                holder.binding.tvViewBookingCode.visibility = View.GONE
                holder.binding.tvCancel.visibility = View.VISIBLE
                holder.binding.mbStatus.desc =
                    holder.itemView.context.getString(R.string.status_pending)
                holder.binding.mbStatus.tvDesc.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorValue
                    )
                )
            }
            1 -> {
                holder.binding.tvViewBookingCode.visibility = View.VISIBLE
                holder.binding.tvCancel.visibility = View.VISIBLE
                holder.binding.mbStatus.desc =
                    holder.itemView.context.getString(R.string.status_accepted)
                holder.binding.mbStatus.tvDesc.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorSwitch
                    )
                )
            }
            2 -> {
                holder.binding.tvViewBookingCode.visibility = View.GONE
                holder.binding.tvCancel.visibility = View.GONE
                holder.binding.mbStatus.desc =
                    holder.itemView.context.getString((R.string.status_cancelled))
                holder.binding.mbStatus.tvDesc.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorErrorText
                    )
                )
            }
        }
    }

}