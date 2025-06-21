package com.popiin.bottomDialogFragment

import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentAddStoryCaptionBinding
import com.popiin.listener.AddSpecialRequiremnetListener

class AddStoryCaptionBottomFragment(
    var title: String = "",
    var desc: String = "",
    var hint: String = "",
    private var captionListener: AddSpecialRequiremnetListener<String>,
) : BaseBottomSheetDialog<BottomFragmentAddStoryCaptionBinding>() {
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_add_story_caption
    }

    override fun initViews() {
        binding!!.title = title
        binding!!.desc = desc
        binding!!.hint = hint

        binding!!.btnAdd.setOnClickListener {
            if (binding!!.edtCaption.text.toString().isEmpty()) {
                captionListener.onAddCaptionClick("")
            } else {
                captionListener.onAddCaptionClick(binding!!.edtCaption.text.toString())
            }

            dialog!!.dismiss()
        }

        binding!!.ivClose.setOnClickListener {
            if (binding!!.edtCaption.text.toString().isEmpty()) {
                captionListener.onAddCaptionClick("")
            } else {
                captionListener.onAddCaptionClick(binding!!.edtCaption.text.toString())
            }

            dialog!!.dismiss()
        }
    }
}