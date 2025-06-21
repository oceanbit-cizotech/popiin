package com.popiin.fragment

import android.view.View
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.EventAttendeesFragment
import com.popiin.activity.EventBookingAttendeesFragment
import com.popiin.annotation.CONSTANT
import com.popiin.databinding.FragmentBusinessScanCodeBinding
import com.popiin.dialog.CommonDialog
import com.popiin.req.ScanCodeEventReq
import com.popiin.req.ScanOfferCodeReq

import com.popiin.res.CommonRes
import com.popiin.res.ScannedEventVenue
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class BusinessScanCodeFragment :
    BaseFragment<FragmentBusinessScanCodeBinding>() {
    private var mCodeScanner: CodeScanner? = null
    public override fun getLayout(): Int {
        return R.layout.fragment_business_scan_code
    }

    companion object {
        var onlyEvent: Boolean = false
        fun newInstance(eventOnly: Boolean): BusinessScanCodeFragment {
            onlyEvent = eventOnly
            return BusinessScanCodeFragment()
        }
    }


    override fun initViews() {
        binding.llTopBarParent.ivBack.setOnClickListener(View.OnClickListener { mActivity!!.supportFragmentManager.popBackStack() })
        mCodeScanner = CodeScanner(requireActivity(), binding.scannerView)
        mCodeScanner!!.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                callApiToScanCodeEvent(it.text)
            }
        }

        binding.scannerView.setOnClickListener { mCodeScanner!!.startPreview() }
    }

    private fun callApiToScanCodeEvent(qrDetail: String) {
        val code: Array<String> = qrDetail.replace(CONSTANT.SEPRATEOR, "-").split("-".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val codeType = code[0]
        val id = code[1]
        val bookingId = code[2]
        if (onlyEvent && codeType.equals("3", ignoreCase = true)) {
            common.openDialog(requireActivity(), getString(R.string.txt_use_scan_code_menu))
            return
        }
        mActivity!!.runOnUiThread { showProgress() }
        val call: Call<CommonRes?>
        if (codeType.equals("1", ignoreCase = true)) {
            call = apiInterface.postScanCodeEvent(
                PrefManager.getAccessToken(),
                ScanCodeEventReq(id, bookingId)
            )!!
        } else if (codeType.equals("3", ignoreCase = true)) {
            call = apiInterface.doScanOfferCode(
                PrefManager.getAccessToken(),
                ScanOfferCodeReq(id, "" + PrefManager.getUser()!!.user!!.id)
            )!!
        } else {
            call = apiInterface.doScanOfferCode(
                PrefManager.getAccessToken(),
                ScanOfferCodeReq(id, "" + PrefManager.getUser()!!.user!!.id)
            )!!
        }
        call.enqueue(object : Callback<CommonRes?> {
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                hideProgress()
                if (isValidResponse(response)) {
                    if (response.body()!!.status) {
                        val commonDialog = CommonDialog(
                            requireActivity(),
                            getString(R.string.lbl_ok), "", "", response.body()!!.msg!!
                        )
                        commonDialog.show()
                        commonDialog.binding.btnPositive.setOnClickListener {
                            commonDialog.dismiss()
                            if (onlyEvent) {
                                backPressedWithFragment(EventBookingAttendeesFragment::class.java.simpleName)
                            } else {
                                backPressedWithFragment(BusinessScanCodeFragment::class.java.simpleName)
                                setFragmentAdd(
                                    EventAttendeesFragment.newInstance(),
                                    EventAttendeesFragment::class.java.simpleName
                                )
                            }
                        }
//                        CommonDialogBuilder(
//                            "",
//                            response.body()!!.msg,
//                            activity!!.resources.getString(R.string.lbl_ok)
//                        )
//                            .setListner(object : CommonDialogListner() {
//                                fun onPositiveButtonClick(item: Any?) {
//                                    super.onPositiveButtonClick(item)
//                                    setFragmentWithStack(
//                                        BookingAttendeesFragment.newInstance(),
//                                        BookingAttendeesFragment::class.java.getSimpleName()
//                                    )
//                                }
//                            })
//                            .show(mActivity)
                        PrefManager.setScannedEventVenue(
                            ScannedEventVenue(
                                id,
                                codeType,
                                bookingId,
                                Calendar.getInstance().timeInMillis
                            )
                        )
                    } else {
                        common.openDialog(requireActivity(), response.body()!!.msg)
                    }
                }
            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner!!.startPreview()
    }

    override fun onPause() {
        mCodeScanner!!.releaseResources()
        super.onPause()
    }
}