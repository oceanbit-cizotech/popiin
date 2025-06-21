package com.popiin.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.QRCodePageAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.FragmentWhatsOnQrCodeBinding
import com.popiin.res.MyWhatsonBookRes
import com.popiin.res.Scancode
import com.popiin.util.Constant
import com.popiin.util.QrGenerator
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class WhatsOnQRCodeFragment : BaseFragment<FragmentWhatsOnQrCodeBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_whats_on_qr_code
    }
    companion object {
        var myWhatsOnItem: MyWhatsonBookRes.Data? = null
        fun newInstance(item: MyWhatsonBookRes.Data): WhatsOnQRCodeFragment {
            myWhatsOnItem = item
            return WhatsOnQRCodeFragment()
        }
    }

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


    override fun initViews() {
        if (myWhatsOnItem != null) {
            binding.model = myWhatsOnItem!!.whatson
            binding.venueModel = myWhatsOnItem!!.venue
            binding.dates = myWhatsOnItem!!.whatson!!.end_time
        }

        binding.common = common

        if (myWhatsOnItem != null) {
            scancodes = myWhatsOnItem!!.scancode!!
            eventId = "" + myWhatsOnItem!!.id
            eventName = ""
            startTime = ""
            endTime = ""
            price = 0.0
            codeType = Constant().four
            ticketType = ""
            bookingTime = ""
            common.loadImageFromUrl(binding.ivEvent, myWhatsOnItem!!.whatson!!.whatsonimages[0].imageUrl)
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
            if (myWhatsOnItem != null) {
                common.copyQrCode(binding.llCopyCode.context,myWhatsOnItem!!.ref_number!!)
            }
        }


        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

       binding.ivShare.setOnClickListener {
          /*  if (eventItem != null) {
                val link =
                    if (eventItem!!.event!!.share_link == null) "" else eventItem!!.event!!.share_link
                val shareBody = """
                ${Constant().eventNameHeader} ${eventItem!!.event!!.name.toString()}
                ${Constant().venue} ${eventItem!!.event!!.venue.toString()}
                $link ${getString(R.string.txt_app_download)}""".trimIndent()
                shareMessage(shareBody)
            }*/

        }
        binding.llDownloadImage.setOnClickListener{
            common.saveBitmapToGallery(binding.llDownloadImage.context,qrCodePageAdapter.qrCode!!)
            showToast("QR code saved to your photos.")
        }
    }

}