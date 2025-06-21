package com.popiin.adapter

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventPagerBinding
import com.popiin.util.Constant

class WhatsOnPreviewImageAdapter(var list:ArrayList<String> ,private val onItemClick: (Int) -> Unit) : BaseRVAdapter<AdapterEventPagerBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_event_pager
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterEventPagerBinding>, position: Int) {
        if (list.size > 0) {
            if (list[position].contains(Constant().pdf)) {
                holder.binding.imgImage.scaleType = ImageView.ScaleType.FIT_CENTER
                holder.binding.imgImage.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorSecondaryText
                    )
                )
                Glide.with(holder.itemView.context).load(R.drawable.ic_default_pdf).into(holder.binding.imgImage)
                holder.binding.imgImage.setOnClickListener {
                    onItemClick(position)
                }
            }
            else {
                holder.binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(holder.itemView.context).load(list[position])
                    .placeholder(R.drawable.default_image)
                    .into(holder.binding.imgImage)

                holder.binding.imgImage.setOnClickListener {
                    onItemClick(position)
                }
            }

        } else {
            holder.binding.imgImage.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorWhite
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return if (list.size > 0) list.size else 1
    }
}