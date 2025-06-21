package com.popiin.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterOfferVenueBinding
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.res.VenueListRes
import java.util.ArrayList

class OfferVenueAdapter(var venues: ArrayList<VenueListRes.Venue>, var listener:AdapterMyVenuesListener<VenueListRes.Venue>,var selectedPosition:Int=0) : BaseRVAdapter<AdapterOfferVenueBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_offer_venue
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterOfferVenueBinding>, position: Int) {
        holder.binding.tvFilter.setText(venues[position].venue_name)

        /*if (venues[position].isSelected){
            selectedPosition = position
        }*/

        setItem(holder,selectedPosition)

        holder.itemView.setOnClickListener {

            if(selectedPosition!=holder.absoluteAdapterPosition){
                venues[selectedPosition].isSelected=false
                venues[position].isSelected = true
                selectedPosition = holder.absoluteAdapterPosition
                notifyDataSetChanged()
            }
        }

        holder.binding.checkbox.setOnClickListener {
            if(selectedPosition!=holder.absoluteAdapterPosition){
                venues[selectedPosition].isSelected=false
                venues[position].isSelected = true
                selectedPosition = holder.absoluteAdapterPosition
                notifyDataSetChanged()
            }
        }
    }

    private fun setItem(holder: BaseViewHolder<AdapterOfferVenueBinding>, selectedPosition: Int) {
        if (selectedPosition == holder.absoluteAdapterPosition) {
            holder.binding.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorWhite))
            holder.binding.checkbox.isChecked = true
            listener.onEventSelected(venues[selectedPosition], selectedPosition)
        } else {
            holder.binding.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.colorSemiGrey))
            holder.binding.checkbox.isChecked = false
        }
    }

    override fun getItemCount(): Int {
        return venues.size
    }
}