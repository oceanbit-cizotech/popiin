package com.popiin.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.SplashActivity
import com.popiin.activity.*
import com.popiin.databinding.DialogReferAFriendBinding
import com.popiin.databinding.FragmentSettingsBinding

import com.popiin.util.Constant
import com.popiin.util.PrefManager

import java.io.IOException
import java.util.*


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_settings
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() {

        binding.tvProfileName.text=PrefManager.getName()
        common.loadImageFromUrl(binding.ivProfile,PrefManager.getUser()!!.user!!.profilePic)

        val addresses: List<Address>?
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())

        try {
            @Suppress("DEPRECATION")
            addresses = geocoder.getFromLocation(PrefManager.lastLocation!!.latitude,
                PrefManager.lastLocation!!.longitude,
                1
            )
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val state: String = if (addresses!![0].locality != null) {
                if (addresses[0].locality.equals(
                        Constant().gujarat,
                        true
                    )
                ) Constant().gj else addresses[0].locality
            } else {
                if (addresses[0].subAdminArea.equals(
                        Constant().gujarat,
                        true
                    )
                ) Constant().gj else addresses[0].subAdminArea
            }

            val country: String = addresses[0].countryName
            binding.tvAddress.text = "$state, $country"
        } catch (e: IOException) {
            e.printStackTrace()
        }

        binding.tvEditProfile.setOnClickListener {
            startActivity(Intent(requireActivity(),EditProfileActivity::class.java))
            mActivity!!.overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }

        binding.inclProfile.root.setOnClickListener {
            startActivity(Intent(requireActivity(),EditProfileActivity::class.java))
            mActivity!!.overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }


        binding.inclGetSupport.root.setOnClickListener {
            setFragmentWithStack(HelpFragment.newInstance(), HelpFragment::class.java.simpleName)
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        binding.inclBusiness.root.setOnClickListener {
            setFragmentAdd(
                BusinessFragment.newInstance(),
                BusinessFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclMyBookings.root.setOnClickListener {
            setFragmentWithStack(
                MyBookingsFragment.newInstance(0),
                MyBookingsFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclMyWhatsonBookings.root.setOnClickListener {
            setFragmentWithStack(
                MyWhatsonBookingFragment.newInstance(),
                MyWhatsonBookingFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclNotification.root.setOnClickListener {
            setFragmentWithStack(
                NotificationFragment.newInstance(),
                NotificationFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclReferFriend.root.setOnClickListener {
            openReferFriendDialog()
        }

        binding.inclAbout.root.setOnClickListener {
            setFragmentWithStack(AboutFragment.newInstance(), AboutFragment::class.java.simpleName)
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.inclSignOut.root.setOnClickListener {
            PrefManager.clearPreferences()
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }

    private fun openReferFriendDialog() {
        val dialog = PopupWindow(mActivity)
        val binding: DialogReferAFriendBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity), R.layout.dialog_refer_a_friend, null, false)

        binding.tvReferLink.text = Constant().shareLink

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnShare.setOnClickListener {
            shareMessage(getString(R.string.txt_app_download)+"\n"+Constant().shareLink)
        }

        binding.ivCopyCode.setOnClickListener {
            copyCode(requireActivity(),binding.tvReferLink.text.toString(),getString(R.string.txt_code_copied))
        }

        dialog.animationStyle = R.style.animation
        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    override fun onResume() {
        super.onResume()
        if(isInternetConnect()) {
            common.loadImageFromUrl(binding.ivProfile, PrefManager.getUser().user?.profilePic)
            if (PrefManager.getUser().user!!.unreadCount > 0) {
                binding.inclNotification.ivNotification.visibility = View.VISIBLE
            } else {
                binding.inclNotification.ivNotification.visibility = View.GONE
            }
        }
    }
}