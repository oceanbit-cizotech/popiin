package com.popiin.adapter

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventPagerBinding
import com.popiin.listener.AdapterEventListener
import com.popiin.res.EventListRes
import com.popiin.util.Constant


class EventPagerAdapter : BaseRVAdapter<AdapterEventPagerBinding> {
    var eventPagerList = ArrayList<EventListRes.Images>()
    var whatsPagerList = ArrayList<EventListRes.Whatsonimages>()
    lateinit var event: EventListRes.Event
    lateinit var listener: AdapterEventListener<EventListRes.Event?>

    constructor(
        eventPagerList: ArrayList<EventListRes.Images>,
        event: EventListRes.Event,
        listener: AdapterEventListener<EventListRes.Event?>,
    ) {
        this.eventPagerList = eventPagerList
        this.event = event
        this.listener = listener
    }

    constructor(
        event: EventListRes.Event,
        whatsPagerList: ArrayList<EventListRes.Whatsonimages>,
        listener: AdapterEventListener<EventListRes.Event?>,
    ) {
        this.whatsPagerList = whatsPagerList
        this.event = event
        this.listener = listener
    }

    override fun getLayout(): Int {
        return R.layout.adapter_event_pager
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterEventPagerBinding>, position: Int) {
        if (eventPagerList.size > 0) {
            setImageWithType(holder, eventPagerList.get(position).image)
        } else {
            setImageWithType(holder, whatsPagerList.get(position).image_url!!)
        }

        holder.binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
        holder.binding.imgImage.setOnClickListener {
            listener.onItemClick(event, position)
        }
    }

    override fun getItemCount(): Int {
        return if (eventPagerList.size > 0) eventPagerList.size else whatsPagerList.size
    }


    fun setImageWithType(holder: BaseViewHolder<AdapterEventPagerBinding>, image: String) {
        if (image.contains(Constant().pdf)) {
            Glide.with(holder.itemView.context)
                .load(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_pdf))
                .into(holder.binding.imgImage)
        } else {
            Glide.with(holder.itemView.context).load(image)
                .into(holder.binding.imgImage)
        }
    }

}