package com.popiin.adapter

import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterDemographicReportSummaryBinding
import com.popiin.res.SalesReportRes.Demographics_reports
import com.popiin.util.Common
import com.popiin.util.Constant
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class DemographicSummaryReportAdapter(
    var list: ArrayList<Demographics_reports>,
    private var totalBooking: Int,
) : BaseRVAdapter<AdapterDemographicReportSummaryBinding?>() {
    var c: Common = Common.instance!!
    init {
        list.sort()
    }


    override fun getLayout(): Int {
        return R.layout.adapter_demographic_report_summary
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterDemographicReportSummaryBinding?>, position: Int) {
       if(list[position].gender.equals("1")){
           holder.binding!!.name ="Male"
       }else if(list[position].gender.equals("2")){
           holder.binding!!.name ="Female"
       }else{
           holder.binding!!.name ="Other"
       }
        holder.binding!!.sold = "" + list[position].attendees
        holder.binding!!.revenue = c.currencySymbol + c.formatter.getDecimalFormatValue(list[position].total)
        holder.binding!!.calc = (list[position].attendees * 100 / totalBooking).toFloat().roundToInt()
        holder.binding!!.tvName.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                c.chartDemoColorsId[position]
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }


}