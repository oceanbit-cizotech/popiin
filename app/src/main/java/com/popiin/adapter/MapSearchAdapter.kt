package com.popiin.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.popiin.listener.MapSearchItemClickListener
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.RowMapSearchBinding
import com.popiin.res.VenueListRes
import java.util.*

class MapSearchAdapter(
    var list: ArrayList<VenueListRes.Venue>,
    var listener: MapSearchItemClickListener,
) : BaseRVAdapter<RowMapSearchBinding>() {
    var currentQuery = ""
    override fun getLayout(): Int {
        return R.layout.row_map_search
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RowMapSearchBinding>, position: Int) {
        holder.binding.lblTitle.text = list[position].venue_name!!.formatTextWithQuery(currentQuery)
        holder.binding.root.setOnClickListener {
            listener.onItemClick(
                list[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun filter(query: String) {
        currentQuery = query.lowercase(Locale.getDefault())
        notifyDataSetChanged()
    }

    private fun String.formatTextWithQuery(query: String): SpannableStringBuilder {
        val spannableBuilder = SpannableStringBuilder(this)
        val lowerCaseQuery = query.lowercase(Locale.getDefault())

        // Find all occurrences of the query in the text
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