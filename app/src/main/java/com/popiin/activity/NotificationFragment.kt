package com.popiin.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.NotificationAdapter
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.databinding.FragmentNotificationBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.InfoDialogListener
import com.popiin.req.DeleteNotificationReq
import com.popiin.res.DeleteNotificationRes
import com.popiin.res.NotificationsRes
import com.popiin.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    var list: ArrayList<NotificationsRes.Data> = ArrayList()
    var notificationIds: ArrayList<Int> = ArrayList()
    lateinit var notificationAdapter: NotificationAdapter
    var pageNo = defaultPage
    var isLoading = false
    var layoutManager: LinearLayoutManager? = null
    override fun getLayout(): Int {
        return R.layout.fragment_notification
    }

    companion object {
        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
    }

    override fun initViews() {
        binding.topHeader.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        pageNo = defaultPage
        if (list.size > 0) {
            list.clear()
        }

        layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        notificationAdapter = NotificationAdapter(list, listener)
        binding.rvNotifications.layoutManager = layoutManager
        binding.rvNotifications.adapter = notificationAdapter
        binding.rvNotifications.isNestedScrollingEnabled = false
        binding.rvNotifications.addOnScrollListener(recyclerViewOnScrollListener)

        callNotificationListApi(true, pageNo)
    }

    var selectedPosition = -1
    var listener: AdapterItemClickListener<NotificationsRes.Data> =
        object : AdapterItemClickListener<NotificationsRes.Data>() {
            override fun onAdapterDeleteClick(item: NotificationsRes.Data, position: Int) {
                super.onAdapterDeleteClick(item, position)
                notificationIds.clear()
                notificationIds.add(item.id)
                selectedPosition = position
                showCommonInfoBottomFragment(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_sure_delete),
                    getString(R.string.txt_sure_to_delete),
                    getString(R.string.txt_you_want_to_delete_notification),
                    getString(R.string.txt_no),
                    getString(R.string.txt_yes_delete),
                    infoDialogListener)

            }
        }

    private fun callNotificationListApi(isLoaderDisplay: Boolean, pageNo: Int) {
        if (isInternetConnect()) {
            if (isLoaderDisplay) {
                showProgress()
            }
            val call: Call<NotificationsRes?>? =
                apiInterface.getNotificationsList(PrefManager.getAccessToken(), pageNo, pageLimit)
            call!!.enqueue(object : Callback<NotificationsRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<NotificationsRes?>,
                    response: Response<NotificationsRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (this@NotificationFragment.pageNo == defaultPage) {
                            list.clear()
                        }
                        if (response.body()!!.success) {
                            list.addAll(response.body()!!.data)
                            notificationAdapter.notifyDataSetChanged()
                            isLoading = true
                            this@NotificationFragment.pageNo++
                        } else {
                            isLoading = false
                        }

                        if (list.size == 0) {
                            binding.tvMessage.visibility = View.VISIBLE
                            binding.tvMessage.text = getString(R.string.no_notification_to_display)
                        }else{
                            binding.tvMessage.visibility = View.GONE
                        }

                        if (isLoaderDisplay) {
                            hideProgress()
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationsRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun showCommonInfoBottomFragment(
        image: Drawable? = null,
        message: String,
        desc: String,
        negativeText: String,
        positiveText: String,
        listener: InfoDialogListener,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(
                image,
                message,
                desc,
                negativeText,
                positiveText,
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText),
                ContextCompat.getColor(requireActivity(), R.color.colorHeaderText), false,
                listener
            )
        commonInfoBottomFragment.isCancelable = false
        requireActivity().supportFragmentManager.let { commonInfoBottomFragment.show(it, "") }
    }

    var infoDialogListener: InfoDialogListener = object : InfoDialogListener() {

        override fun onInfoNegativeClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoNegativeClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoCloseClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoCloseClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
            callDeleteNotificationApi(notificationIds, selectedPosition)
        }

    }

    private fun callDeleteNotificationApi(notificationId: ArrayList<Int>, position: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<DeleteNotificationRes?>? =
                apiInterface.doDeleteNotification(PrefManager.getAccessToken(),
                    DeleteNotificationReq(notificationId))
            call!!.enqueue(object : Callback<DeleteNotificationRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<DeleteNotificationRes?>,
                    response: Response<DeleteNotificationRes?>,
                ) {
                    hideProgress()
                    if (response.body()!!.success) {
                        list.removeAt(position)
                        notificationAdapter.notifyDataSetChanged()
                    } else {
                        showToast(response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<DeleteNotificationRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener = object :
        RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount: Int = layoutManager!!.childCount
            val totalItemCount: Int = layoutManager!!.itemCount
            val firstVisibleItemPosition: Int = layoutManager!!.findFirstVisibleItemPosition()
            if (isLoading) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    if (isLoading) {
                        isLoading = false
                        callNotificationListApi(false, pageNo)
                    }
                }
            }
        }

    }

}