package com.popiin.adapter

import android.os.Build
import android.text.Html
import com.popiin.BaseRVAdapter
import com.popiin.BaseViewHolder
import com.popiin.R
import com.popiin.databinding.AdapterOnboardingBinding


class OnBoardingAdapter : BaseRVAdapter<AdapterOnboardingBinding>() {
    override fun getLayout(): Int {
        return R.layout.adapter_onboarding
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterOnboardingBinding>, position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.tvOnbaordingTitle.text =
                Html.fromHtml(holder.itemView.context.getString(R.string.txt_onboarding_title),
                    Html.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}