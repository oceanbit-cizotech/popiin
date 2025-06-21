package com.popiin.adapter

import android.graphics.PorterDuff
import android.util.LayoutDirection
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterAmensBinding
import com.popiin.model.AmensModel
import com.popiin.model.AmensSubModel
import com.popiin.flowlayout.FlowLayout
import com.popiin.flowlayout.TagAdapter


class AmenAdapter(var amensList : ArrayList<AmensModel>) : BaseRVAdapter<AdapterAmensBinding>() {
    private lateinit var amenSubAdapter: AmenSubAdapter
    override fun getLayout(): Int {
        return R.layout.adapter_amens
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterAmensBinding>, position: Int) {
        holder.binding.model = amensList[position]

        amenSubAdapter = AmenSubAdapter(amensList[position].featureList)

        if (position == amensList.size - 1){
            holder.binding.tagFlowView.visibility = View.GONE
        }else{
            holder.binding.tagFlowView.visibility = View.VISIBLE
        }

        holder.binding.flowlayout.adapter = object :
            com.popiin.flowlayout.TagAdapter<AmensSubModel?>(amensList[position].featureList as List<AmensSubModel?>?) {

            override fun onSelected(position: Int, view: View) {
                super.onSelected(position, view)

            }

            override fun unSelected(position: Int, view: View) {
                super.unSelected(position, view)

            }

            override fun getView(parent: com.popiin.flowlayout.FlowLayout?, position: Int, t: AmensSubModel?): View {
                val view = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.adapter_amen_sub, parent, false)
                val tv = view.findViewById<TextView>(R.id.tv_tag)
                val iv = view.findViewById<ImageView>(R.id.img_tag)
                iv.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                    R.color.colorBlue), PorterDuff.Mode.MULTIPLY)
                tv.text = t!!.feature

                if (t.image != null) {
                    iv.visibility = View.VISIBLE
                    iv.background = t.image
                } else {
                    iv.visibility = View.GONE
                }

                return view
            }

        }.also { amenSubAdapter }

    }

    override fun getItemCount(): Int {
       return amensList.size
    }
}