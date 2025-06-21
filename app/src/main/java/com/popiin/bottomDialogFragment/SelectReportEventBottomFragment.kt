package com.popiin.bottomDialogFragment

import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.adapter.ReportListAdapter
import com.popiin.databinding.BottomSelectEventVenueFragmentBinding
import com.popiin.listener.AdapterReportListItemListner
import com.popiin.model.ReportListModel
import com.popiin.util.Constant

class SelectReportEventBottomFragment(
    val list: ArrayList<ReportListModel>,
    var listener: AdapterReportListItemListner<ReportListModel>,
    var selectedVenue: Int,
    var name: String,
) : BaseBottomSheetDialog<BottomSelectEventVenueFragmentBinding>() {
    lateinit var adapter: ReportListAdapter
    override fun getLayout(): Int {
        return R.layout.bottom_select_event_venue_fragment
    }

    override fun initViews() {

        binding!!.tvTitle.text = name

        adapter = ReportListAdapter(list, reportListener, selectedVenue)
        binding?.rvVenueList?.layoutManager = LinearLayoutManager(mActivity)
        binding?.rvVenueList?.adapter = adapter
        binding?.ivClose?.setOnClickListener {
            dismiss()
        }
        binding!!.cbItem.isChecked=list.all { it.isSelected }
        binding?.btnSelectVenue?.setOnClickListener {
            listener.onSelectEvent(list)
           /* if (binding!!.cbItem.isChecked){
                listener.onAllItemSelect(true)
            }else{
                listener.onItemSelect(list[selectedVenue],selectedVenue,list[selectedVenue].isSelected)
            }*/
            dismiss()
        }
        binding!!.cbItem.setOnClickListener{ buttonView ->
            if (binding!!.cbItem.isChecked) {
                for (reportListModel in list) {
                    reportListModel.isSelected = true
                }
                binding!!.tvSelectStatus.text=Constant().selectAll
            } else {
                for (reportListModel in list) {
                    reportListModel.isSelected = false
                }
                binding!!.tvSelectStatus.text=Constant().deselectAll
            }
            adapter.notifyDataSetChanged()
        }
    }

    private var reportListener: AdapterReportListItemListner<ReportListModel?> =
        object : AdapterReportListItemListner<ReportListModel?>() {

            override fun onItemSelect(item: ReportListModel?, position: Int, isSelect: Boolean) {
                super.onItemSelect(item, position, isSelect)
                list[selectedVenue]
                selectedVenue = position
                list[position].isSelected = isSelect
                areAllSelected(list)
            }

            override fun onAllItemSelect(isSelect: Boolean) {
                super.onAllItemSelect(isSelect)
            for (reportListModel in list) {
                reportListModel.isSelected = isSelect
            }
        }

    }

    fun areAllSelected(lists: ArrayList<ReportListModel>){
        if(lists.all {it.isSelected}){
            binding!!.tvSelectStatus.text=Constant().selectAll
            binding!!.cbItem.isChecked=true
        }else {
            binding!!.tvSelectStatus.text=Constant().deselectAll
            binding!!.cbItem.isChecked=false
        }

    }

}