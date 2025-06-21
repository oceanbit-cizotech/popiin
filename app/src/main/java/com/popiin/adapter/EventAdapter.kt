package com.popiin.adapter


import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterEventsBinding
import com.popiin.listener.AdapterEventListener
import com.popiin.res.EventListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import java.util.*


class EventAdapter(
    private var eventList: ArrayList<EventListRes.Event?>,
    private var listener: AdapterEventListener<EventListRes.Event?>,
) : BaseRVAdapter<AdapterEventsBinding>() {
    private lateinit var eventPagerAdapter: EventPagerAdapter

    private var originalList: ArrayList<EventListRes.Event?> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.adapter_events
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterEventsBinding>,
        @SuppressLint("RecyclerView") position: Int,
    ) {


        if (eventList[position]!!.type!! == "whatson") {
            holder.binding.ivShare.visibility=View.GONE
            holder.binding.cbLike.visibility=View.GONE
            if (eventList[position]!!.whatsonticket!!.size > 0) {
                if (eventList[position]!!.whatsonticket!![0].price == 0.0) {
                    holder.binding.tvPrice.text =
                        holder.itemView.context.getString(R.string.txt_free)
                } else {
                    holder.binding.tvPrice.text =
                        Common.instance!!.currencySymbol + Common.instance!!.getDecimalFormatValue(
                            eventList[position]!!.whatsonticket!![0].price
                        )
                }
            }

            if (eventList[position]!!.venueevent != null) {
                holder.binding.tvEventName.text = eventList[position]!!.title
                holder.binding.tvEventLocation.text =
                    eventList[position]!!.venueevent!!.venue_address + ", " + eventList[position]!!.venueevent!!.venue_city + ", " + eventList[position]!!.venueevent!!.venue_postal_code

            }
            holder.binding.tvDate.text = Common.instance!!.convertDateInFormat(
                eventList[position]!!.what_datetime,
                Constant().yyyyMmDdHhMmSsCamel,
                Constant().eeeDdMmmYyyy
            )
            holder.binding.tvCalenderTime.text = Common.instance!!.convertDateInFormat(
                eventList[position]!!.what_datetime,
                Constant().yyyyMmDdHhMmSsCamel,
                Constant().hhMmA
            )

            if (eventList[position]!!.whatsonimages!!.size <= 1) {
                holder.binding.arIndicator.visibility = View.GONE
            } else {
                holder.binding.arIndicator.visibility = View.VISIBLE
            }

            eventPagerAdapter = EventPagerAdapter(eventList[position]!!, eventList[position]!!.whatsonimages!!, listener)
            holder.binding.rvEventImages.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.binding.rvEventImages.adapter = eventPagerAdapter
            holder.binding.rvEventImages.onFlingListener = null
            holder.binding.arIndicator.attachTo(holder.binding.rvEventImages, true)
            holder.binding.arIndicator.numberOfIndicators = eventList[position]!!.whatsonimages!!.size
            if(eventList[position]!!.whatsonimages!!.size==0){
                holder.binding.ivDefault.visibility=View.VISIBLE
            }else{
                holder.binding.ivDefault.visibility=View.GONE
            }

        } else {
            holder.binding.ivShare.visibility=View.VISIBLE
            holder.binding.cbLike.visibility=View.VISIBLE
            holder.binding.isFavorite=eventList[position]!!.isFavorite
            eventList[position]!!.has_ticket = 0
            eventList[position]!!.tickets!!.sort()
            // eventList[position]!!.lowestPrice = eventList[position]!!.tickets!![0].price
            for (ticket in eventList[position]!!.tickets!!) {
                if (ticket.available_quantity > 0) {
                    eventList[position]!!.has_ticket = 1
                    break
                }
            }

            if (eventList[position]!!.has_ticket == 1) {
                if (eventList[position]!!.tickets!![0].price == 0.0) {
                    holder.binding.tvPrice.text =
                        holder.itemView.context.getString(R.string.txt_free)
                } else {
                    holder.binding.tvPrice.text =
                        Common.instance!!.currencySymbol + Common.instance!!.getDecimalFormatValue(
                            eventList[position]!!.tickets!![0].price
                        )
                }
            } else {
                holder.binding.tvPrice.text = holder.itemView.context.getString(R.string.txt_sold_out)
            }

            holder.binding.tvEventName.text = eventList[position]!!.name
            holder.binding.tvEventLocation.text =
                eventList[position]!!.address + ", " + eventList[position]!!.city + ", " + eventList[position]!!.postal_code
            holder.binding.tvDate.text = Common.instance!!.convertDateInFormat(
                eventList[position]!!.start_date_time,
                Constant().yyyyMmDdHhMmSsCamel,
                Constant().eeeDdMmmYyyy
            )
            holder.binding.tvCalenderTime.text = Common.instance!!.convertDateInFormat(
                eventList[position]!!.start_date_time,
                Constant().yyyyMmDdHhMmSsCamel,
                Constant().hhMmA
            )

            if (eventList[position]!!.images!!.size <= 1) {
                holder.binding.arIndicator.visibility = View.GONE
            } else {
                holder.binding.arIndicator.visibility = View.VISIBLE
            }


            eventPagerAdapter =
                EventPagerAdapter(eventList[position]!!.images!!, eventList[position]!!, listener)
            holder.binding.rvEventImages.layoutManager =
                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.binding.rvEventImages.adapter = eventPagerAdapter
            //  PagerSnapHelper().attachToRecyclerView(holder.binding.rvEventImages)
            holder.binding.rvEventImages.onFlingListener = null

            holder.binding.arIndicator.attachTo(holder.binding.rvEventImages, true)
            holder.binding.arIndicator.numberOfIndicators = eventList[position]!!.images!!.size
            if(eventList[position]!!.images!!.size==0){
                holder.binding.ivDefault.visibility=View.VISIBLE
            }else{
                holder.binding.ivDefault.visibility=View.GONE
            }
        }


        if (eventList[position]!!.category != null) {
            holder.binding.tvCategory.visibility = View.VISIBLE
            val strArray: List<String?>?
            strArray = eventList[position]!!.category!!.split(",")

            val firstOne =
                strArray[0].replace(
                    CONSTANT.SEPRATEOR + Constant().other + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                ).replace(CONSTANT.SEPRATEOR, ", ")
            val firstOneCategory = firstOne.split(", ")
            holder.binding.tvCategory.text = firstOneCategory[0]

        } else {
            holder.binding.tvCategory.visibility = View.GONE
            holder.binding.tvCategory.visibility = View.GONE
        }

        if (eventList[position]!!.entertainment != null && eventList[position]!!.entertainment!!.isNotEmpty()) {
            holder.binding.tvEventEntertainment.visibility = View.VISIBLE
            holder.binding.llEntertainment.visibility = View.VISIBLE
            holder.binding.tvEventEntertainment.text =
                eventList[position]!!.entertainment!!.replace(
                    Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", "
                ).replace(CONSTANT.SEPRATEOR, ", ")
        } else {
            holder.binding.llEntertainment.visibility = View.GONE
        }

        if (eventList[position]!!.music != null && eventList[position]!!.music!!.isNotEmpty()) {
            holder.binding.tvEventMusic.visibility = View.VISIBLE
            holder.binding.llEventMusic.visibility = View.VISIBLE

            holder.binding.tvEventMusic.text = eventList[position]!!.music!!.replace(
                Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                ", "
            ).replace(CONSTANT.SEPRATEOR, ", ")
        } else {
            holder.binding.tvEventEntertainment.visibility = View.GONE
            holder.binding.llEventMusic.visibility = View.GONE
        }



        originalList.addAll(eventList)

        holder.binding.model = eventList[position]
        holder.binding.common = Common.instance

        holder.binding.clMain.setOnClickListener {
            listener.onItemClick(eventList[position], position)
        }

        holder.binding.ivShare.setOnClickListener {
            listener.onShareClick(eventList[position], position)
        }

        holder.binding.cbLike.setOnClickListener {
            listener.onFavouriteClick(
                eventList[position],
                position,
                holder.binding.cbLike.isChecked
            )
        }

        holder.binding.llEntertainment.visibility=View.GONE
        holder.binding.llEventMusic.visibility=View.GONE
        holder.binding.tvEventLocation.visibility=View.GONE
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortListByPrice(sortType: Int) {
        if (sortType == 0) {
            eventList.clear()
            eventList.addAll(originalList)
        } else {
            for (event in eventList) {
                if(event!!.tickets!=null){
                    if(event!!.tickets!!.size>0) {
                        event!!.tickets!!.sort()
                        event.isSelected = false
                        event.lowestPrice = event.tickets!![0].price
                    }else{
                        event.isSelected = false
                        event.lowestPrice =0.0
                    }
                }else{
                    if(event.whatsonticket!!.size>0) {
                        event.whatsonticket!!.sort()
                        event.isSelected = false
                        event.lowestPrice = event.whatsonticket[0].price
                    }else{
                        event.isSelected = false
                        event.lowestPrice = 0.0
                    }
                }

            }

            eventList.sortWith { e1, e2 ->
                if (e1!!.lowestPrice > e2!!.lowestPrice) {
                    sortType
                } else if (e1.lowestPrice < e2.lowestPrice) {
                    sortType * -1
                } else {
                    0
                }
            }
        }
        notifyDataSetChanged()
    }
}