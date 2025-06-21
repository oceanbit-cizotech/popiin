package com.popiin.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterOfferPreviewBinding
import com.popiin.res.VenueListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class OfferPreviewAdapter(
    var list: ArrayList<VenueListRes.Offerslive>,
) : BaseRVAdapter<AdapterOfferPreviewBinding>() {
    var c : Common = Common.instance!!
    override fun getLayout(): Int {
        return R.layout.adapter_offer_preview
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterOfferPreviewBinding>,
        position: Int
    ) {
        val today = LocalDate.now()
        holder.binding.inclOfferDetails.value = list[position].title
        holder.binding.inclOfferStarts.value =
            c.convertDateInFormat(list[position].open_time, Constant().HHmmss24hrs, Constant().hhMmA)
        holder.binding.inclOfferEnds.value =
            c.convertDateInFormat(list[position].close_time, Constant().HHmmss24hrs, Constant().hhMmA)
        holder.binding.inclOfferDay.value = list[position].working_day

//        if (c.getOpenCloseText(venueData!!) == Common.instance!!.CLOSED_TEXT) {
//            holder.binding.inclOfferStatus.value = c.getOpenCloseText(venueData!!)
//            holder.binding.inclOfferStatus.tvOfferValue.setTextColor(
//                ContextCompat.getColor(
//                    holder.itemView.context,
//                    R.color.colorErrorText
//                )
//            )
//        } else {
//            holder.binding.inclOfferStatus.value = c.getOpenCloseText(venueData!!)
//            holder.binding.inclOfferStatus.tvOfferValue.setTextColor(
//                ContextCompat.getColor(
//                    holder.itemView.context,
//                    R.color.colorSwitch
//                )
//            )
//        }

        val calendar = Calendar.getInstance()
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        val currentTime = String.format("%02d:%02d:%02d", currentHour, currentMinute, second)

        val time1: LocalTime = LocalTime.parse(list[position].open_time)
        val time2: LocalTime = LocalTime.parse(list[position].close_time)
        val currentLocalTime: LocalTime = LocalTime.parse(currentTime)


        if (c.convertDateInFormat(
                today.toString(),
                Constant().yyyyMmDd,
                Constant().eeee
            ) == list[position].working_day
        ) {
            if (time1.isBefore(currentLocalTime) && time2.isAfter(currentLocalTime)) {
                holder.binding.inclOfferStatus.value =
                    holder.itemView.context.getString(R.string.txt_open_now_plain)
                holder.binding.inclOfferStatus.tvOfferValue.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorSwitch
                    )
                )
            } else {
                holder.binding.inclOfferStatus.value =
                    holder.itemView.context.getString(R.string.txt_close_now)
                holder.binding.inclOfferStatus.tvOfferValue.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorErrorText
                    )
                )
            }
        } else {
            holder.binding.inclOfferStatus.value =
                holder.itemView.context.getString(R.string.txt_close_now)
            holder.binding.inclOfferStatus.tvOfferValue.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorErrorText
                )
            )
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}