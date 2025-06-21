package com.popiin.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.EventPagerAdapter
import com.popiin.adapter.WhatsOnPreviewImageAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.DialogWhatsOnPreviewBinding
import com.popiin.databinding.FragmentWhatsOnPreviewBinding
import com.popiin.listener.InfoDialogListener
import com.popiin.model.WhatsReserveTicketModel
import com.popiin.req.CreateWhatsOnReq
import com.popiin.req.UpdateWhatsOnReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueOfferRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WhatsOnPreviewFragment : BaseFragment<FragmentWhatsOnPreviewBinding>() {
    private var venueOffer: VenueOfferRes? = null
    private var isFirstPopup = false
    override fun getLayout(): Int {
        return R.layout.fragment_whats_on_preview
    }

    companion object {
        var whatOnId: String = ""
        fun newInstance(whatsOnId: String = ""): WhatsOnPreviewFragment {
            whatOnId = whatsOnId
            return WhatsOnPreviewFragment()
        }
    }

    override fun initViews() {
        venueOffer = common.createWhatsOnModel.venuesOfferRes


        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.tvViewMore.setOnClickListener {
            openWhatsONViewMoreDialog()
        }

        binding.llEdit.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }


        binding.tvSubmitSave.setOnClickListener {
            isFirstPopup = true
            showCommonInfoBottomFragment(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_sure_delete),
                getString(R.string.txt_do_you_want_to),
                getString(R.string.txt_whats_on_acticvate),
                getString(R.string.txt_no),
                getString(R.string.txt_yes_activate),
                infoDialogListener
            )
        }

        setPreviewData()
    }

    private fun callCreateWhatsOnApi() {
        val images = arrayOfNulls<String>(common.createWhatsOnModel.whatsOnImages!!.size)
        if (isInternetConnect()) {
            for (i in 0 until common.createWhatsOnModel.whatsOnImages!!.size) {
                images[i] = common.createWhatsOnModel.whatsOnImages!![i]
            }
            showProgress()
            val tickets: ArrayList<WhatsReserveTicketModel> = ArrayList()

            tickets.clear()

            if (common.createWhatsOnModel.addTickets.size > 0) {
                common.createWhatsOnModel.addTickets.forEach { ticket ->
                 ticket.venue_id=venueOffer!!.data!!.id
                }
                tickets.addAll(common.createWhatsOnModel.addTickets)
            }
            if (common.createWhatsOnModel.updateTickets.size > 0) {
                tickets.addAll(common.createWhatsOnModel.updateTickets)
            }

            val createWhatsOnReq = CreateWhatsOnReq(
                "",
                "" + venueOffer!!.data!!.id,
                "" + common.createWhatsOnModel.title,
                "" + common.createWhatsOnModel.description,
                "" + common.convertDateInFormat(
                    common.createWhatsOnModel.startDate + " " + common.createWhatsOnModel.startTime,
                    Constant().ddMmYyyyHhMmA,
                    Constant().yyyyMmDdHhMmSs
                ),
                "" + common.convertDateInFormat(
                    common.createWhatsOnModel.endDate + " " + common.createWhatsOnModel.endTime,
                    Constant().ddMmYyyyHhMmA,
                    Constant().yyyyMmDdHhMmSs
                ),
                "" + common.createWhatsOnModel.music,
                "" + common.createWhatsOnModel.djLineUpName,
                "" + common.createWhatsOnModel.entertainment,
                "" + common.createWhatsOnModel.dressCode,
                "" + common.createWhatsOnModel.entryPolicy,
                "" + common.createWhatsOnModel.otherInfo,
                images,
                common.convertDateInFormat(
                    common.createWhatsOnModel.startDate,
                    Constant().ddMmmYyyy,
                    Constant().eeeDdMmmYyyy
                )!!,
                common.createWhatsOnModel.startTime,
                common.convertDateInFormat(
                    common.createWhatsOnModel.endDate,
                    Constant().ddMmmYyyy,
                    Constant().eeeDdMmmYyyy
                )!!,
                common.createWhatsOnModel.endTime,
                tickets, whatOnId.ifEmpty { "" }
            )


            val call: Call<CommonRes?>? = if (whatOnId.isNotEmpty()) apiInterface.doUpdateWhatsOn(
                PrefManager.getAccessToken(),
                createWhatsOnReq) else apiInterface.doCreateWhatsOn(PrefManager.getAccessToken(),
                createWhatsOnReq)
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {

                    isFirstPopup = false
                    showCommonInfoBottomFragment(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_pass_success)!!,
                        getString(R.string.txt_whats_on_activated),
                        response.body()!!.msg!!, "",
                        getString(R.string.txt_done),
                        infoDialogListener
                    )
                    common.createWhatsOnModel.addTickets.clear()
                    common.createWhatsOnModel.updateTickets.clear()
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }



    fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        messageDesc: String,
        negativeText: String,
        positiveText: String,
        listener: InfoDialogListener
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                messageDesc,
                negativeText,
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                listener
            )
        commonInfoBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { commonInfoBottomFragment.show(it, "") }
    }

    var infoDialogListener : InfoDialogListener = object : InfoDialogListener(){

        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            if (isFirstPopup){
                callCreateWhatsOnApi()
            }else{
                bottomSheetDialog.dismiss()
                setFragmentWithStack(WhatsOnFragment.newInstance(),
                    WhatsOnFragment::class.java.simpleName)
            }
        }

    }

    private fun setPreviewData() {
        binding.inclPreview.tvOfferPreviewName.text = venueOffer!!.data!!.venue_name
        binding.inclPreview.tvLocation.text = venueOffer!!.data!!.venue_address
        binding.inclPreview.tvDistance.text = getString(R.string.txt_default_distance)
        binding.tvTitle.text = common.createWhatsOnModel.title
        binding.tvVenueType.text = venueOffer!!.data!!.venuetypes[0].venue_type!!

        common.loadImageFromUrl(
            binding.inclPreview.ivOfferPreview,
            common.createWhatsOnModel.banner
        )

        if (whatOnId.isNotEmpty()) {
            binding.tvSubmitSave.text = getString(R.string.txt_update)
        } else {
            binding.tvSubmitSave.text = getString(R.string.txt_submit)
        }

        binding.tvDateTime.text =
            common.convertDateInFormat(
                common.createWhatsOnModel.startDate,
                Constant().ddMmmYyyy, Constant().eeeDdMmmYyyy
            )!! +
                    " • " + common.createWhatsOnModel.startTime


        if (common.createWhatsOnModel.whatsOnImages != null && common.createWhatsOnModel.whatsOnImages!!.isNotEmpty()) {

            if (common.createWhatsOnModel.whatsOnImages!![0]!!.contains(Constant().pdf)) {
                Glide.with(binding.ivWhatsOn.context)
                    .load(ContextCompat.getDrawable(binding.ivWhatsOn.context, R.drawable.ic_pdf))
                    .into(binding.ivWhatsOn)
            } else {
                common.loadImageFromUrl(binding.ivWhatsOn, common.createWhatsOnModel.whatsOnImages!![0])

            }

        } else {
            binding.ivWhatsOn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorValue
                )
            )
        }


        binding.tvMusic.text =
            common.createWhatsOnModel.music
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ").ifEmpty { getString(R.string.txt_not_available) }
        binding.tvEntertainment.text =
            common.createWhatsOnModel.entertainment
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ").ifEmpty { getString(R.string.txt_not_available) }
    }

    private fun openWhatsONViewMoreDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogWhatsOnPreviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_whats_on_preview,
            null,
            false
        )

        val displaymetrics = DisplayMetrics()

        mActivity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        binding.clPass.height = (height * 0.85).toInt()
        binding.clPass.requestLayout()

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val list: ArrayList<String> = ArrayList()

        list.clear()
        for (i in 0 until common.createWhatsOnModel.whatsOnImages!!.size) {
            list.add(common.createWhatsOnModel.whatsOnImages!![i]!!)
        }

        if(list.isEmpty()){
            binding.ivDefault.visibility= View.VISIBLE
        }else{
            binding.ivDefault.visibility= View.GONE
        }

        val imageViewAdapter = WhatsOnPreviewImageAdapter(list){

        }
        binding.rvEventImages.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvEventImages.adapter = imageViewAdapter
        binding.rvEventImages.onFlingListener = null

        binding.arIndicator.attachTo(binding.rvEventImages, true)
        binding.arIndicator.numberOfIndicators = list.size

        binding.inclName.desc = common.createWhatsOnModel.title
        binding.inclDesc.desc = common.createWhatsOnModel.description

        binding.inclStartDate.desc = common.convertDateInFormat(
            common.createWhatsOnModel.startDate,
            Constant().ddMmmYyyy,
            Constant().eeeDdMmmYyyy
        )
        binding.inclEndDate.desc = common.convertDateInFormat(
            common.createWhatsOnModel.endDate,
            Constant().ddMmmYyyy,
            Constant().eeeDdMmmYyyy
        )
        binding.inclStartTime.desc = common.createWhatsOnModel.startTime
        binding.inclEndTime.desc = common.createWhatsOnModel.endTime

        binding.inclEventMusic.desc =
            common.createWhatsOnModel.music
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ").ifEmpty { getString(R.string.txt_not_available) }
        binding.inclEventEntertainment.desc =
            common.createWhatsOnModel.entertainment
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ").ifEmpty { getString(R.string.txt_not_available) }

        binding.inclEventDjLineUp.desc =
            common.createWhatsOnModel.djLineUpName.ifEmpty { getString(R.string.txt_not_available) }
        binding.inclEventDressCode.desc =
            common.createWhatsOnModel.dressCode.ifEmpty { getString(R.string.txt_not_available) }
        binding.inclEventEntryPolicy.desc =
            common.createWhatsOnModel.entryPolicy.ifEmpty { getString(R.string.txt_not_available) }
        binding.inclEventOtherInfo.desc =
            common.createWhatsOnModel.otherInfo.ifEmpty { getString(R.string.txt_not_available) }

        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.isFocusable = true
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    override fun onResume() {
        super.onResume()
    }
}