package com.popiin.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.*
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.MyBookingAdapter
import com.popiin.adapter.MyVenueBookAdapter
import com.popiin.adapter.MyWhatsOnBookAdapter
import com.popiin.bottomDialogFragment.AddSpecialRequestBottomFragment
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentMyBokingsBinding
import com.popiin.dialog.CommonDialog
import com.popiin.dialog.DirectionsDialog
import com.popiin.fragment.WhatsOnQRCodeFragment
import com.popiin.listener.AdapterMyBookingListener
import com.popiin.listener.AdapterMyVenueBookListener
import com.popiin.listener.AddSpecialRequiremnetListener
import com.popiin.listener.InfoDialogListener
import com.popiin.req.CancelBookingReq
import com.popiin.req.MakeSpecialRequestReq
import com.popiin.req.MyBookingReq
import com.popiin.res.*
import com.popiin.util.Constant
import com.popiin.util.HorizontalMarginItemDecoration
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.fragment.WhatsOnEventBookFragment
import com.popiin.listener.BranchIOListener
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyBookingsFragment : BaseFragment<FragmentMyBokingsBinding>() {
    var myBookingAdapter: MyBookingAdapter? = null
    var myBooking: ArrayList<MyBookingRes.Data?> = ArrayList()
    var myVenueBookAdapter: MyVenueBookAdapter? = null
    var myVenues: ArrayList<VenueAttendeesRes.Data?> = ArrayList()
    var myWhatsonBookData: ArrayList<MyWhatsonBookRes.Data> = ArrayList()
    lateinit var adapter: MyWhatsOnBookAdapter
    var pageWhatsOnNo = defaultPage
    var isLoading = false
    var pageNo = defaultPage
    var pageNoVenue = defaultPage
    var isNextFragment = false
    var selectedIndex = 0
    var layoutManager: LinearLayoutManager? = null
    override fun getLayout(): Int {
        return R.layout.fragment_my_bokings
    }

    companion object {
        var selectedTab:Int=0
        //1 means venue
        //0 means event
        //2 means whats on
        fun newInstance(type:Int): MyBookingsFragment {
            selectedTab=type
            return MyBookingsFragment()
        }
    }

    override fun initViews() {
        selectedIndex= selectedTab
        pageNo = defaultPage
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab: View = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.height = 85
            layoutParams.setMargins(0, 0, 24, 0)
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }

        binding.topHeader.ivBack.setOnClickListener {
         //   mActivity!!.supportFragmentManager.popBackStack()
            val fragmentManager = mActivity!!.supportFragmentManager
            var fragmentTag = "VenueBookFragment" // Use the same tag used when adding the fragment

            var isFragmentInBackStack = false
            for (i in 0 until fragmentManager.backStackEntryCount) {
                val entry = fragmentManager.getBackStackEntryAt(i)
                if (entry.name == fragmentTag) {
                    isFragmentInBackStack = true
                    break
                }else if(entry.name == BookNowFragment::class.java.simpleName){
                    fragmentTag=BookNowFragment::class.java.simpleName
                    isFragmentInBackStack = true
                    break
                }else if(entry.name == WhatsOnEventBookFragment::class.java.simpleName){
                    fragmentTag=WhatsOnEventBookFragment::class.java.simpleName
                    isFragmentInBackStack = true
                    break
                }
            }

            if (isFragmentInBackStack) {
                fragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }else{
                mActivity!!.supportFragmentManager.popBackStack()
            }
        }

        myBookingAdapter = MyBookingAdapter(myBooking, adapterMyBookingListener)
        myVenueBookAdapter = MyVenueBookAdapter(myVenues, adapterMyVenuesListener)
        adapter = MyWhatsOnBookAdapter(myWhatsonBookData, listener)
        binding.rvMyBookings.isNestedScrollingEnabled = false
        PagerSnapHelper().attachToRecyclerView(binding.rvMyBookings)
        true.updateList(pageNo)


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isLoading = true
                if (tab.position == 0) {
                    selectedIndex = 0
                    layoutManager =
                        LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvMyBookings.layoutManager = layoutManager
                    binding.rvMyBookings.adapter = myBookingAdapter
                    showProgress()
                    pageNo = defaultPage
                    true.updateList(pageNo)
                } else if (tab.position == 1) {
                    selectedIndex = 1
                    layoutManager =
                        LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvMyBookings.layoutManager = layoutManager
                    binding.rvMyBookings.adapter = myVenueBookAdapter
                    if (isNextFragment) {
                        isNextFragment = false
                    } else {
                        showProgress()
                        pageNoVenue = defaultPage
                        true.callToGetVenues(pageNoVenue)

                    }
                } else if (tab.position == 2) {
                    selectedIndex = 2
                    layoutManager =
                        LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
                    binding.rvMyBookings.layoutManager = layoutManager
                    binding.rvMyBookings.adapter = adapter
                    if (isNextFragment) {
                        isNextFragment = false
                    } else {
                        showProgress()
                        pageWhatsOnNo = defaultPage
                        callWhatsOnBookingApi(pageWhatsOnNo, true)

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedIndex))
        if (selectedIndex == 0) {
            layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.rvMyBookings.layoutManager = layoutManager
            binding.rvMyBookings.adapter = myBookingAdapter
            if (myBooking.size == 0) {
                showProgress()
                pageNo = defaultPage
                true.updateList(pageNo)
            }
        } else if (selectedIndex == 1) {
            layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.rvMyBookings.layoutManager = layoutManager
            binding.rvMyBookings.adapter = myVenueBookAdapter
            if (myVenues.size == 0) {
                showProgress()
                pageNoVenue = defaultPage
                true.callToGetVenues(pageNoVenue)
            }
        } else if (selectedIndex == 2) {
            layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            binding.rvMyBookings.layoutManager = layoutManager
            binding.rvMyBookings.adapter = adapter
            if (myWhatsonBookData.size == 0) {
                showProgress()
                pageWhatsOnNo = defaultPage
                callWhatsOnBookingApi(pageWhatsOnNo, true)
            }
        }


        val itemDecoration = HorizontalMarginItemDecoration(
            requireActivity(),
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.rvMyBookings.addItemDecoration(itemDecoration)
    }

    var listener: AdapterMyBookingListener<MyWhatsonBookRes.Data> =
        object : AdapterMyBookingListener<MyWhatsonBookRes.Data>() {
            override fun onViewBookingCode(item: MyWhatsonBookRes.Data, position: Int) {
                super.onViewBookingCode(item, position)
                setFragmentWithStack(WhatsOnQRCodeFragment.newInstance(item),
                    WhatsOnQRCodeFragment::class.java.simpleName)
            }

            override fun onCancel(item: MyWhatsonBookRes.Data, position: Int) {
                super.onCancel(item, position)
                bookingId = "" + item.id
                isVenueCancel = false
                isCancelSucess = false
                showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_failed),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_cancel_booking),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_cancel),
                    cancelListener)
            }

            override fun onDirectionClick(item: MyWhatsonBookRes.Data, position: Int) {
                super.onDirectionClick(item, position)

                val directionDialog = DirectionsDialog(
                    getBaseActivity()!!, LatLng(
                        PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude
                    ), LatLng(
                        item.venue?.venue_latitude!!, item.venue?.venue_longitude!!
                    )
                )
                directionDialog.show()
            }
        }


    var bookingId = ""
    var isVenueCancel = false
    var cancelListener: InfoDialogListener = object : InfoDialogListener() {

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
            bottomSheetDialog.dismiss()
            if (!isCancelSucess) {
                if (isVenueCancel) {
                    callCancelBookingApi(venueBookingId, true)
                } else {
                    callCancelBookingApi(bookingId, false)
                }
            }


        }

    }

    private fun Boolean.updateList(pageNo: Int) {
        if (!isInternetConnect()) {
            return
        }
        if (this) {
            showProgress()
        }
        val call: Call<MyBookingRes?>? = apiInterface.getMyBookings(
            PrefManager.getAccessToken(),
            MyBookingReq(pageNo, pageLimit)
        )
        call!!.enqueue(object : Callback<MyBookingRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<MyBookingRes?>, response: Response<MyBookingRes?>) {
                if (isValidResponse(response)) {
                    if (this@MyBookingsFragment.pageNo == defaultPage) {
                        myBooking.clear()
                    }
                    if (response.body()!!.success) {
                        myBooking.clear()
                        myBooking.addAll(response.body()!!.data!!)
                        myBookingAdapter!!.notifyDataSetChanged()
                        isLoading = true
                        this@MyBookingsFragment.pageNo++
                    } else {
                        isLoading = false
                    }

                    if (myBooking.size == 0) {
                        binding.tvMessage.visibility = View.VISIBLE
                        binding.tvMessage.text = getString(R.string.no_booking_to_display)
                    } else {
                        binding.tvMessage.visibility = View.GONE
                    }

                    if (this@updateList) {
                        hideProgress()
                    }
                }
            }

            override fun onFailure(call: Call<MyBookingRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    private fun Boolean.callToGetVenues(pageNoVenue: Int) {
        if (isInternetConnect()) {
            if (this) {
                showProgress()
            }
            val call: Call<VenueAttendeesRes?>? =
                apiInterface.getVenueAttendeesList(
                    PrefManager.getAccessToken(),
                    pageNoVenue,
                    pageLimit
                )
            call!!.enqueue(object : Callback<VenueAttendeesRes?> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueAttendeesRes?>,
                    response: Response<VenueAttendeesRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (this@MyBookingsFragment.pageNo == defaultPage) {
                            myVenues.clear()
                        }
                        if (response.body()!!.success) {
                            myVenues.clear()
                            myVenues.addAll(response.body()!!.data!!)
                            myVenueBookAdapter!!.notifyDataSetChanged()
//                            binding.tvMessage.setVisibility(View.GONE)
                            isLoading = true
                            this@MyBookingsFragment.pageNo++
                        } else {
                            isLoading = false
                        }
                        if (myVenues.size == 0) {
                            binding.tvMessage.visibility = View.VISIBLE
                            binding.tvMessage.text = getString(R.string.no_booking_to_display)
                        } else {
                            binding.tvMessage.visibility = View.GONE
                        }
                        if (this@callToGetVenues) {
                            hideProgress()
                        }
                    }
                }

                override fun onFailure(call: Call<VenueAttendeesRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        } else {
            showToast(getString(R.string.noInternetConnection))
        }
    }

    private fun callWhatsOnBookingApi(pageNo: Int, isLoad: Boolean) {
        if (isInternetConnect()) {
            if (isLoad) {
                showProgress()
            }
            val call: Call<MyWhatsonBookRes?>? =
                apiInterface.myWhatsOnBooking(PrefManager.getAccessToken(),
                    MyBookingReq(pageNo, pageLimit))
            call!!.enqueue(object : Callback<MyWhatsonBookRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<MyWhatsonBookRes?>,
                    response: Response<MyWhatsonBookRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (this@MyBookingsFragment.pageNo == defaultPage) {
                            myWhatsonBookData.clear()
                        }
                        if (response.body()!!.success) {
                            myWhatsonBookData.clear()
                            myWhatsonBookData.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                            this@MyBookingsFragment.pageNo++
                        }

                        if (myWhatsonBookData.size == 0) {
                            binding.tvMessage.visibility = View.VISIBLE
                            binding.tvMessage.text =
                                getString(R.string.no_booking_to_display)
                        } else {
                            binding.tvMessage.visibility = View.GONE
                        }

                    }
                }

                override fun onFailure(call: Call<MyWhatsonBookRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    var items: MyBookingRes.Data? = null
    var positions = 0

    private var adapterMyBookingListener: AdapterMyBookingListener<MyBookingRes.Data> =
        object : AdapterMyBookingListener<MyBookingRes.Data>() {
            override fun onDirectionClick(item: MyBookingRes.Data, position: Int) {
                super.onDirectionClick(item, position)

                val directionDialog = DirectionsDialog(
                    getBaseActivity()!!, LatLng(
                        PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude
                    ), LatLng(
                        item.event!!.lat, item.event.longi
                    )
                )
                directionDialog.show()
            }

            override fun onAddToCalender(item: MyBookingRes.Data, position: Int) {
                super.onAddToCalender(item, position)
                showCommonInfoBottomFragment(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_sure_delete),
                    getString(R.string.txt_add_to_calender),
                    item.event!!.name + " " + getString(R.string.txt_will_add_to_calendar),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_add),
                    object : InfoDialogListener() {
                        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
                            super.onInfoNegativeClick(bottomSheetDialog)
                            bottomSheetDialog.dismiss()
                        }

                        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
                            super.onInfoPositiveClick(bottomSheetDialog)
                            showCommonInfoBottomFragment(
                                ContextCompat.getDrawable(requireActivity(),
                                    R.drawable.ic_pass_success),
                                getString(R.string.txt_event_added),
                                getString(R.string.txt_calendat_success),
                                "",
                                getString(R.string.txt_done),
                                infoDialogListener
                            )
                        }

                        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
                            super.onInfoCloseClick(bottomSheetDialog)
                            bottomSheetDialog.dismiss()
                        }
                    }
                )

            }

            override fun onInviteFriends(item: MyBookingRes.Data, position: Int) {
                super.onInviteFriends(item, position)
                if(item.event!!.share_link!=null && item.event.share_link!!.isNotEmpty()){
                    shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = item.event.name, url =item.event.share_link!! )
                }else {
                    val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().eventId, "" + id)
                    shareBranchIOLink(properties, object : BranchIOListener() {
                        override fun onLinkCreate(url: String?) {
                            super.onLinkCreate(url)
                            shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = item.event.name, url =url!! )
                            callPostUpdateLinkApi(item.event_id, url!!)
                        }
                    })
                }
            }

            override fun onAddSpecialRequirement(item: MyBookingRes.Data, position: Int) {
                super.onAddSpecialRequirement(item, position)
                items = item
                positions = position
                showSpecialRequirementBottomFragment(item, position)
            }

            override fun onViewBookingCode(item: MyBookingRes.Data, position: Int) {
                super.onViewBookingCode(item, position)
                setFragmentWithStack(
                    EventQRCodeFragment.newInstance(item, null),
                    EventQRCodeFragment::class.java.simpleName
                )
            }
        }

    private fun showSpecialRequirementBottomFragment(item: MyBookingRes.Data, position: Int) {
        val addSpecialRequestBottomFragment =
            AddSpecialRequestBottomFragment(specialRequirementListener, item, position)
        addSpecialRequestBottomFragment.isCancelable = false
        requireActivity().supportFragmentManager.let {
            addSpecialRequestBottomFragment.show(
                it,
                ""
            )
        }
    }

    private var specialRequirementListener: AddSpecialRequiremnetListener<MyBookingRes.Data> =
        object : AddSpecialRequiremnetListener<MyBookingRes.Data>() {
            override fun onAddClick(
                item: MyBookingRes.Data,
                position: Int,
                specialRequirement: String?,
            ) {
                super.onAddClick(item, position, specialRequirement)
                callSpecialRequestApi(specialRequirement!!)
            }
        }

    private fun callSpecialRequestApi(specialRequirement: String) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.getMakeSpecialRequest(
                PrefManager.getAccessToken(),
                MakeSpecialRequestReq(
                    "" + items!!.event_id,
                    "" + items!!.id,
                    "1",
                    specialRequirement
                )
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        myBooking[positions]!!.special_request = specialRequirement
                        myBookingAdapter!!.notifyDataSetChanged()
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    var venueBookingId = ""
    var adapterMyVenuesListener: AdapterMyVenueBookListener<VenueAttendeesRes.Data> =
        object : AdapterMyVenueBookListener<VenueAttendeesRes.Data>() {
            override fun onAddToCalender(item: VenueAttendeesRes.Data, position: Int) {
                super.onAddToCalender(item, position)
                val commonDialog = CommonDialog(
                    requireActivity(),
                    getString(R.string.txt_yes),
                    getString(R.string.txt_no),
                    getString(R.string.txt_add_to_calender),
                    getString(R.string.txt_calender_add_desc)
                )
                commonDialog.binding.btnPositive.setOnClickListener {
                    commonDialog.dismiss()
                    common.addEvents(
                        requireActivity(),
                        item.booking_datetime,
                        item.end_datetime,
                        item.venue!!.venue_name,
                        ""
                    )
                    showCommonInfoBottomFragment(
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pass_success),
                        getString(R.string.txt_event_added),
                        getString(R.string.txt_calendat_success),
                        "",
                        getString(R.string.txt_done),
                        infoDialogListener
                    )
                }

                commonDialog.binding.btnNegative.setOnClickListener {
                    commonDialog.dismiss()
                }
                commonDialog.show()
            }

            override fun onCancelClick(item: VenueAttendeesRes.Data, position: Int) {
                super.onCancelClick(item, position)
                isVenueCancel = true
                venueBookingId = "" + item.id
                isCancelSucess = false
                showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_failed),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_cancel_booking),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_cancel),
                    cancelListener)
            }

            override fun onDirectionClick(item: VenueAttendeesRes.Data, position: Int) {
                super.onDirectionClick(item, position)


                val directionDialog: DirectionsDialog
                if(item.venue?.venue_latitude==null){
                    directionDialog = DirectionsDialog(getBaseActivity()!!, LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude), LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude))
                }else{
                    directionDialog = DirectionsDialog(getBaseActivity()!!, LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude), LatLng(item.venue?.venue_latitude!!, item.venue?.venue_longitude!!))
                }
                directionDialog.show()

            }

            override fun onInviteFriends(item: VenueAttendeesRes.Data, position: Int) {
                super.onInviteFriends(item, position)
               if(item.venue!!.venue_share_link!=null && item.venue!!.venue_share_link!!.isNotEmpty()){
                    shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue!!.venue_name, url =item.venue!!.venue_share_link!! )
                }else {
                    val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().venueId, "" + id)
                    shareBranchIOLink(properties, object : BranchIOListener() {
                        override fun onLinkCreate(url: String?) {
                            super.onLinkCreate(url)
                            shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue!!.venue_name, url =url!! )
                            callPostVenueUpdateLinkApi(item.venue_id, url!!)
                        }
                    })
                }

            }

            override fun onViewBookingCode(item: VenueAttendeesRes.Data, position: Int) {
                super.onViewBookingCode(item, position)
                setFragmentWithStack(
                    EventQRCodeFragment.newInstance(null, item),
                    EventQRCodeFragment::class.java.simpleName
                )
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
        requireActivity().supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
    }

    var infoDialogListener: InfoDialogListener = object : InfoDialogListener() {

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
            bottomSheetDialog.dismiss()

        }

    }

    var isCancelSucess = false
    private fun callCancelBookingApi(bookingId: String, isVenueCancel: Boolean) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CancelBookingRes?>? =
                if (isVenueCancel) {
                    apiInterface.cancelVenueBooking(PrefManager.getAccessToken(),
                        CancelBookingReq(Constant().user, bookingId, 2))
                } else {
                    apiInterface.cancelBooking(PrefManager.getAccessToken(),
                        CancelBookingReq(Constant().user, bookingId, 2))
                }

            call!!.enqueue(object : Callback<CancelBookingRes?> {
                override fun onResponse(
                    call: Call<CancelBookingRes?>,
                    response: Response<CancelBookingRes?>,
                ) {
                    hideProgress()
                    if (response.body()!!.success) {
                        isCancelSucess = true
                        showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                            R.drawable.ic_pass_success),
                            if (isVenueCancel) getString(R.string.txt_venue_booking_cancel) else getString(
                                R.string.txt_whats_on_booking_cancel),
                            response.body()!!.message!!, "",
                            getString(R.string.txt_done), cancelListener)
                        if (isVenueCancel) {
                            false.callToGetVenues(pageNoVenue)
                        } else {
                            callWhatsOnBookingApi(pageWhatsOnNo, false)
                        }

                    } else {
                        if (response.body()!!.message != null) {
                            showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                                R.drawable.ic_failed),
                                getString(R.string.txt_failed),
                                response.body()!!.message!!, "",
                                getString(R.string.txt_done), infoDialogListener)
                        }

                    }
                }

                override fun onFailure(call: Call<CancelBookingRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }

            })
        }
    }

}