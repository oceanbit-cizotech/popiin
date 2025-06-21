package com.popiin.bottomDialogFragment

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmnetCommonInfoBinding
import com.popiin.listener.InfoDialogListener

class CommonInfoBottomFragment(
    val image: Drawable? = null,
    val message: String? = null,
    var desc: String? = null,
    private val negativeText: String? = null,
    val positiveText: String? = null,
    private val messageColor: Int = 0,
    private val messageDescColor: Int = 0,
    private val isPayment: Boolean,
    val listener: InfoDialogListener,
) : BaseBottomSheetDialog<BottomFragmnetCommonInfoBinding>() {
    var bottomSheetDialog = this
    override fun getLayout(): Int {
        return R.layout.bottom_fragmnet_common_info
    }

    override fun initViews() {

        if (image == null) {
            binding!!.ivInfo.visibility = View.GONE
        }

        binding!!.ivInfo.setImageDrawable(image)
        binding!!.tvMessage.text = message
        binding!!.tvMessage.setTextColor(messageColor)
        if (negativeText != null && negativeText.isNotEmpty()) {
            binding!!.btnNo.visibility = View.VISIBLE
            binding!!.ivClose.visibility = View.VISIBLE
            binding!!.btnNo.text = negativeText

        }

        if (desc != null && desc!!.isNotEmpty()) {
            binding!!.tvMessageDesc.visibility = View.VISIBLE
            binding!!.tvMessageDesc.text = desc
            binding!!.tvMessageDesc.setTextColor(messageDescColor)
        } else {
            binding!!.tvMessageDesc.visibility = View.GONE
        }

        if (isPayment) {
            binding!!.ivClose.visibility = View.GONE
            binding!!.tvMessage.textSize = 20f
            binding!!.tvMessageDesc.textSize = 16f
            binding!!.tvMessage.typeface = ResourcesCompat.getFont(requireActivity(), R.font.inter_semi_bold)
            binding!!.tvMessageDesc.typeface = ResourcesCompat.getFont(requireActivity(), R.font.inter_regular)
        } else {
        //    binding!!.ivClose.visibility = View.VISIBLE
            binding!!.tvMessage.typeface = ResourcesCompat.getFont(requireActivity(), R.font.inter_medium)
            binding!!.tvMessageDesc.typeface = ResourcesCompat.getFont(requireActivity(), R.font.inter_bold)
            binding!!.tvMessage.textSize = 20f
            binding!!.tvMessageDesc.textSize = 20f
        }

        binding!!.btnDone.text = positiveText!!

        binding!!.btnDone.setOnClickListener {
            dismiss()
            listener.onInfoPositiveClick(bottomSheetDialog)
        }

        binding!!.btnNo.setOnClickListener {
            dismiss()
            listener.onInfoNegativeClick(bottomSheetDialog)
        }

        binding!!.ivClose.setOnClickListener {
            dismiss()
            listener.onInfoCloseClick(bottomSheetDialog)
        }

    }
}