package com.popiin.adapter

import android.annotation.SuppressLint
import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterDaytimeBinding
import com.popiin.listener.DayTimeChangeListener
import com.popiin.model.TimingModel
import com.popiin.util.Common

class DaysAdapter(
    var list: List<TimingModel>,
    private var timeChangeListener: DayTimeChangeListener<TimingModel?>,
) :
    BaseRVAdapter<AdapterDaytimeBinding?>() {
    var c: Common = Common.instance!!
    override fun getLayout(): Int {
        return R.layout.adapter_daytime
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder<AdapterDaytimeBinding?>, @SuppressLint("RecyclerView") position: Int) {
        holder.binding!!.days = list[position]


        if (list[position].open_time!!.isNotEmpty() && list[position].close_time!!.isEmpty()) {
            holder.binding?.tvError!!.visibility = View.VISIBLE
            holder.binding!!.tvError.text =
                holder.itemView.context.getString(R.string.txt_err_select_closing_hours)
        } else if (list[position].open_time!!.isEmpty() && list[position].close_time!!.isNotEmpty()) {
            holder.binding!!.tvError.visibility = View.VISIBLE
            holder.binding!!.tvError.text =
                holder.itemView.context.getString(R.string.txt_err_select_opening_hours)
        } else {
            holder.binding!!.tvError.visibility = View.GONE
        }

        if (!list[position].isClear) {
            holder.binding!!.imgClose.visibility = View.VISIBLE
        } else {
            holder.binding!!.imgClose.visibility = View.INVISIBLE
        }

        holder.binding!!.edtOpenTime.setOnClickListener {
            timeChangeListener.onAddOpenTime(list[position], position)
        }
        holder.binding!!.edtCloseTime.setOnClickListener {
            timeChangeListener.onAddCloseTime(list[position], position)
        }

        holder.binding!!.imgClose.setOnClickListener {
            timeChangeListener.onClearTime(true, position)
            holder.binding!!.imgClose.visibility = View.INVISIBLE
            holder.binding!!.edtCloseTime.setText("")
            holder.binding!!.edtOpenTime.setText("")
        }

    }
}