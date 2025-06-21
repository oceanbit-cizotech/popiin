package com.popiin.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.QRCodePageAdapter
import com.popiin.databinding.FragmentEventQrCodeBinding
import com.popiin.fragment.WhatsOnQRCodeFragment.Companion.myWhatsOnItem
import com.popiin.res.MyBookingRes
import com.popiin.res.Scancode
import com.popiin.res.VenueAttendeesRes
import com.popiin.util.Constant


class EventQRCodeFragment : BaseFragment<FragmentEventQrCodeBinding>() {
    private var eventId: String = ""
    private var eventName: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    private lateinit var scancodes: ArrayList<Scancode>
    private var price: Double = 0.0
    private var codeType: String = ""
    private var ticketType: String = ""
    private var bookingTime: String = ""
    private lateinit var qrCodePageAdapter: QRCodePageAdapter
    private var isQrCodeEnabled = false
    override fun getLayout(): Int {
        return R.layout.fragment_event_qr_code
    }

    companion object {
        var eventItem: MyBookingRes.Data? = null
        var venueItem: VenueAttendeesRes.Data? = null
        fun newInstance(
            event: MyBookingRes.Data?,
            venue: VenueAttendeesRes.Data?
        ): EventQRCodeFragment {
            eventItem = event
            venueItem = venue
            return EventQRCodeFragment()
        }
    }

    var refNumber:String?=null
    override fun initViews() {
        if (eventItem != null) {
            binding.model = eventItem
            refNumber= eventItem!!.ref_number
        } else {
            binding.venueModel = venueItem
            refNumber= venueItem!!.ref_number

        }

        binding.common = common

        if (eventItem != null) {
            scancodes = eventItem!!.scancode!!
            eventId = "" + eventItem!!.event_id
            eventName = eventItem!!.event!!.name!!
            startTime = eventItem!!.event!!.start_date_time!!
            endTime = eventItem!!.event!!.end_date_time!!
            price = eventItem!!.price
            codeType = Constant().one
            ticketType = eventItem!!.booking_type!!
            bookingTime = eventItem!!.event!!.start_date_time!!
            common.loadImageFromUrl(binding.ivEvent, eventItem!!.images!![0].image)

            isQrCodeEnabled

        } else {
            scancodes = venueItem!!.scancode!!
            eventId = "" + venueItem!!.venue_id
            eventName = venueItem!!.venue!!.venue_name!!
            startTime = venueItem!!.booking_datetime!!
            endTime = venueItem!!.end_datetime!!
            price = venueItem!!.price
            codeType = Constant().one
            ticketType = venueItem!!.ticket!!.name!!
            bookingTime = venueItem!!.booking_datetime!!
            common.loadImageFromUrl(binding.ivEvent, venueItem!!.venue!!.venue_banner_image)
        }

        qrCodePageAdapter = QRCodePageAdapter(
            scancodes,
            eventId,
            eventName,
            startTime,
            endTime,
            price,
            codeType,
            ticketType
        )
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(8))
        binding.qrViewPager.setPageTransformer(compositePageTransformer)

        binding.qrViewPager.adapter = qrCodePageAdapter
        binding.qrViewPager.clipToPadding = false
        binding.qrViewPager.clipChildren = false
        binding.qrViewPager.offscreenPageLimit = 3
        binding.qrViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER


        binding.llCopyCode.setOnClickListener {
            common.copyQrCode(binding.llCopyCode.context,refNumber!!)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.ivShare.setOnClickListener {
            if (eventItem != null) {
                val link =
                    if (eventItem!!.event!!.share_link == null) "" else eventItem!!.event!!.share_link
                val shareBody = """
                ${Constant().eventNameHeader} ${eventItem!!.event!!.name.toString()}
                ${Constant().venue} ${eventItem!!.event!!.venue.toString()}
                $link ${getString(R.string.txt_app_download)}""".trimIndent()
                shareMessage(shareBody)
            }

        }

        binding.llDownloadImage.setOnClickListener {
            common.saveBitmapToGallery( binding.llDownloadImage.context,qrCodePageAdapter.qrCode!!)
            showToast("QR code saved to your photos.")
        }
    }

}