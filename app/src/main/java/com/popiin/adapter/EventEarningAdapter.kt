package com.popiin.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterEventEarningBinding
import com.popiin.listener.AdapterWithdrawListener
import com.popiin.res.Earning
import com.popiin.util.Common
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventEarningAdapter(
    var earning: ArrayList<Earning>?,
    var adapterWithdrawListener: AdapterWithdrawListener<Earning>,
) : BaseRVAdapter<AdapterEventEarningBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_event_earning
    }

    override fun getItemCount(): Int {
        return earning!!.size
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterEventEarningBinding>,
        position: Int
    ) {
        holder.binding.earning = earning?.get(position)
        holder.binding.common = Common.instance

        // 3 day related logic
        val dateTimeStr =  earning?.get(position)?.endDateTime
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val parsedDateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val newDateTime = parsedDateTime.plusDays(3)

        val currentDateTime = LocalDateTime.now()

       if (newDateTime.isBefore(currentDateTime)){
           val paid =earning?.get(position)?.paidAmount
           val unPaid =earning?.get(position)?.unpaidAmount
           if (paid?.toFloat() == 0f && unPaid?.toFloat()!! > 0f){
               if(unPaid.toDouble() > paid.toDouble()){
                   holder.binding.tvWithdraw.visibility=View.VISIBLE
               }
               val withdrawRequest=earning?.get(position)?.withdrwalRequest
               if(withdrawRequest.equals("requested")){
                   holder.binding.tvWithdraw.alpha=0.5f
                   holder.binding.tvWithdraw.isEnabled=false
                   holder.binding.tvWithdraw.text="Withdrawal requested"
               }else if(withdrawRequest.equals("processed")){
                   val color = ContextCompat.getColor(holder.itemView.context, R.color.colorSplashGreen)
                   holder.binding.tvWithdraw.setTextColor(color)
                   holder.binding.tvWithdraw.setTypeface(null, Typeface.BOLD)
                   holder.binding.tvWithdraw.alpha=1f
                   holder.binding.tvWithdraw.isEnabled=false
                   holder.binding.tvWithdraw.text="Paid"
                   holder.binding.tvWithdraw.setStroke(color)
               }
           }else {
               val withdrawRequest=earning?.get(position)?.withdrwalRequest
               if(withdrawRequest.equals("processed")){
                   holder.binding.tvWithdraw.visibility=View.VISIBLE
                   val color = ContextCompat.getColor(holder.itemView.context, R.color.colorSplashGreen)
                   holder.binding.tvWithdraw.setTextColor(color)
                   holder.binding.tvWithdraw.setTypeface(null, Typeface.BOLD)
                   holder.binding.tvWithdraw.alpha=1f
                   holder.binding.tvWithdraw.isEnabled=false
                   holder.binding.tvWithdraw.text="Paid"
                   holder.binding.tvWithdraw.setStroke(color)
               }
           }
       }

        holder.binding.tvWithdraw.setOnClickListener {
            adapterWithdrawListener.onWithdrawEventClick(earning!![position], position)
        }
    }
}