package com.popiin.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.googledirection.constant.Language
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.BookOptionsAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.FragmentEventSummaryBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.BranchIOListener
import com.popiin.model.TicketBook
import com.popiin.req.CreateEventReq
import com.popiin.req.EditEventReq
import com.popiin.req.UpdateLinkReq
import com.popiin.res.CommonRes
import com.popiin.res.CreateEventRes
import com.popiin.res.EditEventRes
import com.popiin.res.EventListRes
import com.popiin.res.VenueListRes
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventSummaryFragment : BaseFragment<FragmentEventSummaryBinding>() {
    var list: ArrayList<TicketBook> = ArrayList()

    lateinit var bookOptionsAdapter: BookOptionsAdapter
    override fun getLayout(): Int {
        return R.layout.fragment_event_summary
    }

    companion object {
        var isEditEvent = false
        fun newInstance(isEdit: Boolean): EventSummaryFragment {
            isEditEvent = isEdit
            return EventSummaryFragment()
        }
    }

    override fun initViews() {

            if (common.createEventModel.is_draft == 1 ||common.createEventModel.id == 0) {
                binding.btnSave.visibility = View.VISIBLE
                binding.btnSubmitEvent.text = getString(R.string.txt_submit_event)
            } else if (common.createEventModel.is_draft == 0) {
                binding.btnSave.visibility = View.GONE
                binding.btnSubmitEvent.text = getString(R.string.txt_update_event)
            }



        binding.inclAddressDetails.root.setOnClickListener {
            setUiExpandCollapse(
                binding.inclEventSummAddress.root,
                binding.inclAddressDetails.chkDropDown
            )
        }

        binding.inclAddressDetails.chkDropDown.setOnCheckedChangeListener { buttonView, isChecked ->
            setUIWithCheckClick(isChecked, binding.inclEventSummAddress.root)
        }

        binding.inclEventTime.chkDropDown.setOnCheckedChangeListener { buttonView, isChecked ->
            setUIWithCheckClick(isChecked, binding.inclEventSummTime.root)
        }

        binding.inclMusic.chkDropDown.setOnCheckedChangeListener { buttonView, isChecked ->
            setUIWithCheckClick(isChecked, binding.inclEventSummMusic.root)
        }

        binding.inclEntertainment.chkDropDown.setOnCheckedChangeListener { buttonView, isChecked ->
            setUIWithCheckClick(isChecked, binding.inclEventSummEntertainment.root)
        }

        binding.inclBookOptions.chkDropDown.setOnCheckedChangeListener { buttonView, isChecked ->
            setUIWithCheckClick(isChecked, binding.inclEventSummBookOptions.root)
        }

        binding.inclOther.chkDropDown.setOnCheckedChangeListener { buttonView, isChecked ->
            setUIWithCheckClick(isChecked, binding.inclEventSummOther.root)
        }

        binding.inclEventTime.root.setOnClickListener {
            setUiExpandCollapse(binding.inclEventSummTime.root,binding.inclEventTime.chkDropDown)
        }

        binding.inclMusic.root.setOnClickListener {
            setUiExpandCollapse(binding.inclEventSummMusic.root,binding.inclMusic.chkDropDown)
        }

        binding.inclEntertainment.root.setOnClickListener {
            setUiExpandCollapse(binding.inclEventSummEntertainment.root,binding.inclEntertainment.chkDropDown)
        }

        binding.tvEventName.text = common.createEventModel.eventName
        binding.txtEventDescription.text = common.createEventModel.description


        binding.inclBookOptions.root.setOnClickListener {
            setUiExpandCollapse(binding.inclEventSummBookOptions.root,binding.inclBookOptions.chkDropDown)
        }

        binding.inclOther.root.setOnClickListener {
            setUiExpandCollapse(binding.inclEventSummOther.root,binding.inclOther.chkDropDown)
        }

        binding.llEditEvent.setOnClickListener {
            common.isSetEventData=true
            setFragmentWithStack(CreateEventFragment.newInstance(true),CreateEventFragment::class.java.simpleName)
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if (isEditEvent && common.createEventModel.id > 0) {
                callEditEventApi(1)
            }else{
                callCreateEventApi(1)
            }
        }

        binding.btnSubmitEvent.setOnClickListener {
            if ( common.createEventModel.is_draft == 0 && common.createEventModel.id > 0) {
                // call update event api
                callUpdateEventApi(0)
            } else if (common.createEventModel.is_draft == 1 && common.createEventModel.id > 0) {
                // call edit event api
                callEditEventApi(0)
            }else{
                // call create event api
                callCreateEventApi(0)
            }
        }

        setAddressData()
        setEventTimeData()
        setBookingData()
        setOtherData()
        setMusicAndEntertainmentData()
    }

    private fun setUIWithCheckClick(checked: Boolean, root: View) {
        if (checked) {
            root.visibility = View.VISIBLE
        } else {
            root.visibility = View.GONE
        }
    }

    private fun callUpdateEventApi(isDraft: Int) {
        val images = arrayOfNulls<String>(common.createEventModel.imagePath!!.size)
        if (isInternetConnect()) {
            showProgress()
            for (i in 0 until common.createEventModel.imagePath!!.size) {
                images[i] = common.createEventModel.imagePath!![i]
            }
            var ticketBooks: ArrayList<TicketBook> = ArrayList()

            common.createEventModel.ticketBooks.forEach { ticketBook ->
                if(ticketBook.isNew){
                    ticketBooks.add(ticketBook)
                }
            }


            val call = apiInterface.doUpdateEvent(
                PrefManager.getAccessToken(),
                EditEventReq(
                    "" + common.createEventModel.id,
                    Language.ENGLISH,
                    common.createEventModel.is_public,
                    common.createEventModel.eventName,
                    common.createEventModel.age,
                    common.createEventModel.description,
                    common.createEventModel.venueName,
                    common.createEventModel.address,
                    common.createEventModel.latitude,
                    common.createEventModel.longitude,
                    common.createEventModel.city,
                    common.createEventModel.postCode,
                    common.convertDateInFormat(
                        common.createEventModel.startDateTime,
                        "dd MMM yyyy hh:mma",
                        "yyyy-MM-dd HH:mm:ss"
                    )!!,
                    common.convertDateInFormat(
                        common.createEventModel.endDateTime,
                        "dd MMM yyyy hh:mma",
                        "yyyy-MM-dd HH:mm:ss"
                    )!!,
                    common.createEventModel.music.isNotEmpty(),
                    common.createEventModel.music,
                   ""+ common.createEventModel.eventMusicName,
                    common.createEventModel.category,
                    common.createEventModel.dressCode.isNotEmpty(),
                    common.createEventModel.dressCode,
                    common.createEventModel.entryPolicy.isNotEmpty(),
                    common.createEventModel.entryPolicy,
                    common.createEventModel.lastEntryPolicy,
                    common.createEventModel.otherInfo,
                    common.createEventModel.totalCapacity,
                    if (ticketBooks.size > 0) "1" else "0",
                    "England",
                    isDraft,
                    images,
                    ticketBooks,
                    common.createEventModel.entertainment
                )
            )
            call!!.enqueue(object : Callback<EditEventRes?> {
                override fun onResponse(call: Call<EditEventRes?>, response: Response<EditEventRes?>) {
                    if (isValidResponse(response)){
                        if (response.body()!!.success) {
                            openEventAddDialog(
                                getString(R.string.txt_event_update),
                                getString(R.string.txt_thank_you) +" "+response.body()!!.message
                            )
                            setPostUpdateLinkApi(
                                common.createEventModel.id,
                                isDraft,
                                response.body()!!.message
                            )

                        }else{
                            showToast(response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EditEventRes?>, t: Throwable) {
                    onApiFailure(throwable = t )
                }
            })
        }
    }

    private fun callEditEventApi(isDraft: Int) {
        val images = arrayOfNulls<String>(common.createEventModel.imagePath!!.size)
        if (isInternetConnect()) {
            showProgress()
            for (i in 0 until common.createEventModel.imagePath!!.size) {
                images[i] = common.createEventModel.imagePath!![i]
            }
            var ticketBooks: ArrayList<TicketBook> = ArrayList()

            common.createEventModel.ticketBooks.forEach { ticketBook ->
                if(ticketBook.isNew){
                    ticketBooks.add(ticketBook)
                }
            }

            val call = apiInterface.doEditEvent(
                PrefManager.getAccessToken(),
                EditEventReq(
                    "" + common.createEventModel.id,
                    Language.ENGLISH,
                    common.createEventModel.is_public,
                    common.createEventModel.eventName,
                    common.createEventModel.age,
                    common.createEventModel.description,
                    common.createEventModel.venueName,
                    common.createEventModel.address,
                    common.createEventModel.latitude,
                    common.createEventModel.longitude,
                    common.createEventModel.city,
                    common.createEventModel.postCode,
                    common.convertDateInFormat(
                        common.createEventModel.startDateTime,
                        "dd MMM yyyy hh:mma",
                        "yyyy-MM-dd HH:mm:ss"
                    )!!,
                    common.convertDateInFormat(
                        common.createEventModel.endDateTime,
                        "dd MMM yyyy hh:mma",
                        "yyyy-MM-dd HH:mm:ss"
                    )!!,
                    common.createEventModel.music.isNotEmpty(),
                    common.createEventModel.music,
                    ""+common.createEventModel.eventMusicName,
                    common.createEventModel.category,
                    common.createEventModel.dressCode.isNotEmpty(),
                    common.createEventModel.dressCode,
                    common.createEventModel.entryPolicy.isNotEmpty(),
                    common.createEventModel.entryPolicy,
                    common.createEventModel.lastEntryPolicy,
                    common.createEventModel.otherInfo,
                    common.createEventModel.totalCapacity,
                    if (ticketBooks.size > 0) "1" else "0",
                    "England",
                    isDraft,
                    images,
                    ticketBooks,
                    common.createEventModel.entertainment
                )
            )
            call!!.enqueue(object : Callback<EditEventRes?> {
                override fun onResponse(call: Call<EditEventRes?>, response: Response<EditEventRes?>) {
                    if (isValidResponse(response)){
                        if (response.body()!!.success){

                            if (isDraft == 1){
                                openEventAddDialog(
                                    getString(R.string.txt_event_saved),getString(R.string.txt_event_saved_after_3_days))
                            }else{
                                openEventAddDialog(
                                    getString(R.string.txt_event_update),
                                    getString(R.string.txt_thank_you) +" "+ response.body()!!.message
                                )
                            }

                            setPostUpdateLinkApi(common.createEventModel.id,isDraft, response.body()!!.message)

                        }else{
                            showToast(response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EditEventRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun setPostUpdateLinkApi(id: Int, isDraft: Int, msg: String) {
        val properties: LinkProperties = LinkProperties().addControlParameter(
            Parameters().eventId,
            "" + id
        )

        shareBranchIOLink(properties, object : BranchIOListener() {
            override fun onLinkCreate(url: String?) {
                super.onLinkCreate(url)
                callPostUpdateLinkApi(id, url!!)
            }

            override fun onLinkCreateError(error: BranchError?) {
                super.onLinkCreateError(error)
                val commonDialog = CommonDialog(
                    requireActivity(),
                    getString(R.string.lbl_ok), "", "", msg
                )
                commonDialog.show()
                commonDialog.binding.btnPositive.setOnClickListener {
                    commonDialog.dismiss()
                }
            }
        })
    }

    private fun setMusicAndEntertainmentData() {
        if (common.createEventModel.music.isNotEmpty()) {
            binding.inclMusic.root.visibility = View.VISIBLE
            binding.viewMusic.visibility = View.VISIBLE
            binding.inclEventSummMusic.inclMusic.title =
                common.createEventModel.music.replace("Other" + CONSTANT.SEPRATEOR_OTHER, "")
                    .replace(CONSTANT.SEPRATEOR, ", ").replace(CONSTANT.SEPRATEOR_OTHER, ", ")
            if (common.createEventModel.eventMusicName.isNotEmpty()) {
                binding.inclEventSummMusic.inclEventDjLineUp.root.visibility=View.VISIBLE
                binding.inclEventSummMusic.inclEventDjLineUp.title = common.createEventModel.eventMusicName
            } else {
                binding.inclEventSummMusic.inclEventDjLineUp.root.visibility=View.GONE
                binding.inclEventSummMusic.inclEventDjLineUp.title = "--"
            }
        }else{
            binding.inclMusic.root.visibility = View.GONE
            binding.inclEventSummMusic.root.visibility = View.GONE
            binding.viewMusic.visibility = View.GONE
        }


        if (common.createEventModel.entertainment.isNotEmpty()) {
            binding.inclEntertainment.root.visibility = View.VISIBLE
            binding.viewEntertainment.visibility = View.VISIBLE
            binding.inclEventSummEntertainment.inclMusic.title =
                common.createEventModel.entertainment.replace(
                    "Other" + CONSTANT.SEPRATEOR_OTHER,
                    ""
                )
                    .replace(CONSTANT.SEPRATEOR, ", ").replace(CONSTANT.SEPRATEOR_OTHER, ", ")
            binding.inclEventSummEntertainment.inclBazza.title = "--"
        }else{
            binding.inclEntertainment.root.visibility = View.GONE
            binding.inclEventSummEntertainment.root.visibility = View.GONE
            binding.viewEntertainment.visibility = View.GONE
        }

    }

    private fun callCreateEventApi(isDraft: Int) {
        val images = arrayOfNulls<String>(common.createEventModel.imagePath!!.size)
        if (isInternetConnect()) {
            showProgress()
            for (i in 0 until common.createEventModel.imagePath!!.size) {
                images[i] = common.createEventModel.imagePath!![i]
            }

            val call: Call<CreateEventRes?>? = apiInterface.doCreateEvent(
                PrefManager.getAccessToken(),
                CreateEventReq(
                    Language.ENGLISH,
                    common.createEventModel.is_public,
                    common.createEventModel.eventName,
                    common.createEventModel.age,
                    common.createEventModel.description,
                    common.createEventModel.venueName,
                    common.createEventModel.address,
                    common.createEventModel.latitude,
                    common.createEventModel.longitude,
                    common.createEventModel.city,
                    common.createEventModel.postCode,
                    common.convertDateInFormat(
                        common.createEventModel.startDateTime,
                        "dd MMM yyyy hh:mma",
                        "yyyy-MM-dd HH:mm:ss"
                    )!!,
                    common.convertDateInFormat(
                        common.createEventModel.endDateTime,
                        "dd MMM yyyy hh:mma",
                        "yyyy-MM-dd HH:mm:ss"
                    )!!,
                    common.createEventModel.music.isNotEmpty(),
                    common.createEventModel.music,
                    ""+common.createEventModel.eventMusicName,
                    common.createEventModel.category,
                    common.createEventModel.dressCode.isNotEmpty(),
                    common.createEventModel.dressCode,
                    common.createEventModel.entryPolicy.isNotEmpty(),
                    common.createEventModel.entryPolicy,
                    common.createEventModel.lastEntryPolicy,
                    common.createEventModel.otherInfo,
                    common.createEventModel.totalCapacity,
                    if (common.createEventModel.ticketBooks.size > 0) "1" else "0",
                    "",
                    isDraft,
                    images,
                    common.createEventModel.ticketBooks,
                    common.createEventModel.entertainment
                )
            )
            call!!.enqueue(object : Callback<CreateEventRes?> {
                override fun onResponse(call: Call<CreateEventRes?>?, response: Response<CreateEventRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            PrefManager.setHasEvent(1)
                            if (isDraft == 1){
                                openEventAddDialog(getString(R.string.txt_event_saved),getString(R.string.txt_event_saved_after_3_days))
                            }else{
                                openEventAddDialog(getString(R.string.txt_event_update), ""+response.body()!!.message
                                )
                            }

                            setPostUpdateLinkApi(response.body()!!.eventData!!.id,isDraft,response.body()!!.message!!)
                        } else {
                            common.openDialog(requireActivity(), response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateEventRes?>?, t: Throwable?) {
                    hideProgress()
                    t?.printStackTrace()
                }
            })
        }
    }



    private fun setOtherData() {
        binding.inclEventSummOther.inclAgeRestrict.title = common.createEventModel.age

        // Entry Policy
        if (common.createEventModel.entryPolicy.isEmpty()) {
            binding.inclEventSummOther.inclEntryPolicy.root.visibility = View.GONE
        } else {
            binding.inclEventSummOther.inclEntryPolicy.root.visibility = View.VISIBLE
            binding.inclEventSummOther.inclEntryPolicy.title = common.createEventModel.entryPolicy
        }

        // Last Policy
        if (common.createEventModel.lastEntryPolicy.isEmpty()) {
            binding.inclEventSummOther.inclLastEntryPolicy.root.visibility = View.GONE
        } else {
            binding.inclEventSummOther.inclLastEntryPolicy.root.visibility = View.VISIBLE
            binding.inclEventSummOther.inclLastEntryPolicy.title = common.createEventModel.lastEntryPolicy
        }

        if (common.createEventModel.dressCode.isEmpty()) {
            binding.inclEventSummOther.inclDressCode.root.visibility = View.GONE
        } else {
            binding.inclEventSummOther.inclDressCode.root.visibility = View.VISIBLE
            binding.inclEventSummOther.inclDressCode.title = common.createEventModel.dressCode
        }

        if (common.createEventModel.category.isEmpty()) {
            binding.inclEventSummOther.inclCategory.root.visibility = View.GONE
        } else {
            binding.inclEventSummOther.inclCategory.root.visibility = View.VISIBLE
            binding.inclEventSummOther.inclCategory.title = common.createEventModel.category
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
        }

        if (common.createEventModel.otherInfo.isEmpty()) {
            binding.inclEventSummOther.inclOtherInfo.root.visibility = View.GONE
        } else {
            binding.inclEventSummOther.inclOtherInfo.root.visibility = View.VISIBLE
            binding.inclEventSummOther.inclOtherInfo.title = common.createEventModel.otherInfo
        }
    }

    private fun setBookingData() {
        bookOptionsAdapter = BookOptionsAdapter(common.createEventModel.ticketBooks)
        binding.inclEventSummBookOptions.rvBookOptions.layoutManager = LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false)
        binding.inclEventSummBookOptions.rvBookOptions.adapter = bookOptionsAdapter
    }

    private fun setEventTimeData() {
        binding.inclEventSummTime.inclStartDate.time = common.convertDateInFormat(
            common.createEventModel.startDateTime,
            Constant().ddMmYyyyHhMmA,
            Constant().mmmDdYyyy
        )
        binding.inclEventSummTime.inclEndDate.time = common.convertDateInFormat(
            common.createEventModel.endDateTime,
            Constant().ddMmYyyyHhMmA,
            Constant().mmmDdYyyy
        )
        binding.inclEventSummTime.inclStartTime.time = common.convertDateInFormat(
            common.createEventModel.startDateTime,
            Constant().ddMmYyyyHhMmA,
            Constant().hhMmA
        )
        binding.inclEventSummTime.inclEndTime.time = common.convertDateInFormat(
            common.createEventModel.endDateTime,
            Constant().ddMmYyyyHhMmA,
            Constant().hhMmA
        )
    }

    private fun setAddressData() {
        binding.inclEventSummAddress.inclAddress.title = common.createEventModel.address
        binding.inclEventSummAddress.inclCity.title = common.createEventModel.city
        binding.inclEventSummAddress.inclVenue.title = common.createEventModel.venueName
        binding.inclEventSummAddress.inclPostalCode.title = common.createEventModel.postCode
    }

    private fun setUiExpandCollapse(root: View, checkBox: CheckBox) {
        if (root.isShown){
            root.visibility = View.GONE
            checkBox.isChecked = false
        }else{
            root.visibility = View.VISIBLE
            checkBox.isChecked = true
        }
    }

    private fun openEventAddedDialog(message: String, secondMessageDesc: String) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        binding.tvPassSuccess.text = message
        binding.tvSuccess.text = secondMessageDesc

        binding.ivClose.visibility = View.GONE

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()

            val fragment = mActivity?.supportFragmentManager?.fragments
            val fragments: ArrayList<Fragment> = arrayListOf()
            fragment?.forEach {
                if(it.javaClass.simpleName.equals(CreateEventFragment::class.java.simpleName)) {
                    fragments.add(it)
                }else if(it.javaClass.simpleName.equals(MyEventsFragment::class.java.simpleName)) {
                    fragments.add(it)
                }
            }
            val manager = mActivity?.supportFragmentManager
            val trans = manager?.beginTransaction()
            fragments.forEach{ removeFragment ->
                trans?.remove(removeFragment)
            }
            trans?.commit()
            manager?.popBackStack()

            setFragmentWithStack(
                MyEventsFragment.newInstance(),
                MyEventsFragment::class.java.simpleName
            )
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }


    private fun openEventAddDialog(
        message: String,
        messageDesc: String,
    ) {
        val dialog = PopupWindow(mActivity)
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_reset_password, null, false
        )

        binding.tvPassSuccess.text = message
        binding.tvSuccess.text = messageDesc

        binding.ivClose.visibility = View.GONE

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()

            val fragment = mActivity?.supportFragmentManager?.fragments
            val fragments: ArrayList<Fragment> = arrayListOf()
            fragment?.forEach {
                if(it.javaClass.simpleName.equals(CreateEventFragment::class.java.simpleName)) {
                    fragments.add(it)
                }else if(it.javaClass.simpleName.equals(MyEventsFragment::class.java.simpleName)) {
                    fragments.add(it)
                }
            }
            val manager = mActivity?.supportFragmentManager
            val trans = manager?.beginTransaction()
            fragments.forEach{ removeFragment ->
                trans?.remove(removeFragment)
            }
            trans?.commit()
            manager?.popBackStack()

            setFragmentWithStack(
                MyEventsFragment.newInstance(),
                MyEventsFragment::class.java.simpleName
            )
        }

        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }


}