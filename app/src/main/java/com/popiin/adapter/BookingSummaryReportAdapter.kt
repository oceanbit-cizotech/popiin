package com.popiin.adapter

import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterBookingSummaryReportBinding
import com.popiin.res.SalesReportRes.Ticket_types
import com.popiin.util.Common
import kotlin.math.roundToInt


class BookingSummaryReportAdapter(
    private var list: List<Ticket_types>,
    private var totalBooking: Int,
) : BaseRVAdapter<AdapterBookingSummaryReportBinding>() {
    var c: Common = Common.instance!!
    override fun getLayout(): Int {
        return R.layout.adapter_booking_summary_report
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterBookingSummaryReportBinding>,
        position: Int,
    ) {
        holder.binding.name = list[position].booking_type
        holder.binding.sold = "" + list[position].total_num
        holder.binding.revenue =
            c.currencySymbol + c.formatter.getDecimalFormatValue(list[position].total)
        holder.binding.calc = (list[position].total_num * 100 / totalBooking).toFloat().roundToInt()
        holder.binding.tvName.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                c.chartColorsId[position % 4]
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }
}