package com.popiin.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.WhatsOnVenuesAdapter
import com.popiin.bottomDialogFragment.SelectVenueBottomFragment
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.databinding.FragmentWhatsOnBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.req.EventReq
import com.popiin.req.VenueDetailsReq
import com.popiin.req.VenueWhatsDeleteReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.res.VenueWhatsListRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WhatsOnFragment : BaseFragment<FragmentWhatsOnBinding>() {
    var venues = ArrayList<VenueListRes.Venue>()
    var venueId = ""
    var selectedVenues = 0
    var whatsOnVenuesAdapter: WhatsOnVenuesAdapter? = null
    var whatsOnVenues = ArrayList<VenueWhatsListRes.Data>()
    var calendars: ArrayList<Calendar> = ArrayList()
    lateinit var format: SimpleDateFormat

    companion object {
        fun newInstance(): WhatsOnFragment {
            return WhatsOnFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_whats_on
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {
        true.callMyVenuesApi()
        whatsOnVenuesAdapter = WhatsOnVenuesAdapter(list = whatsOnVenues, whatsOnListener)
        binding.rvWhatsOn.layoutManager = LinearLayoutManager(mActivity)
        binding.rvWhatsOn.adapter = whatsOnVenuesAdapter

        binding.inclVenueType.edtText.setHintTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorHeaderText
            )
        )

        format = SimpleDateFormat(Constant().ddMmmYyyy)

        binding.inclVenueType.edtText.setOnClickListener {
            showSelectVenueBottomFragment()
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }


        val calender: Calendar = Calendar.getInstance()
        calender.add(Calendar.DAY_OF_YEAR, -1)
        binding.calendarView.setMinimumDate(calender)

        binding.calendarView.setPreviousButtonImage(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_previous
            )!!
        )
        binding.calendarView.setForwardButtonImage(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_next
            )!!
        )

        binding.calendarView.setOnCalendarDayClickListener(object: OnCalendarDayClickListener {
            override fun onClick(calendarDay: com.applandeo.materialcalendarview.CalendarDay) {
                val date = format.format(calendarDay.calendar.time)
                if (calendarDay.calendar.before(calender)) {
                    return
                }

                if (calendarDay.calendar.after(calender)) {
                    common.refreshWhatsOnModel()
                    setFragmentWithStack(
                        CreateWhatsOnFragment.newInstance(
                            null,
                            date,
                            "" + venues[selectedVenue].id
                        ), CreateWhatsOnFragment::class.java.simpleName
                    )
                }
            }


        })

    }


    var selectedVenue: Int = 0
    var listener: AdapterMyVenuesListener<VenueListRes.Venue> =
        object : AdapterMyVenuesListener<VenueListRes.Venue>() {
            override fun onEventSelected(item: VenueListRes.Venue, position: Int) {
                super.onEventSelected(item, position)
                venues[position]
                selectedVenue = position
                venues[position].isSelected = true
                binding.selectedVenue = venues[position].venue_name
                venueId = "" + venues[position].id
                hitApiWhatsOnListFromVenue(venueId)
            }
        }

    private fun Boolean.callMyVenuesApi() {
        if (isInternetConnect()) {
            if (this) {
                showProgress()
            }
            val call: Call<VenueListRes?>? =
                apiInterface.doGetVenuesList(
                    PrefManager.getAccessToken(),
                    EventReq(100, "", 0)
                )
            call!!.enqueue(object : Callback<VenueListRes?> {

                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            venues.clear()
                            venues.addAll(response.body()!!.venues)
                            venues[selectedVenues].isSelected = true
                            venueId = "" + venues[selectedVenues].id
                            hitApiWhatsOnListFromVenue(venueId)
                            binding.selectedVenue = venues[selectedVenues].venue_name
                        }

                        if (venues.size == 0) {
                            binding.tvNoWhatsOn.visibility = View.VISIBLE
                        } else {
                            binding.tvNoWhatsOn.visibility = View.GONE
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    @Suppress("SameParameterValue")
    private fun hitApiWhatsOnListFromVenue(venuesId: String) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<VenueWhatsListRes> = apiInterface.getWhatsOnListFromVenue(
                PrefManager.getAccessToken(),
                VenueDetailsReq(venuesId)
            )
            call.enqueue(object : Callback<VenueWhatsListRes> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueWhatsListRes>,
                    response: Response<VenueWhatsListRes>,
                ) {
                    if (isValidResponse(response)) {
                        if (whatsOnVenues.size > 0) {
                            whatsOnVenues.clear()
                        }
                        if (response.body()!!.success) {
                            whatsOnVenues.addAll(response.body()!!.data)
                            whatsOnVenuesAdapter!!.notifyDataSetChanged()
                        }
                    }
                    if (calendars.size > 0) {
                        calendars.clear()
                    }
                    for (i in whatsOnVenues.indices) {
                        getDates(
                            whatsOnVenues[i].what_datetime!!,
                            whatsOnVenues[i].end_time!!
                        )
                    }
                    if (whatsOnVenues.size > 0) {
                        binding.rvWhatsOn.visibility = View.VISIBLE
                        //  binding.tvErrorMessage.setVisibility(View.GONE)
                    } else {
                        binding.rvWhatsOn.visibility = View.GONE
                        //  binding.tvErrorMessage.setVisibility(View.VISIBLE)
                    }

                    Handler(Looper.myLooper()!!).postDelayed({
                        binding.calendarView.selectedDates = calendars
                    }, 50)
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueWhatsListRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun showSelectVenueBottomFragment() {
        val selectVenueBottomFragment = SelectVenueBottomFragment(venues, listener, selectedVenue)
        selectVenueBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectVenueBottomFragment.show(it, "") }
    }

    private var whatsOnListener: AdapterItemClickListener<VenueWhatsListRes.Data> =
        object : AdapterItemClickListener<VenueWhatsListRes.Data>() {
            override fun onAdapterEditClick(item: VenueWhatsListRes.Data, position: Int) {
                super.onAdapterEditClick(item, position)
                setFragmentWithStack(
                    CreateWhatsOnFragment.newInstance(
                        item,
                        null,
                        "" + item.venue_id
                    ), CreateWhatsOnFragment::class.java.simpleName
                )
            }

            override fun onEventDeleteClick(item: VenueWhatsListRes.Data, position: Int) {
                super.onEventDeleteClick(item, position)
                openDeleteWhatsOnDialog(
                    "" + item.id,
                    position,
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_delete)
                )
            }
        }

    fun openDeleteWhatsOnDialog(id: String, position: Int, title: String, message: String?) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )

        binding.btnNo.visibility = View.VISIBLE
        binding.ivClose.visibility = View.VISIBLE
        binding.btnNo.text = getString(R.string.txt_no)
        binding.btnDone.text = getString(R.string.txt_yes_delete)
        binding.ivPassSuccess.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_sure_delete
            )
        )

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
            callWhatsOnDelete(id, position)
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun callWhatsOnDelete(ids: String, position: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call = apiInterface.getVenuewhatsDelete(
                PrefManager.getAccessToken(),
                VenueWhatsDeleteReq(ids)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        whatsOnVenues.removeAt(position)
                        if (calendars.size > 0) {
                            calendars.clear()
                        }
                        for (i in whatsOnVenues.indices) {
                            getDates(whatsOnVenues[i].what_datetime!!, whatsOnVenues[i].end_time!!)
                        }
                        whatsOnVenuesAdapter!!.notifyDataSetChanged()
                        hitApiWhatsOnListFromVenue(venueId)
                    }
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDates(dateString1: String, dateString2: String) {
        val dates = ArrayList<Date>()
        val df1: DateFormat = SimpleDateFormat(Constant().yyyyMmDd)
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df1.parse(dateString1)
            date2 = df1.parse(dateString2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal1 = Calendar.getInstance()
        cal1.time = date1!!
        val cal2 = Calendar.getInstance()
        cal2.time = date2!!
        while (!cal1.after(cal2)) {
            dates.add(cal1.time)
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = cal1.time
            calendars.add(calendar)
            cal1.add(Calendar.DATE, 1)
        }
    }
}