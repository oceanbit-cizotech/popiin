package com.popiin.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.OfferPreviewAdapter
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.FragmentOfferPreviewBinding
import com.popiin.req.MakeVenueOfferLiveReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueOfferRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OfferPreviewFragment : BaseFragment<FragmentOfferPreviewBinding>() {
    private var offerPreviewList: ArrayList<VenueListRes.Offerslive> = ArrayList()
    private lateinit var offerPreviewAdapter: OfferPreviewAdapter
    override fun getLayout(): Int {
        return R.layout.fragment_offer_preview
    }

    companion object {
        var venueOfferData: VenueOfferRes.Data? = null
        var venueData: VenueListRes.Venue? = null
        fun newInstance(
            data: VenueOfferRes.Data?,
            selectedVenues: VenueListRes.Venue?,
        ): OfferPreviewFragment {
            venueOfferData = data
            venueData = selectedVenues
            return OfferPreviewFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.tvSubmitSave.setOnClickListener {
            if (venueOfferData!!.offers!!.isEmpty()){
                common.openDialog(requireActivity(),getString(R.string.txt_add_offer_venue))
            }else{
                openActivateOfferDialog(false,getString(R.string.txt_do_you_want_to),getString(R.string.txt_activate_offer))
            }

        }

        binding.inclPreview.tvOfferPreviewName.text = venueOfferData!!.venue_name
        if(venueOfferData!!.venuecategories!!.size>0) {
            binding.tvVenueType.text = venueOfferData!!.venuecategories!![0].venue_type!!
        }
        binding.inclPreview.tvLocation.text = venueOfferData!!.venue_address

        common.loadImageFromUrl(binding.inclPreview.ivOfferPreview, venueOfferData!!.venue_banner_image)


        offerPreviewList.clear()
        offerPreviewList.addAll(venueOfferData!!.offers!!)

        offerPreviewAdapter = OfferPreviewAdapter(offerPreviewList)
        binding.rvOffersPreview.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvOffersPreview.adapter = offerPreviewAdapter

        binding.llEdit.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }
    }

    fun openActivateOfferDialog(isActivated:Boolean,title: String, message: String?) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        if (!isActivated){
            binding.btnNo.visibility = View.VISIBLE
            binding.ivClose.visibility = View.VISIBLE
            binding.btnNo.text = getString(R.string.txt_no)
            binding.btnDone.text = getString(R.string.txt_yes_activate)
            binding.ivPassSuccess.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_sure_delete))
        }else{
            binding.btnNo.visibility = View.GONE
            binding.ivClose.visibility = View.GONE
            binding.btnDone.text =getString(R.string.txt_done)
            binding.ivPassSuccess.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_pass_success))
        }


        binding.tvPassSuccess.text = title
        binding.tvSuccess.text = message

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (!isActivated){
                // call activate offer live
                callMakeVenueOffLiveApi()
            }else{
                mActivity!!.supportFragmentManager.popBackStack()
            }
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun callMakeVenueOffLiveApi() {
        if (isInternetConnect()) {
            showProgress()
            var strOffer = ""
            for (i in 0 until venueOfferData!!.offers!!.size) {
                strOffer = if (venueOfferData!!.offers!!.size - 1 == i) {
                    strOffer + venueOfferData!!.offers!![i].id
                } else {
                    strOffer + venueOfferData!!.offers!![i].id.toString() + ", "
                }
            }
            val call: Call<CommonRes?>? = apiInterface.doMakeVenueOfferLive(
                PrefManager.getAccessToken(),
                MakeVenueOfferLiveReq(venueData!!.id, strOffer)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            openActivateOfferDialog(true,getString(R.string.txt_offer_activated),response.body()!!.msg)
                        }
                    }

                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }
}