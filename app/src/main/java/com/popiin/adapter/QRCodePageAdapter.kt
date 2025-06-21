package com.popiin.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.AdapterQrCodePageBinding
import com.popiin.res.Scancode
import com.popiin.util.Common
import com.popiin.util.PrefManager
import com.popiin.util.QrGenerator
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel


class QRCodePageAdapter(
    var list: List<Scancode>,
    var eventId: String,
    var eventName: String,
    var startTime: String,
    var endTime: String,
    var price: Double,
    var codeType: String,
    var ticketType: String,
) :

    BaseRVAdapter<AdapterQrCodePageBinding>() {
    var common: Common = Common.instance!!
    override fun getLayout(): Int {
        return R.layout.adapter_qr_code_page
    }
    var qrCode: Bitmap? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BaseViewHolder<AdapterQrCodePageBinding>,
        position: Int
    ) {

        generateQrCode(holder, position)
        val userName: String =PrefManager.getName()
        if (position > 0) {
            holder.binding.tvUserName.text = "$userName X ${position + 1}"
        } else {
            holder.binding.tvUserName.text = userName
        }

        holder.binding.tvBookingType.text = ticketType
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun generateQrCode(holder: BaseViewHolder<AdapterQrCodePageBinding>, position: Int) {
        try {
            if(common.isCurrentDateBeforeStartDate(startDateStr = startTime)){
                qrCode = QrGenerator.Builder()
                    .content("Disabled")
                    .qrSize(700)
                    .margin(1)
                    .color(
                        if (list[position].is_scanned == 1) ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.colorBlue
                        ) else Color.GRAY
                    )
                    .bgColor(Color.TRANSPARENT)
                    .ecc(ErrorCorrectionLevel.H)
                    .encode()
                holder.binding.ivQrCode.setImageBitmap(qrCode)
                holder.binding.ivQrCode.alpha = 0.3f

            }else{
                qrCode = QrGenerator.Builder()
                    .content(codeType + CONSTANT.SEPRATEOR + eventId + CONSTANT.SEPRATEOR + list[position].id + CONSTANT.SEPRATEOR + eventName)
                    .qrSize(700)
                    .margin(1)
                    .color(
                        if (list[position].is_scanned == 1) ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.colorBlue
                        ) else Color.BLACK
                    )
                    .bgColor(Color.TRANSPARENT)
                    .ecc(ErrorCorrectionLevel.H)
                    .encode()
                holder.binding.ivQrCode.setImageBitmap(qrCode)
            }

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}