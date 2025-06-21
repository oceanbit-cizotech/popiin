package com.popiin.adapter

import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterNotificationsBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.NotificationsRes
import com.popiin.util.Common

class NotificationAdapter(
    var list: ArrayList<NotificationsRes.Data>,
    var listener: AdapterItemClickListener<NotificationsRes.Data>,
) : BaseRVAdapter<AdapterNotificationsBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_notifications
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterNotificationsBinding>,
        position: Int,
    ) {
        holder.binding.model = list[position]
        holder.binding.common = Common.instance

        holder.binding.ivDelete.setOnClickListener {
            listener.onAdapterDeleteClick(list[position], position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}