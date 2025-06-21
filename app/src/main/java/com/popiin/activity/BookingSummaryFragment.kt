package com.popiin.activity

import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.databinding.FragmentBookingSummaryBinding
import com.popiin.res.BookEventRes
import com.popiin.res.EventListRes
import com.popiin.res.FavouriteEventsRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import java.text.SimpleDateFormat
import java.util.*

class BookingSummaryFragment : BaseFragment<FragmentBookingSummaryBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_booking_summary
    }

    companion object {
        var event: EventListRes.Event? = null
        var favouriteEvent: FavouriteEventsRes.FavouriteEvent? = null
        var bookingData: BookEventRes.Data? = null
        fun newInstance(
            favEventItem: FavouriteEventsRes.FavouriteEvent? = null,
            eventItem: EventListRes.Event? = null,
            data: BookEventRes.Data?,
        ): BookingSummaryFragment {
            event = eventItem
            favouriteEvent = favEventItem
            bookingData = data
            return BookingSummaryFragment()
        }
    }

    override fun initViews() {
        binding.event = event
        binding.eventFav = favouriteEvent
        binding.common = common
        binding.bookingData = bookingData

        binding.email = PrefManager.getUser().user?.email

        binding.tvUserName.desc =PrefManager.getName()

        val currentTime: String = SimpleDateFormat(Constant().hhMmA, Locale.US).format(Date())
        binding.inclBookTime.desc = currentTime


        binding.btnMyBookings.setOnClickListener {
          setFragmentWithStack(
              MyBookingsFragment.newInstance(Constant().eventBooking),
              MyBookingsFragment::class.simpleName
          )
        }

        binding.topHeader.ivBack.setOnClickListener{
            mActivity!!.supportFragmentManager.popBackStack()
        }

    }

}