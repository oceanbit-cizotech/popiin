package com.popiin.adapter

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterDialogWhatsOnBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes
import com.popiin.util.Common
import com.popiin.util.Constant

class WhatsOnDialogAdapter(
    private var whatsOnList: ArrayList<VenueListRes.Whatson>,
    var listener: AdapterItemClickListener<VenueListRes.Whatson>,
) : BaseRVAdapter<AdapterDialogWhatsOnBinding>() {
    var c = Common.instance!!

    override fun getLayout(): Int {
        return R.layout.adapter_dialog_whats_on
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterDialogWhatsOnBinding>, position: Int) {
//        holder.binding.common = Common.instance
//        holder.binding.model = whatsOnList[position]

        if (whatsOnList[position].whatsonimages!!.isNotEmpty()) {
            holder.binding.ivWhatsOn.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(holder.itemView.context)
                .load(whatsOnList[position].whatsonimages!![0].image_url)
                .into(holder.binding.ivWhatsOn)
            holder.binding.tvDateTime.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorWhite
                )
            )
        } else {
            holder.binding.ivWhatsOn.scaleType = ImageView.ScaleType.CENTER_INSIDE
            Glide.with(holder.itemView.context)
                .load(R.drawable.default_image)
                .into(holder.binding.ivWhatsOn)
            holder.binding.tvDateTime.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorSemiGrey
                )
            )
        }


        holder.binding.tvDateTime.text =
            c.convertDateInFormat(
                whatsOnList[position].what_datetime,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmmYyyy
            )!! + " • " +
                    c.convertDateInFormat(
                        whatsOnList[position].what_datetime,
                        Constant().yyyyMmDdHhMmSs,
                        Constant().hhMmA
                    )!!

        if (whatsOnList[position].music != null) {
            holder.binding.tvMusic.visibility = View.VISIBLE
            holder.binding.tvMusic.text =
                if (whatsOnList[position].music != null) whatsOnList[position].music!!
                    .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", ")
                    .replace(
                        CONSTANT.SEPRATEOR,
                        ", "
                    ) else holder.itemView.context.getString(R.string.txt_not_available)
        } else {
            holder.binding.tvMusic.visibility = View.INVISIBLE
        }


        if (whatsOnList[position].entertainment != null) {
            holder.binding.tvEntertainment.visibility = View.VISIBLE
            holder.binding.tvEntertainment.text =
                if (whatsOnList[position].entertainment != null) whatsOnList[position].entertainment!!
                    .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", ")
                    .replace(
                        CONSTANT.SEPRATEOR,
                        ", "
                    ) else holder.itemView.context.getString(R.string.txt_not_available)
        } else {
            holder.binding.tvEntertainment.visibility = View.INVISIBLE
        }



        holder.binding.tvViewMore.setOnClickListener {
            listener.onAdapterItemClick(whatsOnList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return whatsOnList.size
    }
}