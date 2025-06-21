package com.popiin.bottomDialogFragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.WhatsonBookTypeAdapter
import com.popiin.adapter.WhatsonEventBookTypeAdapter
import com.popiin.databinding.BottomFragmentSelectWhatsonBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.EventListRes
import com.popiin.res.VenueListRes

class SelectWhatsonEventBottomFragment(
    var tickets: ArrayList<EventListRes.Whatsonticket>,
    private var selectedItem: Int,
    private var bookTypeListener: AdapterItemClickListener<EventListRes.Whatsonticket>,
) : BaseBottomSheetDialog<BottomFragmentSelectWhatsonBinding>() {
    private lateinit var bookTypeAdapter: WhatsonEventBookTypeAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_whatson
    }

    override fun initViews() {
        bookTypeAdapter =
            WhatsonEventBookTypeAdapter(tickets, selectedItem, bookTypeListener, dialog)
        binding?.rvBookType?.layoutManager = LinearLayoutManager(mActivity)
        binding?.rvBookType?.adapter = bookTypeAdapter
        binding?.ivClose?.setOnClickListener {
            dismiss()
        }
    }

}