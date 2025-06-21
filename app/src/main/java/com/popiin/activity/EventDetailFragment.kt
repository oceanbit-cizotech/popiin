package com.popiin.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ParseException
import android.provider.CalendarContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentEventDetailsBinding
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.DialogSpecialRequestBinding
import com.popiin.dialog.DirectionsDialog
import com.popiin.fragment.ImagePreviewFragment
import com.popiin.listener.InfoDialogListener
import com.popiin.res.EventListRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.google.android.gms.maps.model.LatLng
import com.popiin.activity.ExploreDetailsFragment.Companion.exploreItem
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.BranchIOListener
import com.popiin.listener.FavoriteListener
import com.popiin.realm.EventFavorites
import com.popiin.realm.VenuesFavorites
import com.popiin.req.MarkFavouriteReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.util.Parameters
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventDetailFragment : BaseFragment<FragmentEventDetailsBinding>() {
    private var eventItem: EventListRes.Event? = null
    private var isViewBookCode = true
    var favorites : FavoriteListener?=null

    override fun getLayout(): Int {
        return R.layout.fragment_event_details

    }

    companion object {
        fun newInstance(): EventDetailFragment {
            return EventDetailFragment()
        }
    }

    override fun initViews() {
        eventItem = PrefManager.getEvent()
        binding.model = eventItem
        binding.isFavorite=eventItem!!.isFavorite
        if(eventItem?.has_ticket == 1){
            if(eventItem?.tickets?.get(0)?.price == 0.0){
                binding.price="Free"
            }else{
                binding.price="£" + eventItem?.tickets?.get(0)?.price?.let {
                    common.getDecimalFormatValue(
                        it
                    )
                }
            }
        }else{
            binding.price=""
        }

        binding.common = common
        binding.username =PrefManager.getName()

        common.loadImageFromUrl(binding.ivEvent, eventItem!!.images?.get(0)!!.image)

        binding.tvAddToCalendar.setOnClickListener {
            showCommonInfoBottomFragment(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_sure_delete),
                getString(R.string.txt_add_to_calender),
                eventItem!!.name + " will added to the calendar on your device.",
                getString(R.string.txt_no),
                getString(R.string.txt_yes_add),
                infoDialogListener
            )
        }

        if (eventItem!!.music != null && eventItem!!.music!!.isNotEmpty()) {
            binding.inclEventMusic.root.visibility = View.VISIBLE
            val strArray: String?
            strArray = eventItem!!.music!!.replace(
                CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                ", "
            )
                .replace(CONSTANT.SEPRATEOR, ", ")
            binding.inclEventMusic.desc = strArray
        } else {
            binding.inclEventMusic.root.visibility = View.GONE
        }

        if (eventItem!!.entertainment != null && eventItem!!.entertainment!!.isNotEmpty()) {
            binding.inclEventEntertainment.root.visibility = View.VISIBLE
            val strArray: String?
            strArray = eventItem!!.entertainment!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ")
                .replace(Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            binding.inclEventEntertainment.desc = strArray
        } else {
            binding.inclEventEntertainment.root.visibility = View.GONE
        }

        if (eventItem!!.category != null && eventItem!!.category!!.isNotEmpty()) {
            binding.inclEventTags.root.visibility = View.VISIBLE
            val strArray: String?
            strArray = eventItem!!.category!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ")
                .replace(Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            binding.inclEventTags.desc = strArray
        } else {
            binding.inclEventTags.root.visibility = View.GONE
        }

        if (eventItem!!.other_information != null && eventItem!!.other_information!!.isNotEmpty()) {
            binding.inclEventOtherInfo.root.visibility = View.VISIBLE
            binding.inclEventOtherInfo.desc = eventItem!!.other_information
        } else {
            binding.inclEventOtherInfo.root.visibility = View.GONE
        }

        if (eventItem!!.entry_policy != null && eventItem!!.entry_policy!!.isNotEmpty()) {
            binding.inclEventEntryPolicy.root.visibility = View.VISIBLE
            binding.inclEventEntryPolicy.desc = eventItem!!.other_information
        }else{
            binding.inclEventEntryPolicy.root.visibility = View.GONE
        }


        binding.topHeader.ivShare.setOnClickListener {
            val link = if (eventItem!!.share_link == null) "" else eventItem!!.share_link
            val shareBody = """
                ${Constant().eventNameHeader} ${eventItem!!.name.toString()}
                ${Constant().venue} ${eventItem!!.venue.toString()}
                ${Constant().address} ${eventItem!!.address.toString()},${eventItem!!.city.toString()}
                ${Constant().description} ${eventItem!!.description.toString()}
                $link ${getString(R.string.txt_app_download)}""".trimIndent()
            shareMessage(shareBody)
        }

        binding.tvInviteFriends.setOnClickListener {
           // shareMessage(Constant().shareLink + "\n" + getString(R.string.txt_app_download))
            binding.ivShare.callOnClick()
        }

        binding.tvSpecialRequest.setOnClickListener {
            openSpecialRequestDialog()
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }


        binding.tvGetDirections.setOnClickListener {
            if (!common.isAllowClick) {
                return@setOnClickListener
            }
            val directionDialog = DirectionsDialog(
                mActivity!!,
                LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude),
                LatLng(eventItem!!.lat, eventItem!!.longi)
            )
            directionDialog.show()
        }

        binding.tvViewBookingCode.setOnClickListener {
            setFragmentWithStack(
                BookNowFragment.newInstance(eventItem!!, null),
                BookNowFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.ivEvent.setOnClickListener{
            var list: ArrayList<String> = ArrayList()
            eventItem!!.images!!.forEach{  images ->
                list.add(images.image)
            }
            setFragmentAdd(
                ImagePreviewFragment.newInstance(list,0),
                ImagePreviewFragment::class.java.simpleName
            )
        }

        binding.inclEventTags.root.visibility = View.GONE

        binding.cbLike.setOnClickListener {
            if (eventItem!!.isFavorite == 0) {
                setFavoriteData(eventItem!!)
                callEventFavourite(eventItem!!.id.toString(), 1)
                favorites?.onStatusUpdates(eventItem!!.id.toInt(), 1,SHARE_MESSAGE_TYPE.EVENT)
            } else {
                setFavoriteData(eventItem!!)
                callEventFavourite(eventItem!!.id.toString(), 0)
                favorites?.onStatusUpdates(eventItem!!.id.toInt(), 0,SHARE_MESSAGE_TYPE.EVENT)
            }
        }

        binding.ivShare.setOnClickListener {
            if(eventItem!!.share_link!=null && eventItem!!.share_link!!.isNotEmpty()){
                shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = eventItem!!.name, url =eventItem!!.share_link!! )
            }else {
                val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().eventId, "" + id)
                shareBranchIOLink(properties, object : BranchIOListener() {
                    override fun onLinkCreate(url: String?) {
                        super.onLinkCreate(url)
                        shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = eventItem!!.name, url =url!! )
                        callPostUpdateLinkApi(eventItem!!.id, url!!)
                    }
                })
            }
        }

        if(eventItem!!.tickets!!.isEmpty()){
            binding.tvViewBookingCode.visibility=View.INVISIBLE
        }else{
            binding.tvViewBookingCode.visibility=View.VISIBLE
        }

    }

    private fun callEventFavourite(eventId: String, status: Int) {
        if (!isInternetConnect()) {
            return
        }
        val call: Call<CommonRes?>? = apiInterface.marFavourite(PrefManager.getAccessToken(),
            MarkFavouriteReq("" + eventId, 2, status)
        )
        call!!.enqueue(object : Callback<CommonRes?> {
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                eventItem?.isFavorite=status
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    fun setFavoriteData(item: EventListRes.Event) {
        val userId: Int = PrefManager.getUser()!!.user!!.id
        if (realmController!!.isFavoritesEvents(userId, item.id) == 1) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(EventFavorites::class.java).equalTo("id", item.id)
                        .findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavoritesEvents(userId, item.id) == 0) {
            realm!!.executeTransaction { realm ->
                val rl: EventFavorites? =
                    realm.where(EventFavorites::class.java).equalTo("id", item.id).findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = EventFavorites(PrefManager.getUser()!!.user!!.id, item.id, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
    }

    private fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        negativeText: String,
        positiveText: String,
        listener: InfoDialogListener
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
            addEvents(
                eventItem!!.start_date_time,
                eventItem!!.end_date_time,
                eventItem!!.name,
                eventItem!!.description
            )
        }

    }

    private fun openSpecialRequestDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogSpecialRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_special_request, null, false
        )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
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

    private fun openAddCalendarDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_reset_password, null, false)

        binding.tvPassSuccess.text = getString(R.string.txt_event_added)
        binding.tvSuccess.text = getString(R.string.txt_calendat_success)

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDone.setOnClickListener {
           dialog.dismiss()
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


    @SuppressLint("SimpleDateFormat")
    fun addEvents(strStartDate: String?, strEndDate: String?, name: String?, description: String?) {
        val cr = getBaseActivity()!!.contentResolver
        val values = ContentValues()
        val formatter = SimpleDateFormat(Constant().yyyyMmDdHhMmSs)
        val startDateCalender = Calendar.getInstance()
        val endDateCalender = Calendar.getInstance()
        val startDate: Date
        val endDate: Date
        if (strStartDate == null) {
            return
        }
        try {
            startDate = formatter.parse(strStartDate) as Date
            endDate = formatter.parse(strEndDate!!)!!
            startDateCalender.time = startDate
            endDateCalender.time = endDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        values.put(CalendarContract.Events.DTSTART, startDateCalender.timeInMillis)
        values.put(CalendarContract.Events.DTEND, endDateCalender.timeInMillis)
        values.put(CalendarContract.Events.TITLE, name)
        values.put(CalendarContract.Events.DESCRIPTION, description)
        val timeZone = TimeZone.getDefault()
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.id)

        // default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1)


        // for one hour
        //  values.put(CalendarContract.Events.DURATION, "+P1H");
        values.put(CalendarContract.Events.HAS_ALARM, 1)

        // insert event to calendar
        cr.insert(CalendarContract.Events.CONTENT_URI, values)

        openAddCalendarDialog()
    }
}