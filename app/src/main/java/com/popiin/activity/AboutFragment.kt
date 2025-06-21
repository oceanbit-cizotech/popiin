package com.popiin.activity

import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.databinding.FragmentAboutBinding

class AboutFragment : BaseFragment<FragmentAboutBinding>() {
    override fun getLayout(): Int {
        return R.layout.fragment_about
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    override fun initViews() {
        binding.inclTermsUse.root.setOnClickListener {
            setFragmentWithStack(
                TermsOfServiceFragment.newInstance(),
                TermsOfServiceFragment::class.java.simpleName
            )
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclPrivacyPolicy.root.setOnClickListener {
            setFragmentWithStack(
                PrivacyPolicyFragment.newInstance(),
                PrivacyPolicyFragment::class.java.simpleName
            )
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclHelp.root.setOnClickListener {
            setFragmentWithStack(HelpFragment.newInstance(), HelpFragment::class.java.simpleName)
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclPartnership.root.setOnClickListener {
            setFragmentWithStack(
                PartnershipFragment.newInstance(),
                PartnershipFragment::class.java.simpleName
            )
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}