package com.popiin.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterVenueTypeTagBinding
import com.popiin.res.VenueListRes
import com.popiin.flowlayout.FlowLayout
import com.popiin.flowlayout.TagAdapter


class VenueTypeTagAdapter(var venueTypeTagList: ArrayList<VenueListRes.Venuecategories>) :
    BaseRVAdapter<AdapterVenueTypeTagBinding>() {
    private lateinit var amenSubAdapter: ExploreVenueCategoryTagAdapter
    override fun getLayout(): Int {
        return R.layout.adapter_venue_type_tag
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterVenueTypeTagBinding>,
        position: Int,
    ) {
        holder.binding.model = venueTypeTagList[position]

        val mVals = venueTypeTagList[position].category_name!!.split(CONSTANT.SEPRATEOR)

        println("onBindViewHolder : venueType : " + venueTypeTagList[position].venue_type)

        amenSubAdapter = ExploreVenueCategoryTagAdapter(venueTypeTagList)

        if (position == venueTypeTagList.size - 1) {
            holder.binding.tagFlowView.visibility = View.GONE
        } else {
            holder.binding.tagFlowView.visibility = View.VISIBLE
        }

        holder.binding.flowlayout.adapter = object :
            com.popiin.flowlayout.TagAdapter<String?>(mVals) {
            override fun getView(parent: com.popiin.flowlayout.FlowLayout?, position: Int, t: String?): View {
                val view = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.adapter_amen_sub, parent, false)
                val tv = view.findViewById<TextView>(R.id.tv_tag)
                val iv = view.findViewById<ImageView>(R.id.img_tag)

                tv.text = mVals[position]
                iv.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.ic_music))
                return view
            }

        }.also { amenSubAdapter }

    }

    override fun getItemCount(): Int {
        return venueTypeTagList.size
    }
}