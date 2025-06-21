package com.popiin.bottomDialogFragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.BookTypeAdapter
import com.popiin.databinding.BottomFragmentSelectBookTypeBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.res.VenueListRes

class SelectBookTypeBottomFragment(
    var tickets: ArrayList<VenueListRes.Tickets>,
    private var selectedItem: Int,
    private var bookTypeListener: AdapterItemClickListener<VenueListRes.Tickets>,
) : BaseBottomSheetDialog<BottomFragmentSelectBookTypeBinding>() {
    private lateinit var bookTypeAdapter: BookTypeAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_book_type
    }

    override fun initViews() {
        bookTypeAdapter = BookTypeAdapter(tickets, selectedItem, bookTypeListener, dialog)
        binding?.rvBookType?.layoutManager = LinearLayoutManager(mActivity)
        binding?.rvBookType?.adapter = bookTypeAdapter
        binding?.ivClose?.setOnClickListener {
            dismiss()
        }
    }
}