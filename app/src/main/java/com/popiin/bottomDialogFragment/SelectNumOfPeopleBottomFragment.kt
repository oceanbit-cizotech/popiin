package com.popiin.bottomDialogFragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.NumOfPeopleAdapter
import com.popiin.databinding.BottomFragmentSelectNumOfPeopleBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.model.NumOfPeopleModel

class SelectNumOfPeopleBottomFragment(
    private var numberPeoples: ArrayList<NumOfPeopleModel>,
    private var selectedItem: Int,
    private var numOfPeopleListener: AdapterItemClickListener<NumOfPeopleModel>,
) :
    BaseBottomSheetDialog<BottomFragmentSelectNumOfPeopleBinding>() {
    private lateinit var numOfPeopleAdapter: NumOfPeopleAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_num_of_people
    }

    override fun initViews() {

        numOfPeopleAdapter = NumOfPeopleAdapter(numberPeoples, selectedItem, numOfPeopleListener, dialog)
        binding?.rvNumOfPeople?.layoutManager = LinearLayoutManager(mActivity)
        binding?.rvNumOfPeople?.adapter = numOfPeopleAdapter
        binding?.ivClose?.setOnClickListener {
            dismiss()
        }
    }
}