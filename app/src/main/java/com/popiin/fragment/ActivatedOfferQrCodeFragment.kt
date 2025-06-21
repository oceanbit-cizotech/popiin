package com.popiin.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.FragmentActivatedOfferQrCodeBinding
import com.popiin.res.VenueListRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.popiin.util.QrGenerator
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ActivatedOfferQrCodeFragment : BaseFragment<FragmentActivatedOfferQrCodeBinding>() {

    override fun getLayout(): Int {
       return R.layout.fragment_activated_offer_qr_code
    }

    companion object{
        private  var activatedOffer: VenueListRes.Offerslive? = null
        fun newInstance(item:VenueListRes.Offerslive?):ActivatedOfferQrCodeFragment{
            activatedOffer=item
            return ActivatedOfferQrCodeFragment()
        }
    }
    @SuppressLint("NewApi")
    override fun initViews() {
        binding.tvOfferName.text= activatedOffer?.title

        val qrCode: Bitmap?
        try {
            qrCode = QrGenerator.Builder()
                .content(Constant().three + CONSTANT.SEPRATEOR + activatedOffer?.id + CONSTANT.SEPRATEOR + PrefManager.getUser()?.user?.id)
                .qrSize(700)
                .margin(1)
                .color(
                   Color.BLACK
                )
                .bgColor(Color.TRANSPARENT)
                .ecc(ErrorCorrectionLevel.H) //                    .overlay(mActivity, R.drawable.icn_loader1)
                //                    .overlaySize(100)
                //                    .overlayAlpha(255)
                .encode()
            binding.ivQrCode.setImageBitmap(qrCode)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val currentTime = LocalTime.now()
        val endTimeString = activatedOffer?.close_time // Replace this with your actual end time string (e.g., "17:30:00")

        // Parse the end time string into a LocalTime object using the HH:mm:ss format
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val endTime = LocalTime.parse(endTimeString, formatter)

        // Calculate the difference between current time and end time
        val duration = Duration.between(currentTime, endTime)

        // If the duration is negative, it means the end time has passed
        if (duration.isNegative) {
            println("Offer ended")
        } else {
            val remainingHours = duration.toHours()
            val remainingMinutes = (duration.toMinutes() % 60)
            var strHours:String="hour"
            var strMinutes:String="minute"
            if(remainingHours>1){
                strHours="hours"
            }

            if(remainingMinutes>1){
                strMinutes="minutes"
            }
            if (remainingHours > 0) {
                binding.tvOfferTime.text="offer ends in "+remainingHours+" "+strHours+" "+remainingMinutes+" "+strMinutes
            } else {
                binding.tvOfferTime.text="offer ends in "+remainingMinutes+" "+strMinutes
            }
        }

    }
}