package com.popiin.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.net.ParseException
import android.provider.CalendarContract
import android.view.View
import androidx.core.content.ContextCompat
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentFavouriteEventDetailsBinding
import com.popiin.dialog.DirectionsDialog
import com.popiin.listener.InfoDialogListener
import com.popiin.res.FavouriteEventsRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.google.android.gms.maps.model.LatLng
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.listener.BranchIOListener
import com.popiin.listener.FavoriteListener
import com.popiin.realm.EventFavorites
import com.popiin.req.MarkFavouriteReq
import com.popiin.res.CommonRes
import com.popiin.util.Common
import com.popiin.util.Parameters
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FavouriteEventDetailsFragment : BaseFragment<FragmentFavouriteEventDetailsBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_favourite_event_details
    }

    companion object {
        var eventItem: FavouriteEventsRes.FavouriteEvent? = null
        fun newInstance(item: FavouriteEventsRes.FavouriteEvent?): FavouriteEventDetailsFragment {
            eventItem = item
            return FavouriteEventDetailsFragment()
        }
    }
    var favorite:FavoriteListener?=null

    override fun initViews() {
        binding.model = eventItem
        binding.common = common
        binding.isFavorite=1
        binding.username =PrefManager.getName()

        common.loadImageFromUrl(binding.ivEvent, eventItem!!.images?.get(0)!!.image)

        if (eventItem!!.event!!.has_ticket == 1) {
            val sortedTickets = eventItem!!.tickets!!.sortedBy { it.price }
            if (sortedTickets[0].price == 0.0) {
                binding.tvPrice.text = getString(R.string.txt_free)
            } else {
                binding.tvPrice.text =
                    Common.instance!!.currencySymbol + Common.instance!!.getDecimalFormatValue(
                        sortedTickets[0].price
                    )
            }
        } else {
            binding.tvPrice.text = getString(R.string.txt_sold_out)
            binding.tvPrice.visibility=View.GONE
        }

        binding.tvAddToCalendar.setOnClickListener {
            showCommonInfoBottomFragment(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_sure_delete),
                getString(
                    R.string.txt_add_to_calender
                ),
                getString(R.string.txt_calender_add_desc),
                getString(R.string.txt_no),
                getString(R.string.txt_yes_add),
                infoDialogListener
            )
        }

        if (eventItem!!.event!!.music != null && eventItem!!.event!!.music!!.isNotEmpty()) {
            binding.inclEventMusic.root.visibility = View.VISIBLE
            val strArray: String?
            strArray = eventItem!!.event!!.music!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            binding.inclEventMusic.desc = strArray
        } else {
            binding.inclEventMusic.root.visibility = View.GONE
        }

        if (eventItem!!.event!!.entertainment != null && eventItem!!.event!!.entertainment!!.isNotEmpty()) {
            binding.inclEventEntertainment.root.visibility = View.VISIBLE
            val strArray: String?
            strArray = eventItem!!.event!!.entertainment!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            binding.inclEventEntertainment.desc = strArray
        } else {
            binding.inclEventEntertainment.root.visibility = View.GONE
        }

        if (eventItem!!.event!!.category != null && eventItem!!.event!!.category!!.isNotEmpty()) {
            binding.inclEventTags.root.visibility = View.VISIBLE
            val strArray: String?
            strArray = eventItem!!.event!!.category!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                    ", ")
                .replace(Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
            binding.inclEventTags.desc = strArray
        } else {
            binding.inclEventTags.root.visibility = View.GONE
        }

        if (eventItem!!.event!!.other_information != null && eventItem!!.event!!.other_information!!.isNotEmpty()) {
            binding.inclEventOtherInfo.root.visibility = View.VISIBLE
            binding.inclEventOtherInfo.desc = eventItem!!.event!!.other_information
        } else {
            binding.inclEventOtherInfo.root.visibility = View.GONE
        }

        if (eventItem!!.event!!.entry_policy != null && eventItem!!.event!!.entry_policy!!.isNotEmpty()) {
            binding.inclEventEntryPolicy.root.visibility = View.VISIBLE
            binding.inclEventLastEntry.root.visibility = View.VISIBLE
            binding.inclEventEntryPolicy.desc = eventItem!!.event!!.entry_policy
            binding.inclEventLastEntry.desc = eventItem!!.event!!.entry_policy
        } else {
            binding.inclEventEntryPolicy.root.visibility = View.GONE
            binding.inclEventLastEntry.root.visibility = View.GONE
        }

        binding.topHeader.ivShare.setOnClickListener {
            val link =
                if (eventItem!!.event!!.share_link == null) "" else eventItem!!.event!!.share_link
            val shareBody = """
                Event name: ${eventItem!!.event!!.name.toString()}
                $link """.trimIndent()
            shareMessage(shareBody)
        }

        binding.tvInviteFriends.setOnClickListener {
          //  shareMessage(Constant().shareLink + "\n" + getString(R.string.txt_app_download))
            binding.ivShare.callOnClick()
        }

        binding.ivShare.setOnClickListener{
            eventItem!!.event!!.share_link
            if(eventItem!!.event!!.share_link!=null && eventItem!!.event!!.share_link!!.isNotEmpty()){
                shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = eventItem!!.event!!.name, url =eventItem!!.event!!.share_link!! )
            }else {
                val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().eventId, "" + eventItem!!.event_id)
                shareBranchIOLink(properties, object : BranchIOListener() {
                    override fun onLinkCreate(url: String?) {
                        super.onLinkCreate(url)
                        shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = eventItem!!.event!!.name, url =url!! )
                        callPostUpdateLinkApi(eventItem!!.id, url)
                    }
                })
            }
        }

        binding.cbLike.setOnClickListener {
            if (binding.isFavorite == 0) {
                binding.isFavorite=1
                setFavoriteData(eventItem!!.event_id)
                callEventFavourite(eventItem!!.event_id.toString(), 1)
            } else {
                binding.isFavorite=0
                setFavoriteData(eventItem!!.event_id)
                callEventFavourite(eventItem!!.event_id.toString(), 0)
            }
        }


        binding.topHeader.ivBack.setOnClickListener {
            favorite?.onStatusUpdates(eventItem!!.event_id.toInt(),binding.isFavorite, SHARE_MESSAGE_TYPE.EVENT)
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.tvGetDirections.setOnClickListener {
            if (!common.isAllowClick) {
                return@setOnClickListener
            }
            val directionDialog = DirectionsDialog(
                mActivity!!,
                LatLng(PrefManager.lastLocation!!.latitude, PrefManager.lastLocation!!.longitude),
                LatLng(eventItem!!.event!!.lat, eventItem!!.event!!.longi)
            )
            directionDialog.show()
        }

        binding.tvViewBookingCode.setOnClickListener {

            setFragmentWithStack(
                BookNowFragment.newInstance(null, eventItem!!),
                BookNowFragment::class.java.simpleName
            )
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
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
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    fun setFavoriteData(eventId: Int) {
        val userId: Int = PrefManager.getUser().user!!.id
        if (realmController!!.isFavoritesEvents(userId, eventId) == 1) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(EventFavorites::class.java).equalTo("id",eventId)
                        .findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavoritesEvents(userId, eventId) == 0) {
            realm!!.executeTransaction { realm ->
                val rl: EventFavorites? =
                    realm.where(EventFavorites::class.java).equalTo("id", eventId).findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = EventFavorites(PrefManager.getUser().user!!.id, eventId, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
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
                eventItem!!.event!!.start_date_time,
                eventItem!!.event!!.end_date_time,
                eventItem!!.event!!.name,
                eventItem!!.event!!.description
            )

        }

    }
}