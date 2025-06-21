package com.popiin.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterMyVenuesBinding
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.res.VenueListRes

class MyVenuesAdapter(
    var list: ArrayList<VenueListRes.Venue>,
    private var venuesListener: AdapterMyVenuesListener<VenueListRes.Venue?>,
) : BaseRVAdapter<AdapterMyVenuesBinding?>() {
    override fun getLayout(): Int {
        return R.layout.adapter_my_venues
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterMyVenuesBinding?>, position: Int) {
        holder.binding!!.venue = list[position]

        if (list[position].venue_is_draft == 0 && list[position].venue_is_public == 0) {
            holder.binding!!.tvPrivate.visibility = View.VISIBLE
        } else {
            holder.binding!!.tvPrivate.visibility = View.GONE
        }

        if (list[position].venue_is_draft == 0) {
            holder.binding!!.clSaved.visibility = View.GONE
            holder.binding!!.tvPublished.visibility = View.VISIBLE
            holder.binding!!.clCopyLink.visibility = View.VISIBLE
            holder.binding!!.llDocVerify.visibility = View.VISIBLE
            holder.binding!!.venueCopy.root.visibility=View.GONE
            holder.binding!!.frVenueCopy.visibility = View.VISIBLE

        } else {
            holder.binding!!.venueCopy.root.visibility=View.VISIBLE
            holder.binding!!.clSaved.visibility = View.VISIBLE
            holder.binding!!.tvPublished.visibility = View.GONE
            holder.binding!!.clCopyLink.visibility = View.GONE
            holder.binding!!.llDocVerify.visibility = View.GONE
            holder.binding!!.frVenueCopy.visibility = View.GONE
        }

        if (list[position].document != null) {
            when (list[position].document!!.verificationStatus) {
                0 -> {
                    holder.binding!!.inclDocVerify.tvDocumentStatus.visibility = View.VISIBLE
                    holder.binding!!.inclDocVerify.tvDocumentStatus.text =
                        holder.itemView.context.getString(R.string.txt_doc_pending)
                    holder.binding!!.llDocVerify.isEnabled = true
                    holder.binding!!.inclDocVerify.tvDocumentStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.colorEdit
                        )
                    )
                    holder.binding!!.inclDocVerify.ivDocVerify.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.itemView.context,
                            R.drawable.ic_doc_verify_pending
                        )
                    )
                }

                1 -> {
                    holder.binding!!.inclDocVerify.tvDocumentStatus.visibility = View.VISIBLE
                    holder.binding!!.inclDocVerify.tvDocumentStatus.text =
                        holder.itemView.context.getString(R.string.txt_doc_accepted)
                    holder.binding!!.llDocVerify.isEnabled = false
                    holder.binding!!.inclDocVerify.tvDocumentStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.colorSwitch
                        )
                    )
                    holder.binding!!.inclDocVerify.ivDocVerify.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.itemView.context,
                            R.drawable.ic_doc_verify_accept
                        )
                    )
                }

                2 -> {
                    holder.binding!!.inclDocVerify.tvDocumentStatus.visibility = View.VISIBLE
                    holder.binding!!.inclDocVerify.tvDocumentStatus.text =
                        holder.itemView.context.getString(R.string.txt_doc_rejected)
                    holder.binding!!.llDocVerify.isEnabled = false
                    holder.binding!!.inclDocVerify.tvDocumentStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.colorErrorText
                        )
                    )
                    holder.binding!!.inclDocVerify.ivDocVerify.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.itemView.context,
                            R.drawable.ic_doc_verify_reject
                        )
                    )
                }
            }
        } else {
            holder.binding!!.inclDocVerify.ivDocVerify.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_venue_doc
                )
            )
            holder.binding!!.inclDocVerify.tvDocumentStatus.visibility = View.GONE
        }

        holder.binding!!.ivDeleteVenue.setOnClickListener {
            venuesListener.onEventDeleteClick(list[position], position)
        }

        holder.binding!!.inclVenueCopyLink.root.setOnClickListener {
            venuesListener.onEvenCopyClick(list[position], position)
        }

        holder.binding!!.inclShareFriends.root.setOnClickListener {
            venuesListener.onEventShareClick(list[position], position)
        }

        holder.binding!!.ivEditVenue.setOnClickListener {
            venuesListener.onEventEditClick(list[position], position)
        }

        holder.binding!!.inclDocVerify.llDocVerify.setOnClickListener {
            venuesListener.onDocumentVerifyClick(list[position], position)
        }

        holder.binding!!.frVenueCopy.setOnClickListener {
            list[position].id = 0
            venuesListener.onVenueItemCopyClick(list[position], position)
        }

        holder.binding!!.venueCopy.root.setOnClickListener {
            list[position].id = 0
            venuesListener.onVenueItemCopyClick(list[position], position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}