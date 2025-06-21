package com.popiin.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.tech.MifareUltralight
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.MyEventsAdapter
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.databinding.FragmentMyEventsBinding
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.listener.AdapterItemClickListener
import com.popiin.listener.BranchIOListener
import com.popiin.listener.SearchTextListener
import com.popiin.req.DeleteEventsReq
import com.popiin.req.EventReq
import com.popiin.res.CommonRes
import com.popiin.res.EventListRes
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyEventsFragment : BaseFragment<FragmentMyEventsBinding>() {
    var myEvents = ArrayList<EventListRes.Event?>()
    var myEventsAdapter: MyEventsAdapter? = null
    var pageNo = defaultPage
    var strSearch = ""
    var layoutManager: LinearLayoutManager? = null
    var isLoading = true
    override fun getLayout(): Int {
        return R.layout.fragment_my_events
    }

    companion object {
        fun newInstance(): MyEventsFragment {
            return MyEventsFragment()
        }
    }

    override fun initViews() {
        pageNo = defaultPage
        layoutManager = LinearLayoutManager(mActivity)
        binding.rvMyEvents.layoutManager = layoutManager
        myEventsAdapter = MyEventsAdapter(myEvents, listener)
        binding.rvMyEvents.adapter = myEventsAdapter
        binding.rvMyEvents.addOnScrollListener(recyclerViewOnScrollListener)
        callMyEventsApi(true)

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.inclMessage.tvReload.setOnClickListener {
            callMyEventsApi(true)
        }

        binding.topHeader.ivSearch.setOnClickListener {
            if (binding.searchBar.root.isVisible) {
                binding.searchBar.root.visibility = View.GONE
                callMyEventsApi(true)
                if (binding.searchBar.edtSearch.text.toString().isNotEmpty()) {
                    strSearch = ""
                    binding.searchBar.edtSearch.setText("")
                }
            } else {
                binding.searchBar.root.visibility = View.VISIBLE
            }
        }

        binding.searchBar.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mActivity!!.runOnUiThread { hideKeyBoard(getBaseActivity()) }
            }
            true
        }

        binding.searchBar.edtSearch.addTextChangedListener(object : SearchTextListener() {
            override fun performSearch(searchText: String?) {
                pageNo = defaultPage
                strSearch = searchText!!
                if (searchText.isNotEmpty()) {
                    binding.searchBar.ivCancel.visibility = View.VISIBLE
                } else {
                    binding.searchBar.ivCancel.visibility = View.GONE
                    Handler(Looper.myLooper()!!).postDelayed({ hideKeyBoard(getBaseActivity()) },
                        200)
                }
                callMyEventsApi(false)
            }

        })



        binding.searchBar.ivCancel.setOnClickListener {
            binding.searchBar.edtSearch.setText("")
            binding.searchBar.ivCancel.visibility = View.GONE
        }

        binding.btnCreateEvent.setOnClickListener {
            common.refreshCreateEventModel()
            setFragmentAdd(
                CreateEventFragment.newInstance( false),
                CreateEventFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun callMyEventsApi(isLoader: Boolean) {
        if (isInternetConnect()) {
            if (isLoader) {
                showProgress()
            }
            val call: Call<EventListRes?>? = apiInterface.doGetEventList(PrefManager.getAccessToken(),
                EventReq(pageLimit, strSearch, pageNo))
            call!!.enqueue(object : Callback<EventListRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<EventListRes?>,
                    response: Response<EventListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (pageNo == defaultPage) {
                            myEvents.clear()
                        }

                        if (response.body()!!.getSuccess()) {
                            myEvents.clear()
                            myEvents.addAll(response.body()!!.getEvents())
                            pageNo++
                            isLoading = true
                        }

                        myEventsAdapter!!.notifyDataSetChanged()
                    }

                    if (myEvents.size == 0) {
                        binding.inclMessage.root.visibility = View.VISIBLE
                        binding.inclMessage.tvNoDataMessage.text =
                            response.body()!!.getMessage()
                        showToast(response.body()!!.getMessage())
                    } else {
                        binding.inclMessage.root.visibility = View.GONE
                        binding.inclMessage.tvNoDataMessage.text = ""
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<EventListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    var listener : AdapterItemClickListener<EventListRes.Event?> = object :
        AdapterItemClickListener<EventListRes.Event?>() {
        override fun onAdapterItemClick(item: EventListRes.Event?, position: Int) {
            super.onAdapterItemClick(item, position)
            if (item != null) {
                common.isSetEventData=true
                common.convertEventModelToModel(item, isEdit = true,)
            }
            setFragmentAdd(
                CreateEventFragment.newInstance( true),
                CreateEventFragment::class.java.simpleName
            )

            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        override fun onEventDeleteClick(item: EventListRes.Event?, position: Int) {
            super.onEventDeleteClick(item, position)
            item!!.id.openDeleteEventDialog(position)
        }

        override fun onEventCopyClick(item: EventListRes.Event?, position: Int) {
            super.onEventCopyClick(item, position)
            val clipboard: ClipboardManager = mActivity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied", item!!.share_link)
            clipboard.setPrimaryClip(clip)
            common.openDialog(requireActivity(), getString(R.string.event_link_copied))
        }

        override fun onEventShareClick(item: EventListRes.Event?, position: Int) {
            super.onEventShareClick(item, position)
            if(item!!.share_link!=null && item.share_link!!.isNotEmpty()){
                shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = item.name, url =item.share_link!! )
            }else {
                val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().eventId, "" + id)
                shareBranchIOLink(properties, object : BranchIOListener() {
                    override fun onLinkCreate(url: String?) {
                        super.onLinkCreate(url)
                        shareMessageBuilder(type = SHARE_MESSAGE_TYPE.EVENT, title = item.name, url =url!! )
                        callPostUpdateLinkApi(item.id, url!!)
                    }
                })
            }
        }

        override fun onEventItemCopyClick(item: EventListRes.Event?, position: Int) {
            super.onEventItemCopyClick(item, position)

            if (item != null) {
                common.isSetEventData=true
                common.convertEventModelToModel(item, isEdit = false,isCopyTickets = false)
                common.createEventModel.id=0
            }
            setFragmentAdd(
                CreateEventFragment.newInstance( false),
                CreateEventFragment::class.java.simpleName
            )

            mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun Int.openDeleteEventDialog(position: Int) {
        val dialog = PopupWindow(requireActivity())
        val binding: DialogResetPasswordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_reset_password, null, false
        )


        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        if (position == -1 && this == 0) {
            binding.btnNo.visibility = View.GONE
            binding.ivPassSuccess.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_pass_success
                )
            )
            binding.ivClose.visibility = View.GONE
            binding.tvPassSuccess.text = getString(R.string.txt_event_removed)
            binding.tvSuccess.text = getString(R.string.txt_event_has_deleteed)
        }else{
            binding.tvPassSuccess.text = getString(R.string.txt_do_you_want_to)
            binding.tvSuccess.text = getString(R.string.txt_remove_event)
            binding.ivPassSuccess.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_sure_delete
                )
            )
            binding.ivClose.visibility = View.VISIBLE
            binding.btnNo.visibility = View.VISIBLE
            binding.btnNo.text = getString(R.string.txt_no)
            binding.btnDone.text = getString(R.string.txt_yes_remove)
        }


        binding.btnDone.setOnClickListener {
            dialog.dismiss()
            if (position != -1 && this != 0) {
                callDeleteEvent(position, this)
            } else {
                mActivity!!.supportFragmentManager.popBackStack()
            }

        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.contentView = binding.root
        dialog.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.isClippingEnabled = false
        dialog.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        dialog.contentView.bringToFront()
    }

    private fun callDeleteEvent(position: Int, id: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? = apiInterface.doDeleteEvent(PrefManager.getAccessToken(),
                DeleteEventsReq("" + id)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            myEvents.removeAt(position)
                            myEventsAdapter!!.notifyDataSetChanged()
                            isLoading = true
                            0.openDeleteEventDialog(-1)
                        } else {
                            isLoading = false
                            common.openDialog(requireActivity(), response.body()!!.msg)
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                if (isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= MifareUltralight.PAGE_SIZE) {
                        if (this@MyEventsFragment.isLoading) {
                            isLoading = false
                            callMyEventsApi(false)
                        }
                    }
                }
            }
        }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            callMyEventsApi(true)
        }
    }


}