package com.popiin.bottomDialogFragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.WhatsonBookTypeAdapter
import com.popiin.databinding.BottomFragmentSelectWhatsonBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes

class SelectWhatsonBottomFragment(
    var tickets: ArrayList<VenueListRes.WhatsonTicket>,
    private var selectedItem: Int,
    private var bookTypeListener: AdapterItemClickListener<VenueListRes.WhatsonTicket>,) : BaseBottomSheetDialog<BottomFragmentSelectWhatsonBinding>()  {
    private lateinit var bookTypeAdapter: WhatsonBookTypeAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_whatson
    }

    override fun initViews() {
        bookTypeAdapter = WhatsonBookTypeAdapter(tickets, selectedItem, bookTypeListener, dialog)
        binding?.rvBookType?.layoutManager = LinearLayoutManager(mActivity)
        binding?.rvBookType?.adapter = bookTypeAdapter
        binding?.ivClose?.setOnClickListener {
            dismiss()
        }
    }
}