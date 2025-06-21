package com.popiin.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.EventAttendeesFragment
import com.popiin.activity.MyEventsFragment
import com.popiin.activity.MyVenueFragment
import com.popiin.activity.VenueAttendeesFragment
import com.popiin.activity.VenueReservationFragment
import com.popiin.activity.setting.WithdrawFragment
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.ActivityAccountManagementBinding
import com.popiin.listener.InfoDialogListener
import com.popiin.res.CreateStripeLinkRes
import com.popiin.res.LoginRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AccountManagementFragment : BaseFragment<ActivityAccountManagementBinding>() {
    override fun getLayout(): Int {
        return R.layout.activity_account_management
    }

    companion object{
        fun newInstance(): AccountManagementFragment {
            return AccountManagementFragment()
        }
    }

    override fun initViews() {
        setUiWithEventVenueData()
        if (PrefManager.getUser().user != null && PrefManager.getUser().user!!.is_stripe_verified ==1) {
            binding.extraText = getString(R.string.lbl_am_verified)
            binding.isStripeVerified = true
        } else {
            binding.isStripeVerified = false
            binding.extraText = getString(R.string.txt_not_verified)
        }

        binding.topHeader.ivBack.setOnClickListener {
            //onBackPressed()
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.inclMyEvents.root.setOnClickListener {
            setFragmentAdd(
                MyEventsFragment.newInstance(),
                MyEventsFragment::class.java.simpleName
            )
        }

        binding.inclMyVenues.root.setOnClickListener {
            setFragmentAdd(
                MyVenueFragment.newInstance(),
                MyVenueFragment::class.java.simpleName
            )
        }

        binding.inclVenueReserve.root.setOnClickListener {
            setFragmentWithStack(
                VenueReservationFragment.newInstance(),
                VenueReservationFragment::class.java.simpleName
            )
            mActivity?.overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }

        binding.inclEventBookAttendees.root.setOnClickListener {
            setFragmentAdd(
                EventAttendeesFragment.newInstance(),
                EventAttendeesFragment::class.java.simpleName
            )
            mActivity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclVenueBookAttendees.root.setOnClickListener {
            setFragmentAdd(
                VenueAttendeesFragment.newInstance(),
                VenueAttendeesFragment::class.java.simpleName
            )
            mActivity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclWhatsonBookAttendees.root.setOnClickListener {
            setFragmentAdd(
                WhatsOnAttendeesFragment.newInstance(),
                WhatsOnAttendeesFragment::class.java.simpleName
            )
            mActivity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        binding.inclWhatsonBookAttendees.root.visibility=View.GONE

        binding.inclAddPaymentDetails.root.setOnClickListener {
            if(binding.isStripeVerified==true){
                getCreateStripeLink(true)
            }else{
                showCommonInfoBottomFragment(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_payment_info),
                    getString(R.string.txt_add_payment_details),
                    getString(R.string.msg_stripe),
                    getString(R.string.lbl_not_now),
                    getString(R.string.txt_countinue),
                    stripeDialogListener
                )
            }
        }

        binding.inclScanCode.root.setOnClickListener {
            setFragmentAdd(
                BusinessScanCodeFragment.newInstance(false),
                BusinessScanCodeFragment::class.java.simpleName
            )
        }
        binding.inclTrending.root.setOnClickListener {
            setFragmentAdd(
                TrendingFragment.newInstance(),
                TrendingFragment::class.java.simpleName
            )
        }

        binding.inclWithdraw.root.setOnClickListener {
            setFragmentAdd(
                WithdrawFragment.newInstance(),
                WithdrawFragment::class.java.simpleName
            )
            mActivity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclWhatsOn.root.visibility=View.GONE

        binding.inclWhatsOn.root.setOnClickListener {
            if(binding.isStripeVerified==true){
                setFragmentWithStack(
                    WhatsOnFragment.newInstance(),
                    WhatsOnFragment::class.java.simpleName
                )
            }else{
                showCommonInfoBottomFragment(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_payment_info),
                    getString(R.string.txt_add_payment_details),
                    getString(R.string.msg_stripe),
                    getString(R.string.lbl_not_now),
                    getString(R.string.txt_countinue),
                    stripeDialogListener
                )
            }

        }

        binding.inclStory.root.setOnClickListener {
            setFragmentWithStack(
                StoryFragment.newInstance(),
                StoryFragment::class.java.simpleName
            )
            mActivity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclOffers.root.setOnClickListener {
            setFragmentWithStack(
                OffersFragment.newInstance(),
                OffersFragment::class.java.simpleName
            )
        }

        binding.inclSalesReport.root.setOnClickListener {
            setFragmentWithStack(
                SalesReportFragment.newInstance(),
                SalesReportFragment::class.java.simpleName
            )
        }

        if(PrefManager.config?.trendingFlag==0) {
            binding.inclTrending.root.visibility = View.GONE
        }else{
            binding.inclTrending.root.visibility = View.VISIBLE
        }

    }

    var stripeDialogListener: InfoDialogListener = object : InfoDialogListener() {

        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            getCreateStripeLink(true)
            bottomSheetDialog.dismiss()
        }

    }

    private fun setUiWithEventVenueData() {
        if(PrefManager.getHasEvent() > 0 && PrefManager.getHasVenue()==0) {
            binding.inclMyVenues.root.visibility=View.GONE
            binding.inclTrending.root.visibility=View.GONE
            binding.inclVenueReserve.root.visibility=View.GONE
            binding.inclOffers.root.visibility=View.GONE
            binding.inclStory.root.visibility=View.GONE
            binding.inclWhatsOn.root.visibility=View.GONE
            binding.inclWhatsonBookAttendees.root.visibility=View.GONE
            binding.inclVenueBookAttendees.root.visibility=View.GONE
        }
    }

    fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        negativeText: String,
        positiveText: String,
        listener: InfoDialogListener,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                negativeText,
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                listener
            )
        commonInfoBottomFragment.isCancelable = false
        mActivity!!.supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
    }

    private fun getCreateStripeLink(isLoaderDisplay: Boolean) {
        if(isInternetConnect()) {
            if (isLoaderDisplay) {
                showProgress()
            }

            val call: Call<CreateStripeLinkRes?>? = apiInterface.createStripeLink(
                PrefManager.getAccessToken(),
            )
            call!!.enqueue(object : Callback<CreateStripeLinkRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<CreateStripeLinkRes?>,
                    response: Response<CreateStripeLinkRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()?.success == true) {
                            var url = response.body()!!.data.url
                            val i = Intent(Intent.ACTION_VIEW)
                            i.setData(Uri.parse(url))
                            startActivity(i)
                            hideProgress()

                        } else {
                            hideProgress()
                        }
                    }
                    hideProgress()

                }

                override fun onFailure(call: Call<CreateStripeLinkRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    private fun callUserApi() {

        if (isInternetConnect()) {
            val call: Call<LoginRes> = apiInterface.doGetUser(PrefManager.getAccessToken())
            call!!.enqueue(object : Callback<LoginRes> {
                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    if (response.body()!!.success) {
                        PrefManager.setUser(response.body()!!)
                        PrefManager.isStripeVerified = response.body()!!.user!!.is_stripe_verified
                        binding.isStripeVerified= response.body()!!.user!!.is_stripe_verified==1
                        PrefManager.setHasEvent(response.body()!!.user!!.hasEvent)
                        PrefManager.setHasEvent(response.body()!!.user!!.hasVenue)
                    }
                }

                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        callUserApi()
    }

}