package com.popiin.adapter

import android.os.CountDownTimer
import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterFavouriteVenueBinding
import com.popiin.listener.AdapterEventListner
import com.popiin.res.VenueListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.PrefManager

class FavouriteVenueAdapter(
    private var exploreList: ArrayList<VenueListRes.Venue>,
    var listener: AdapterEventListner<VenueListRes.Venue?>,
) : BaseRVAdapter<AdapterFavouriteVenueBinding>() {
    private var c = Common.instance!!
    private var cdTimer: CountDownTimer? = null
    override fun getLayout(): Int {
        return R.layout.adapter_favourite_venue
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterFavouriteVenueBinding>,
        position: Int,
    ) {

        val topThreeVenueType: List<String?> = c.getTopThreeVenueTypeList(exploreList[position])
        var categoryList = ""
        categoryList =
            if (exploreList[position].venue_music != null && exploreList[position].venue_music!!.isNotEmpty()) {
                exploreList[position].venue_music!!.replace(
                    Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ").replace(CONSTANT.SEPRATEOR, ", ")
            } else {
                c.getCategoryListFromVenue(exploreList[position], topThreeVenueType)
            }
        var isMusic = false

        var venueType = ""

        // Whats on Logic Handling
        if (exploreList[position].whatsons!!.size > 0) {
            var isWhatson :Boolean=false
            for (watson in exploreList[position].whatsons!!) {
                isWhatson = c.dateInBitween(watson.what_datetime.toString(), watson.end_time.toString())
                if(isWhatson){
                    break
                }
            }
            if(isWhatson){
                holder.binding.tvWhatsOnTag.visibility = View.VISIBLE
            }else{
                holder.binding.tvWhatsOnTag.visibility = View.GONE
            }
        } else {
            holder.binding.tvWhatsOnTag.visibility = View.GONE
        }

        if (topThreeVenueType.isEmpty()) {
            venueType = holder.itemView.context.getString(R.string.txt_not_available)
        } else {
            for (venueTypeString in topThreeVenueType) {
                venueType += (if (venueType.isEmpty()) "" else ", ") + venueTypeString
            }
        }

        val openCloseText = exploreList[position].getOpenCloseText()
        val openCloseTiming = c.getOpenCloseTimingsWithTime(exploreList[position])

        holder.binding.common = c
        isMusic = exploreList[position].venue_music != null && exploreList[position].venue_music!!.isNotEmpty()
        holder.binding.isMusic = isMusic

        if (categoryList.isEmpty()) {
            categoryList = holder.itemView.context.getString(R.string.txt_not_available)
        }


        holder.binding.categoryList = categoryList.replace(Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, "")
        holder.binding.tvVenueType.text = venueType.replace(Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, "")

        if (openCloseText.equals(c.closedText)) {
            holder.binding.openClosedText = ""
        } else {
            holder.binding.openClosedText = holder.itemView.context.getString(R.string.txt_open_now)
        }

        if (exploreList[position].entertainment != null && exploreList[position].entertainment!!.isNotEmpty()) {
            holder.binding.tvEntertainment.visibility = View.VISIBLE
            holder.binding.ivVenueEntertainment.visibility = View.VISIBLE
            holder.binding.tvEntertainment.text = exploreList[position].entertainment!!.replace(
                Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                ", ").replace(CONSTANT.SEPRATEOR, ", ")
        } else {
            holder.binding.tvEntertainment.visibility = View.INVISIBLE
            holder.binding.ivVenueEntertainment.visibility = View.INVISIBLE
        }

        if (cdTimer != null) {
            cdTimer!!.cancel()
            cdTimer = null
        }

        if (openCloseTiming.contains(Constant().close)) {
            val venueTimes = openCloseTiming.split("")[1].toLong()
            cdTimer = object : CountDownTimer(venueTimes, 1000) {
                @Suppress("KotlinConstantConditions")
                override fun onTick(millisUntilFinished: Long) {
                    var returnText = holder.itemView.context.getString(R.string.txt_close_in)
                    val hours = (millisUntilFinished / (1000 * 60 * 60)).toInt()
                    val minutes = (millisUntilFinished / (1000 * 60)) % 60
                    val seconds = (millisUntilFinished / (1000))
                    val timeText: String
                    if (hours > 2) {
                        holder.binding.isTimeClose = false
                        timeText =
                            if (hours > 0) {
                                hours.toString() + (if (hours == 1) " " + Constant().hour else " " + Constant().hours)
                            } else "$minutes " + Constant().minutes
                    } else {
                        holder.binding.isTimeClose = true
                        timeText = hours.toString() + ":" + minutes + ":" + seconds % 60
                    }
                    returnText = returnText.replace(Constant().hash, timeText)
                    holder.binding.openClosedTime = returnText

                }

                override fun onFinish() {

                }
            }
            (cdTimer as CountDownTimer).start()
        } else {
            holder.binding.isTimeClose = false
            holder.binding.openClosedTime = openCloseTiming
        }


        if (exploreList[position].venue_banner_image != null && exploreList[position].venue_banner_image!!.isNotEmpty()) {
            c.loadImageFromUrl(holder.binding.ivImage, exploreList[position].venue_banner_image)
        } else if (exploreList[position].images != null && exploreList[position].images!!.isNotEmpty() && exploreList[position].images!![0].image!!.isNotEmpty()) {
            c.loadImageFromUrl(holder.binding.ivImage, exploreList[position].images!![0].image)
        } else {
         //   c.loadImageFromUrl(holder.binding.ivImage, CONSTANT.POPIINIMAGE)
        }


        if (exploreList[position].distance != null && exploreList[position].distance != 0.0) {
            holder.binding.distance = exploreList[position].distance
        } else {
            val distance = c.calculateDistanceInMiles(
                PrefManager.lastLocation!!.latitude,
                PrefManager.lastLocation!!.longitude,
                exploreList[position].venue_latitude!!.toDouble(),
                exploreList[position].venue_longitude!!.toDouble()
            )
            holder.binding.distance = distance
        }

        holder.binding.selected = exploreList[position].isSelected
        holder.binding.isFavorite = 1
        holder.binding.venueName = exploreList[position].venue_name


        if (exploreList[position].reservation_enabled == 1 && exploreList[position].tickets!!.isNotEmpty()) {
            holder.binding.btnBookNow.visibility = View.VISIBLE
            holder.binding.btnBookNow.setOnClickListener {
                listener.onBookNowClick(exploreList[position], position)
            }
        } else {
            holder.binding.btnBookNow.visibility = View.GONE
        }

        holder.binding.btnBookNow.setOnClickListener {
            listener.onBookNowClick(exploreList[position], position)
        }


        holder.binding.ivShare.setOnClickListener {
            listener.onShareClick(exploreList[position], position)
        }

        holder.binding.cbLike.setOnClickListener {
            listener.onFavouriteClick(
                exploreList[position],
                position,
                holder.binding.cbLike.isChecked
            )
        }


        holder.binding.root.setOnClickListener {
            listener.onItemClick(exploreList[holder.absoluteAdapterPosition], position)
        }


        // Offer Logic Handling
        var isOfferLiveVisible=false
        for (item in exploreList[position].offerslive!!) {
            if (c.isOfferAvailable(item.working_day!!,item.open_time!!,item.close_time!!) == true) {
                isOfferLiveVisible=true
                break
            }
        }

        if (isOfferLiveVisible && openCloseText.isNotEmpty()) {
            holder.binding.tvOfferTag.visibility = View.VISIBLE
        } else {
            holder.binding.tvOfferTag.visibility = View.GONE
        }

        // static code
        holder.binding.categoryList =holder.itemView.context.getString(R.string.txt_not_available)
        holder.binding.tvEntertainment.visibility = View.INVISIBLE
        holder.binding.ivVenueEntertainment.visibility = View.INVISIBLE

    }

    override fun getItemCount(): Int {
        return exploreList.size
    }
}