package com.popiin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRVAdapter<DB : ViewDataBinding?> :
    RecyclerView.Adapter<BaseViewHolder<DB>>() {
    abstract fun getLayout(): Int
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DB> {
        val binding = DataBindingUtil.inflate<DB>(
            LayoutInflater.from(parent.context),
            getLayout(),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

     inner class MyViewHolder  constructor(_binding: DB) :
        BaseViewHolder<DB>(_binding)
}
