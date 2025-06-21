package com.popiin.activity

import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.databinding.ActivityPrivacyBinding

class PrivacyActivity : BaseActivity<ActivityPrivacyBinding>() {
    override fun getLayout(): Int {
        return R.layout.activity_privacy
    }

    override fun initViews() {
        binding.tvPolicy.tvHeader.text = resources.getString(R.string.txt_privacy_policy)
        binding.tvPolicy1.tvDescription.text = resources.getString(R.string.privacy_policy)
        binding.tvPolicyInformation.tvHeader.text = resources.getString(R.string.lbl_collect_info)
        binding.tvPolicyInformation1.tvDescription.text =
            resources.getString(R.string.collect_info1)
        binding.tvUseInformation.tvHeader.text = resources.getString(R.string.lbl_use_info)
        binding.tvUseInformation1.tvDescription.text = resources.getString(R.string.use_info1)
        binding.tvShareInfo.tvHeader.text = resources.getString(R.string.lbl_sharing)
        binding.tvShareInfo1.tvDescription.text = resources.getString(R.string.sharing_info1)
        binding.tvExercise.tvHeader.text = resources.getString(R.string.lbl_exercise)
        binding.tvExercise1.tvDescription.text = resources.getString(R.string.exercise1)
        binding.tvAccountDeletion.tvHeader.text = resources.getString(R.string.lbl_account_deletion)
        binding.tvAccountDeletion1.tvDescription.text = resources.getString(R.string.account_deletion1)
        binding.tvNotifyChange.tvHeader.text = resources.getString(R.string.lbl_notify_change)
        binding.tvNotifyChange1.tvDescription.text = resources.getString(R.string.notify_change1)
        binding.tvContact.tvHeader.text = resources.getString(R.string.lbl_contact)
        binding.tvContact1.tvDescription.text = resources.getString(R.string.contact1)

        binding.topHeader.ivBack.setOnClickListener {
            finish()
        }
    }

}