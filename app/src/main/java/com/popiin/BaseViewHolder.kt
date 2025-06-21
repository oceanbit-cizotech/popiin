package com.popiin

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<DB : ViewDataBinding?> protected constructor(var binding: DB) :
    RecyclerView.ViewHolder(binding!!.root)