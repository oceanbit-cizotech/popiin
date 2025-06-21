package com.popiin.adapter

import android.view.View
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterFavouritesBinding
import com.popiin.listener.AdapterEventListner
import com.popiin.res.FavouriteEventsRes
import com.popiin.util.Common
import com.popiin.util.Constant

class FavouritesAdapter(
    private var favList: ArrayList<FavouriteEventsRes.FavouriteEvent>,
    private var adapterEventListener: AdapterEventListner<FavouriteEventsRes.FavouriteEvent?>,
) : BaseRVAdapter<AdapterFavouritesBinding>() {
    private lateinit var eventPagerAdapter: FavouriteEventPagerAdapter
    private var common = Common.instance
    override fun getLayout(): Int {
        return R.layout.adapter_favourites
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterFavouritesBinding>, position: Int) {
        holder.binding.model = favList[position]
        holder.binding.common = common


        if (favList[position]!!.event!!.has_ticket == 1) {
            val sortedTickets = favList[position]!!.tickets!!.sortedBy { it.price }
            if (sortedTickets[0].price == 0.0) {
                holder.binding.tvPrice.text =
                    holder.itemView.context.getString(R.string.txt_free)
            } else {
                holder.binding.tvPrice.text =
                    Common.instance!!.currencySymbol + Common.instance!!.getDecimalFormatValue(
                        favList[position]!!.tickets!![0].price
                    )
            }
        } else {
            holder.binding.tvPrice.text = holder.itemView.context.getString(R.string.txt_sold_out)
            holder.binding.tvPrice.visibility=View.GONE
        }

        if (favList[position].event!!.category != null) {
            holder.binding.tvCategory.visibility = View.VISIBLE
            val strArray: List<String?>?
            strArray = favList[position].event!!.category!!.split(",")

            val firstOne =
                strArray[0].replace(CONSTANT.SEPRATEOR + Constant().other + CONSTANT.SEPRATEOR_OTHER,
                    ", ").replace(CONSTANT.SEPRATEOR, ", ")
            val firstOneCategory = firstOne.split(", ")
            holder.binding.tvCategory.text = firstOneCategory[0]
        } else {
            holder.binding.tvCategory.visibility = View.GONE
        }

        if (favList[position]!!.event!!.entertainment != null && favList[position]!!.event!!.entertainment!!.isNotEmpty()) {
            holder.binding.tvEventEntertainment.visibility = View.VISIBLE
            holder.binding.tvEventEntertainment.text =
                favList[position]!!.event!!.entertainment!!.replace(
                    Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                ).replace(CONSTANT.SEPRATEOR, ", ")
        } else {
            holder.binding.tvEventEntertainment.visibility = View.GONE
        }

        if (favList[position].event!!.music != null && favList[position].event!!.music!!.isNotEmpty()) {
            holder.binding.tvEventMusic.visibility = View.VISIBLE
            holder.binding.tvEventMusic.text = favList[position].event!!.music!!.replace(
                Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                ", "
            ).replace(CONSTANT.SEPRATEOR, ", ")
        } else {
            holder.binding.tvEventEntertainment.visibility = View.GONE
        }

        holder.binding.cbLike.isChecked = true
        holder.binding.cbLike.setOnClickListener {
            adapterEventListener.onFavouriteClick(
                favList[position],
                position,
                holder.binding.cbLike.isChecked
            )
        }

        holder.binding.root.setOnClickListener {
            adapterEventListener.onItemClick(favList[position], position)
        }

        holder.binding.ivShare.setOnClickListener {
            adapterEventListener.onShareClick(favList[position], position)
        }

        eventPagerAdapter =
            FavouriteEventPagerAdapter(
                holder.itemView.context,
                favList[position].images,
                favList[position],
                adapterEventListener
            )
        holder.binding.viewPager.adapter = eventPagerAdapter
        holder.binding.pageIndicatorView.setViewPager(holder.binding.viewPager)

        holder.binding.tvCategory.visibility = View.GONE
        holder.binding.tvEventEntertainment.visibility = View.GONE
        holder.binding.tvEventMusic.visibility=View.GONE
    //    holder.binding.tvDate.visibility=View.GONE
    }

    override fun getItemCount(): Int {
        return favList.size
    }
}