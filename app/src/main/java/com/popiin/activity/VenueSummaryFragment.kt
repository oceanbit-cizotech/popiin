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
import com.popiin.activity.RegisterVenueFragment.Companion
import com.popiin.adapter.VenueSumImageAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.FragmentVenueSummaryBinding
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.InclImageWithTitleDescBinding
import com.popiin.dialog.CommonDialog
import com.popiin.listener.BranchIOListener
import com.popiin.model.TimingModel
import com.popiin.req.CreateVenuesReq
import com.popiin.req.EditVenueReq
import com.popiin.req.UpdateLinkReq
import com.popiin.res.CommonRes
import com.popiin.res.CreateVenueRes
import com.popiin.res.EditVenueRes
import com.popiin.res.VenueListRes
import com.popiin.util.Common
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VenueSummaryFragment : BaseFragment<FragmentVenueSummaryBinding>() {
    var images: ArrayList<String> = ArrayList()
    private lateinit var imageSummAdapter: VenueSumImageAdapter
    private lateinit var menuimageSummAdapter: VenueSumImageAdapter
    override fun getLayout(): Int {
        return R.layout.fragment_venue_summary
    }

    companion object {
        var isEditVenue = false
        var venueModel: VenueListRes.Venue? = null
        fun newInstance(venue: VenueListRes.Venue?, isEdit: Boolean): VenueSummaryFragment {
            venueModel = venue
            isEditVenue = isEdit
            return VenueSummaryFragment()
        }
    }

    override fun initViews() {

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        if (venueModel != null && venueModel!!.id > 0) {
            if (venueModel!!.venue_is_draft == 0) {
                binding.btnSave.visibility = View.GONE
                binding.tvSubmitSave.text = getString(R.string.txt_update_camel)
            }else{
                binding.btnSave.visibility = View.VISIBLE
                binding.tvSubmitSave.text = getString(R.string.txt_submit)
            }
        }

        binding.inclBasicInfo.root.setOnClickListener {
            setUiExpandCollapse(
                binding.inclVenueSummBasicInfo.root,
                binding.inclBasicInfo.chkDropDown
            )
        }

        binding.inclAddressDetails.root.setOnClickListener {
            setUiExpandCollapse(
                binding.inclVenueAddressDetails.root,
                binding.inclAddressDetails.chkDropDown
            )
        }

        binding.inclOpenCloseHour.root.setOnClickListener {
            setUiExpandCollapse(
                binding.inclVenueOpenClose.root,
                binding.inclOpenCloseHour.chkDropDown
            )
        }

        binding.inclOtherDetails.root.setOnClickListener {
            setUiExpandCollapse(
                binding.inclVenueOtherDetails.root,
                binding.inclOtherDetails.chkDropDown
            )
        }

        binding.llEdit.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if (isEditVenue && (venueModel!!.id > 0)) {
                callEditVenueApi(1)
            }else{
                callCreateVenueApi(1)
            }
        }
        
        binding.tvSubmitSave.setOnClickListener {
            if (venueModel != null && venueModel!!.venue_is_draft == 0 && (venueModel!!.id > 0)) {
                callUpdateVenueApi(0)
            } else if (venueModel != null && venueModel!!.venue_is_draft == 1 && (venueModel!!.id > 0)) {
                callEditVenueApi(0)
            }else{
                callCreateVenueApi(0)
            }
        }

        binding.tvVenueName.text = common.registerVenueModel.venueName
        binding.txtVenueDescription.text = common.registerVenueModel.description

        setBasicInfoData()
        setAddressData()
        setOpenCloseData()
        setOtherData()
    }

    private fun callUpdateVenueApi(isDraft: Int) {
        val images = arrayOfNulls<String>(common.registerVenueModel.venueImages!!.size)
        val menuimages = arrayOfNulls<String>(common.registerVenueModel.venueMenuImages!!.size)
        val tempTiming: ArrayList<TimingModel> = ArrayList()

        if (isInternetConnect()) {
            showProgress()
            for (i in 0 until common.registerVenueModel.venueImages!!.size) {
                images[i] = common.registerVenueModel.venueImages!![i]
            }
            for (i in 0 until common.registerVenueModel.venueMenuImages!!.size) {
                menuimages[i] = common.registerVenueModel.venueMenuImages!![i]
            }

            tempTiming.addAll(common.convert24HrsTimes())

            val call = apiInterface.doUpdateVenue(
                PrefManager.getAccessToken(),EditVenueReq(
                    "" + venueModel!!.id,
                    Language.ENGLISH,
                    1,
                    common.registerVenueModel.venueName,
                    common.registerVenueModel.age,
                    common.registerVenueModel.description,
                    common.registerVenueModel.address,
                    common.registerVenueModel.latitude,
                    common.registerVenueModel.longitude,
                    common.registerVenueModel.city,
                    common.registerVenueModel.postal_code,
                    common.registerVenueModel.music.isNotEmpty(),
                    common.registerVenueModel.music,
                    common.registerVenueModel.entertainment,
                    common.registerVenueModel.venueMusicDjLine,
                    common.registerVenueModel.dress_code.isNotEmpty(),
                    common.registerVenueModel.dress_code,
                    common.registerVenueModel.entry_policy.isNotEmpty(),
                    common.registerVenueModel.entry_policy,
                    common.registerVenueModel.other_info,
                    "" + 0,
                    "Englend",
                    isDraft,
                    images,
                    tempTiming,
                    menuimages,
                    if (common.registerVenueModel.outDoorEnabled) 1 else 0,
                    common.registerVenueModel.last_entry,
                    "",
                    common.registerVenueModel.venueTypes,
                    common.registerVenueModel.venueTypeCategory,
                    common.registerVenueModel.offers,
                    if (common.registerVenueModel.reservationEnabled) 1 else 0, 0,
                    if (common.registerVenueModel.free_wifi) 1 else 0
                )
            )
            call!!.enqueue(object : Callback<EditVenueRes?> {
                override fun onResponse(
                    call: Call<EditVenueRes?>,
                    response: Response<EditVenueRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success!!) {
                            if (isDraft == 0) {
                                openVenueSuccessDialog(getString(R.string.txt_venue_update),
                                    response.body()!!.message!!)
                            } else {
                                openVenueSuccessDialog(
                                    getString(R.string.txt_venue_update),
                                    response.body()!!.message!!
                                )
                            }
                        }else{
                            showToast(response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EditVenueRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }

    }

    private fun callEditVenueApi(isDraft: Int) {
        val images = arrayOfNulls<String>(common.registerVenueModel.venueImages!!.size)
        val menuimages = arrayOfNulls<String>(common.registerVenueModel.venueMenuImages!!.size)
        val tempTiming: ArrayList<TimingModel> = ArrayList()

        if (isInternetConnect()) {
            showProgress()
            for (i in 0 until common.registerVenueModel.venueImages!!.size) {
                images[i] = common.registerVenueModel.venueImages!![i]
            }
            for (i in 0 until common.registerVenueModel.venueMenuImages!!.size) {
                menuimages[i] = common.registerVenueModel.venueMenuImages!![i]
            }

            tempTiming.addAll(common.convert24HrsTimes())

            val call: Call<EditVenueRes?>? = apiInterface.doEditVenue(
                PrefManager.getAccessToken(),
                EditVenueReq(
                    "" + venueModel!!.id,
                    Language.ENGLISH,
                    1,
                    common.registerVenueModel.venueName,
                    common.registerVenueModel.age,
                    common.registerVenueModel.description,
                    common.registerVenueModel.address,
                    common.registerVenueModel.latitude,
                    common.registerVenueModel.longitude,
                    common.registerVenueModel.city,
                    common.registerVenueModel.postal_code,
                    common.registerVenueModel.music.isNotEmpty(),
                    common.registerVenueModel.music, common.registerVenueModel.entertainment,
                    common.registerVenueModel.venueMusicDjLine,
                    common.registerVenueModel.dress_code.isNotEmpty(),
                    common.registerVenueModel.dress_code,
                    common.registerVenueModel.entry_policy.isNotEmpty(),
                    common.registerVenueModel.entry_policy,
                    common.registerVenueModel.other_info,
                    "" + 0,
                    "Englend",
                    isDraft,
                    images,
                    tempTiming,
                    menuimages,
                    if (common.registerVenueModel.outDoorEnabled) 1 else 0,
                    common.registerVenueModel.last_entry,
                    "",
                    common.registerVenueModel.venueTypes,
                    common.registerVenueModel.venueTypeCategory,
                    common.registerVenueModel.offers,
                    if (common.registerVenueModel.reservationEnabled) 1 else 0, 0,
                    if (common.registerVenueModel.free_wifi) 1 else 0
                )
            )
            call!!.enqueue(object : Callback<EditVenueRes?> {
                override fun onResponse(
                    call: Call<EditVenueRes?>,
                    response: Response<EditVenueRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success!!) {
                            if (isDraft == 1) {
                                openVenueSuccessDialog(getString(R.string.txt_venue_update),

                                    response.body()!!.message!!)
                            } else {
                                openVenueSuccessDialog(
                                    getString(R.string.txt_venue_update),
                                    response.body()!!.message!!)
                            }
                        }else{
                            showToast(response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EditVenueRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun callCreateVenueApi(isDraft: Int) {
        val images = arrayOfNulls<String>(common.registerVenueModel.venueImages!!.size)
        val menuimages = arrayOfNulls<String>(common.registerVenueModel.venueMenuImages!!.size)
        val tempTiming: ArrayList<TimingModel> = ArrayList()

        if (isInternetConnect()) {
            showProgress()
            for (i in 0 until common.registerVenueModel.venueImages!!.size) {
                images[i] = common.registerVenueModel.venueImages!![i]
            }
            for (i in 0 until common.registerVenueModel.venueMenuImages!!.size) {
                menuimages[i] = common.registerVenueModel.venueMenuImages!![i]
            }

            tempTiming.addAll(common.convert24HrsTimes())


            val call: Call<CreateVenueRes?>? = apiInterface.doCreateVenue(
                PrefManager.getAccessToken(),
                CreateVenuesReq(
                    Language.ENGLISH,
                    1,
                    common.registerVenueModel.venueName,
                    common.registerVenueModel.age,
                    common.registerVenueModel.description,
                    common.registerVenueModel.address,
                    common.registerVenueModel.latitude,
                    common.registerVenueModel.longitude,
                    common.registerVenueModel.city,
                    common.registerVenueModel.postal_code,
                    common.registerVenueModel.music.isNotEmpty(),
                    common.registerVenueModel.music,
                    common.registerVenueModel.entertainment,
                    common.registerVenueModel.venueMusicDjLine,
                    common.registerVenueModel.dress_code.isNotEmpty(),
                    common.registerVenueModel.dress_code,
                    common.registerVenueModel.entry_policy.isNotEmpty(),
                    common.registerVenueModel.entry_policy,
                    common.registerVenueModel.other_info,
                    "" + 0,
                    "Englend",
                    isDraft,
                    images,
                    tempTiming,
                    menuimages,
                    if (common.registerVenueModel.outDoorEnabled) 1 else 0,
                    common.registerVenueModel.last_entry,
                    "",
                    common.registerVenueModel.venueTypes,
                    common.registerVenueModel.venueTypeCategory,
                    common.registerVenueModel.offers,
                    if (common.registerVenueModel.reservationEnabled) 1 else 0, 0,
                    if (common.registerVenueModel.free_wifi) 1 else 0
                )
            )
            call!!.enqueue(object : Callback<CreateVenueRes?> {
                override fun onResponse(call: Call<CreateVenueRes?>?, response: Response<CreateVenueRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.isSuccess) {
                            PrefManager.setHasVenue(1)
                            setPostUpdateLinkApi(response.body()!!.venue!!.id,isDraft,response.body()!!.message!!)
                            if (isDraft == 1) {
                                openVenueSuccessDialog(getString(R.string.txt_venue_added),
                                    getString(R.string.txt_venue_create_msg))
                            }else{
                                openVenueSuccessDialog(
                                    getString(R.string.txt_venue_update),
                                    response.body()!!.message!!
                                )
                            }

                        } else {
                            common.openDialog(requireActivity(), response.body()!!.message)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CreateVenueRes?>?, t: Throwable?) {
                    onApiFailure(throwable = t!!)
                }
            })
        }
    }


    private fun setPostUpdateLinkApi(id: Int, isDraft: Int, msg: String) {
        val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().venueId, "" + id)
        shareBranchIOLink(properties, object : BranchIOListener() {
            override fun onLinkCreate(url: String?) {
                super.onLinkCreate(url)
                callPostVenueUpdateLinkApi(id, url!!)
            }

            override fun onLinkCreateError(error: BranchError?) {
                super.onLinkCreateError(error)
                var commonDialog = CommonDialog(
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




    private fun setOtherData() {
        binding.inclVenueOtherDetails.inclDressCode.title = if (common.registerVenueModel.dress_code.isNotEmpty()) common.registerVenueModel.dress_code else "-"
        binding.inclVenueOtherDetails.inclEntryPolicy.title = if (common.registerVenueModel.entry_policy.isNotEmpty()) common.registerVenueModel.entry_policy else "-"
        binding.inclVenueOtherDetails.inclLastEntryPolicy.title = if (common.registerVenueModel.last_entry.isNotEmpty()) common.registerVenueModel.last_entry else "-"

        var venueTypeString = ""
        if (common.registerVenueModel.venueTypes.size > 0){
            for (i in 0 until common.registerVenueModel.venueTypes.size) {
                if (i == common.registerVenueModel.venueTypes.size - 1) {
                    venueTypeString += common.registerVenueModel.venueTypes[i]
                } else {
                    venueTypeString += common.registerVenueModel.venueTypes[i] + ", "
                }
            }
        }

        binding.inclVenueOtherDetails.inclVenueType.title = venueTypeString
        binding.inclVenueOtherDetails.inclMusic.title =
            if (common.registerVenueModel.music.isNotEmpty())
                common.registerVenueModel.music
                    .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", ")
                    .replace(CONSTANT.SEPRATEOR, ", ") else "-"

        binding.inclVenueOtherDetails.inclEntertainment.title =
            if (common.registerVenueModel.entertainment.isNotEmpty())
                common.registerVenueModel.entertainment
                    .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", ")
                    .replace(CONSTANT.SEPRATEOR, ", ") else "-"

        var offersString = "-"
        if (common.registerVenueModel.offers != null) {
            if (common.registerVenueModel.offers!!.size > 0) {
                for (i in common.registerVenueModel.offers!!.indices) {
                    offersString += common.registerVenueModel.offers!![i] + ","
                }
            }
        }



        binding.inclVenueOtherDetails.inclOffers.title = offersString
            .replace("null", "").replace(",", "")

        binding.inclVenueOtherDetails.inclOutdoor.title =
            if (common.registerVenueModel.outDoorEnabled) getString(R.string.txt_yes) else getString(
                R.string.txt_no
            )
        binding.inclVenueOtherDetails.inclReservation.title =
            if (common.registerVenueModel.reservationEnabled) getString(R.string.txt_yes) else getString(
                R.string.txt_no
            )
        binding.inclVenueOtherDetails.inclOtherInfo.title = common.registerVenueModel.other_info


    }

    private fun setOpenCloseData() {
        for (i in 0 until common.registerVenueModel.timingList.size){
            if (common.registerVenueModel.timingList[i].day == getString(R.string.monday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclMon,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)
            }else if (common.registerVenueModel.timingList[i].day == getString(R.string.tuesday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclTues,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)

            }else if (common.registerVenueModel.timingList[i].day == getString(R.string.wednesday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclWed,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)

            }else if (common.registerVenueModel.timingList[i].day == getString(R.string.thursday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclThurs,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)

            }else if (common.registerVenueModel.timingList[i].day == getString(R.string.friday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclFri,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)

            }else if (common.registerVenueModel.timingList[i].day == getString(R.string.saturday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclSat,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)

            }else if (common.registerVenueModel.timingList[i].day == getString(R.string.sunday)){
                setOpenCloseWithTime(binding.inclVenueOpenClose.inclSun,common.registerVenueModel.timingList[i].open_time,common.registerVenueModel.timingList[i].close_time)
            }
        }
    }

    private fun setOpenCloseWithTime(inclMon: InclImageWithTitleDescBinding, openTime: String?, closeTime: String?) {
        inclMon.root.visibility = View.VISIBLE
        inclMon.title = common.convertDateInFormat(openTime,Constant().HHmmss24hrs,Constant().hhMmA)+" - "+ common.convertDateInFormat(closeTime,Constant().HHmmss24hrs,Constant().hhMmA)
    }

    private fun setAddressData() {
        binding.inclVenueAddressDetails.inclAddress.title = common.registerVenueModel.address
        binding.inclVenueAddressDetails.inclCity.title = common.registerVenueModel.city
        binding.inclVenueAddressDetails.inclPostalCode.title = common.registerVenueModel.postal_code
    }

    private fun setBasicInfoData() {
        binding.inclVenueSummBasicInfo.inclAge.title = common.registerVenueModel.age

        imageSummAdapter = VenueSumImageAdapter(common.registerVenueModel.venueImages)
        binding.inclVenueSummBasicInfo.inclImages.rvImage.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.inclVenueSummBasicInfo.inclImages.rvImage.adapter = imageSummAdapter

        menuimageSummAdapter = VenueSumImageAdapter(common.registerVenueModel.venueMenuImages)
        binding.inclVenueSummBasicInfo.inclMenuImages.rvImage.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.inclVenueSummBasicInfo.inclMenuImages.rvImage.adapter = menuimageSummAdapter

    }

    private fun openVenueSuccessDialog(message: String, secondMessageDesc: String) {
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
            var fragments: ArrayList<Fragment> = arrayListOf()
            fragment?.forEach { it->
                if(it.javaClass.simpleName.equals(RegisterVenueFragment::class.java.simpleName)) {
                    fragments.add(it)
                }else if(it.javaClass.simpleName.equals(MyVenueFragment::class.java.simpleName)) {
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
                MyVenueFragment.newInstance(),
                MyVenueFragment::class.java.simpleName
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

    private fun setUiExpandCollapse(root: View, checkBox: CheckBox) {
        if (root.isShown) {
            root.visibility = View.GONE
            checkBox.isChecked = false
        } else {
            root.visibility = View.VISIBLE
            checkBox.isChecked = true
        }
    }



}