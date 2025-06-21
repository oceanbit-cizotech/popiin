package com.popiin.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.activity.ExploreDetailsFragment.Companion.exploreItem
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.RowVenueSearchBinding
import com.popiin.listener.VenueSearchItemClickListener
import com.popiin.model.VenueModel
import com.popiin.util.Common
import com.popiin.util.Constant
import java.util.*
import kotlin.collections.ArrayList

class VenueSearchAdapter(
    private var venueSearchList: ArrayList<VenueModel>,
    private var venueSearchListener: VenueSearchItemClickListener<VenueModel>,
) : BaseRVAdapter<RowVenueSearchBinding>() {
    var c: Common = Common.instance!!
    var venueType = ""

    var currentQuery = ""
    override fun getLayout(): Int {
        return R.layout.row_venue_search
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RowVenueSearchBinding>, position: Int) {
        holder.binding.tvEventName.text = venueSearchList[position].venue_name
        c.loadImageFromUrl(holder.binding.ivVenueImage, venueSearchList[position].venue_banner_image)

        holder.binding.root.setOnClickListener {
            venueSearchListener.onItemClick(venueSearchList[position])
        }

        if (venueSearchList[position].venue_music != null) {
            if (venueSearchList[position].venue_music!!.isNotEmpty()) {
                holder.binding.tvRestro.text = venueSearchList[position].venue_music!!.replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ").replace(CONSTANT.SEPRATEOR, ", ")
            } else {
                holder.binding.tvRestro.visibility = View.INVISIBLE
            }
        }else{
            holder.binding.tvRestro.visibility = View.INVISIBLE
        }
        val topThreeVenueType: List<String?> = c.getTopThreeVenueTypeList(venueSearchList[position])
        if (topThreeVenueType.isEmpty()) {
            venueType = holder.itemView.context.getString(R.string.txt_not_available)
        } else {
            for (venueTypeString in topThreeVenueType) {
                venueType = (if (venueType.isEmpty()) "" else " ") + venueTypeString
            }
        }
        holder.binding.tvVenueType.text = venueType

        holder.binding.tvLocation.text =
            venueSearchList[position].venue_distance?.let { c.formatter.getOneDecimalFormatValue(it) } + " ${Constant().miles}"
        holder.binding.tvGetDirections.setOnClickListener {
            venueSearchListener.onDirections(venueSearchList[position])
        }
    }

    override fun getItemCount(): Int {
        return venueSearchList.size
    }

    fun filter(query: String) {
        currentQuery = query.lowercase(Locale.getDefault())
      //  notifyDataSetChanged()
    }

    private fun String.formatTextWithQuery(query: String): SpannableStringBuilder {
        val spannableBuilder = SpannableStringBuilder(this)
        val lowerCaseQuery = query.lowercase(Locale.getDefault())

        var startIndex = this.lowercase(Locale.getDefault()).indexOf(lowerCaseQuery)
        while (startIndex >= 0) {
            val endIndex = startIndex + query.length
            // Apply bold style to the matched part
            spannableBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // Find the next occurrence of the query
            startIndex = this.indexOf(lowerCaseQuery, endIndex)
        }

        return spannableBuilder
    }

}