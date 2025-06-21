package com.popiin.adapter

import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.LayoutWithImagesBinding
import com.popiin.listener.AdapterItemClickListener


class EventImageAdapter(var list: ArrayList<String?>, var listener: AdapterItemClickListener<String?>) :
    BaseRVAdapter<LayoutWithImagesBinding>() {
    override fun getLayout(): Int {
        return R.layout.layout_with_images
    }

    override fun onBindViewHolder(holder: BaseViewHolder<LayoutWithImagesBinding>, position: Int) {
        Glide.with(holder.itemView.context).load(list[position]).into(holder.binding.imgEvent)

        holder.binding.imgClose.setOnClickListener {
            listener.onEventImageCloseClick(list[position],position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
