package com.popiin.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.MyWhatsOnBookAdapter
import com.popiin.databinding.FragmentMyWhatsonBookingBinding
import com.popiin.listener.AdapterMyBookingListener
import com.popiin.req.MyBookingReq
import com.popiin.res.MyWhatsonBookRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyWhatsonBookingFragment : BaseFragment<FragmentMyWhatsonBookingBinding>() {
    var myWhatsonBookData: ArrayList<MyWhatsonBookRes.Data> = ArrayList()
    lateinit var adapter: MyWhatsOnBookAdapter
    var pageNo = defaultPage
    override fun getLayout(): Int {
        return R.layout.fragment_my_whatson_booking
    }

    companion object {
        fun newInstance(): MyWhatsonBookingFragment {
            return MyWhatsonBookingFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        adapter = MyWhatsOnBookAdapter(myWhatsonBookData, listener)
        binding.rvEventBookAttendees.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvEventBookAttendees.adapter = adapter

        callWhatsOnBookingApi(pageNo)
    }

    var listener: AdapterMyBookingListener<MyWhatsonBookRes.Data> =
        object : AdapterMyBookingListener<MyWhatsonBookRes.Data>() {
            override fun onViewBookingCode(item: MyWhatsonBookRes.Data, position: Int) {
                super.onViewBookingCode(item, position)
                setFragmentWithStack(WhatsOnQRCodeFragment.newInstance(item),
                    WhatsOnQRCodeFragment::class.java.simpleName)
            }
        }

    private fun callWhatsOnBookingApi(pageNo: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<MyWhatsonBookRes?>? =
                apiInterface.myWhatsOnBooking(PrefManager.getAccessToken(),
                    MyBookingReq(pageNo, pageLimit))
            call!!.enqueue(object : Callback<MyWhatsonBookRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<MyWhatsonBookRes?>,
                    response: Response<MyWhatsonBookRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (this@MyWhatsonBookingFragment.pageNo == defaultPage) {
                            myWhatsonBookData.clear()
                        }
                        if (response.body()!!.success) {
                            myWhatsonBookData.clear()
                            myWhatsonBookData.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                            this@MyWhatsonBookingFragment.pageNo++
                        }

                        if (myWhatsonBookData.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text =
                                getString(R.string.no_booking_to_display)
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                        }

                    }
                }

                override fun onFailure(call: Call<MyWhatsonBookRes?>, t: Throwable) {
                    onApiFailure(throwable = t)

                }
            })
        }
    }
}