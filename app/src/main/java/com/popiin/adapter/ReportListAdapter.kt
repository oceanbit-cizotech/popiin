package com.popiin.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.model.ReportListModel
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterReportListBinding
import com.popiin.listener.AdapterReportListItemListner
import com.popiin.util.Common
import com.popiin.util.Constant

class ReportListAdapter(
    var list: List<ReportListModel> = ArrayList(),
    private var listener: AdapterReportListItemListner<ReportListModel?>,
    var selectedVenue: Int=-1,
): BaseRVAdapter<AdapterReportListBinding?>() {
    var c: Common = Common.instance!!

    override fun getLayout(): Int {
        return R.layout.adapter_report_list
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterReportListBinding?>,
        position: Int) {
        holder.binding!!.model = list[position]
        holder.binding!!.clMain.setOnClickListener {
            selectedVenue = position
            if (list[position].isSelected) {
                list[position].isSelected = false
                holder.binding!!.clMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorSemiGrey
                    )
                )
                holder.binding!!.cbItem.isChecked=false
            } else {
                list[position].isSelected = true
                holder.binding!!.clMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorWhite
                    )
                )
                holder.binding!!.cbItem.isChecked=true
            }
            listener.onItemSelect(list[position], position, list[position].isSelected)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}