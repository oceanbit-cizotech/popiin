package com.popiin.adapter

import android.annotation.SuppressLint
import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterMapListBinding
import com.popiin.listener.MapVenueListener
import com.popiin.res.VenueListRes.Venue
import com.popiin.util.Common
import com.popiin.util.Constant


class MapListAdapter(
    private var mapVenueList: ArrayList<Venue>,
    var listener: MapVenueListener<Venue>,
) : BaseRVAdapter<AdapterMapListBinding>() {
    var c: Common = Common.instance!!
    var venueType = ""
    override fun getLayout(): Int {
        return R.layout.adapter_map_list
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterMapListBinding>, position: Int) {
        holder.binding.model = mapVenueList[position]
        val topThreeVenueTypes = getTopThreeVenueTypeList(mapVenueList[position])

        if (topThreeVenueTypes.isEmpty()) {
            venueType = holder.itemView.context.getString(R.string.txt_not_available)
        } else {
            for (venueTypeString in topThreeVenueTypes) {
                venueType = (if (venueType.isEmpty()) "" else " ") + venueTypeString
            }
        }

        holder.binding.tvLocation.text =
            c.formatter.getOneDecimalFormatValue(mapVenueList[position].distance) + " ${Constant().miles}"

        holder.binding.tvVenueType.text = venueType

        if (mapVenueList[position].reservation_enabled == 1 && mapVenueList[position].tickets!!.isNotEmpty()) {
            holder.binding.tvBookNow.visibility = View.VISIBLE
        } else {
            holder.binding.tvBookNow.visibility = View.GONE
        }

        if (mapVenueList[position].venue_music != null) {
            if (mapVenueList[position].venue_music!!.isNotEmpty()) {
                holder.binding.tvRestro.text =
                    mapVenueList[position].venue_music!!.replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                        .replace(CONSTANT.SEPRATEOR, ", ")
            } else {
                holder.binding.tvRestro.visibility =View.INVISIBLE
            }
        }else{
            holder.binding.tvRestro.visibility =View.INVISIBLE
        }

        if (mapVenueList[position].images != null && mapVenueList[position].images!!.isNotEmpty() &&
            mapVenueList[position].images?.get(0)?.image!!.isNotEmpty()
        ) {
            c.loadImageFromUrl(
                holder.binding.ivMapList,
                mapVenueList[position].images?.get(0)?.image
            )
        } else if (mapVenueList[position].venue_banner_image != null && mapVenueList[position].venue_banner_image!!.isNotEmpty()) {
            c.loadImageFromUrl(holder.binding.ivMapList, mapVenueList[position].venue_banner_image)
        } else {
           // c.loadImageFromUrl(holder.binding.ivMapList, CONSTANT.POPIINIMAGE)
        }

        if (mapVenueList[position].isFavorite == 1) {
            holder.binding.cbLike.isSelected = true
        } else {
            holder.binding.cbLike.isSelected = false
        }

        holder.binding.cbLike.setOnClickListener {
            holder.binding.cbLike.isSelected = !holder.binding.cbLike.isSelected

            listener.onInfoFavoriteClick(mapVenueList[position], holder.binding.cbLike.isSelected)
        }

        holder.binding.root.setOnClickListener {
            listener.onInfoItemClick(mapVenueList[position], position)
        }

        holder.binding.tvGetDirections.setOnClickListener {
            listener.onInfoDirectionClick(mapVenueList[position])
        }

        holder.binding.tvBookNow.setOnClickListener {
            listener.onInfoBookNowClick(mapVenueList[position], position)
        }

        holder.binding.ivShare.setOnClickListener {
            listener.onInfoShareClick(mapVenueList[position], position)
        }

        val openCloseText = mapVenueList[position].getOpenCloseText()

        var isOfferLiveVisible=false
        for (item in mapVenueList[position].offerslive!!) {
            if (c.isOfferAvailable(item.working_day!!,item.open_time!!,item.close_time!!) == true) {
                isOfferLiveVisible=true
                break
            }
        }


        if (isOfferLiveVisible && openCloseText.isNotEmpty()) {
            holder.binding.tvOffer.visibility = View.VISIBLE
        } else {
            holder.binding.tvOffer.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mapVenueList.size
    }

    private fun getTopThreeVenueTypeList(venue: Venue): List<String?> {
        val venueTypeInString: MutableList<String?> = ArrayList()
        if (venue.venuetypes != null) {
            var i = 0
            while (i < venue.venuetypes.size && i < 3) {
                venueTypeInString.add(venue.venuetypes[0].venue_type)
                i++
            }
        }
        return venueTypeInString
    }
}