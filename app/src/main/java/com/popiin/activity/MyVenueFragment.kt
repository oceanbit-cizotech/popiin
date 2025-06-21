package com.popiin.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.tech.MifareUltralight
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
import com.popiin.adapter.MyVenuesAdapter
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.databinding.FragmentMyVenueBinding
import com.popiin.databinding.DialogResetPasswordBinding
import com.popiin.fragment.VenueVerificationFragment
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.listener.BranchIOListener
import com.popiin.listener.SearchTextListener
import com.popiin.req.DeleteVenuesReq
import com.popiin.req.EventReq
import com.popiin.res.CommonRes
import com.popiin.res.VenueListRes
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVenueFragment : BaseFragment<FragmentMyVenueBinding>() {
    var pageNo = defaultPage
    var venues = ArrayList<VenueListRes.Venue>()
    var myVenuesAdapter: MyVenuesAdapter? = null
    var strSearch = ""
    var layoutManager: LinearLayoutManager? = null
    var isLoading = true



    override fun getLayout(): Int {
        return R.layout.fragment_my_venue
    }

    companion object {
        fun newInstance(): MyVenueFragment {
            return MyVenueFragment()
        }
    }

    override fun initViews() {
        callMyVenuesApi(true)

        layoutManager = LinearLayoutManager(mActivity)
        myVenuesAdapter = MyVenuesAdapter(venues, venuesListener)
        binding.rvMyVenues.layoutManager = layoutManager
        binding.rvMyVenues.adapter = myVenuesAdapter
        binding.rvMyVenues.addOnScrollListener(recyclerViewOnScrollListener)

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.inclMessage.tvReload.setOnClickListener {
            callMyVenuesApi(true)
        }

        binding.searchBar.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard(getBaseActivity())
            }
            true
        }

        binding.topHeader.ivSearch.setOnClickListener {
            if (binding.searchBar.root.isVisible) {
                binding.searchBar.root.visibility = View.GONE
                callMyVenuesApi(true)
                if (binding.searchBar.edtSearch.text.toString().isNotEmpty()) {
                    strSearch = ""
                    binding.searchBar.edtSearch.setText("")
                }
            } else {
                binding.searchBar.root.visibility = View.VISIBLE
            }
        }

        binding.searchBar.edtSearch.addTextChangedListener(object : SearchTextListener() {
            override fun performSearch(searchText: String?) {
                pageNo = defaultPage
                strSearch = searchText!!
                if (searchText.isNotEmpty()) {
                    binding.searchBar.ivCancel.visibility = View.VISIBLE
                } else {
                    hideKeyBoard(getBaseActivity())
                    binding.searchBar.ivCancel.visibility = View.GONE
                }
                callMyVenuesApi(false)
            }
        })


        binding.searchBar.ivCancel.setOnClickListener {
            binding.searchBar.edtSearch.setText("")
            binding.searchBar.ivCancel.visibility = View.GONE
        }

        binding.btnCreateVenue.setOnClickListener {
            common.refreshRegisterVenueModel()
            setFragmentAdd(
                RegisterVenueFragment.newInstance(null, false),
                RegisterVenueFragment::class.java.simpleName
            )
        }

    }

    private fun callMyVenuesApi(isLoader: Boolean) {

        if (isInternetConnect()) {
            if (isLoader) {
                showProgress()
            }
            val call: Call<VenueListRes?>? =
                apiInterface.doGetVenuesList(
                    PrefManager.getAccessToken(),
                    EventReq(pageLimit, strSearch, pageNo)
                )
            call!!.enqueue(object : Callback<VenueListRes?> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<VenueListRes?>,
                    response: Response<VenueListRes?>,
                ) {
                    if (isValidResponse(response)) {
                        if (pageNo == defaultPage) {
                            venues.clear()
                        }
                        if (response.body()!!.success) {
                            venues.clear()
                            venues.addAll(response.body()!!.venues)
                            pageNo++
                            isLoading = true
                        }
                        if (venues.size == 0) {
                            binding.inclMessage.root.visibility = View.VISIBLE
                            binding.inclMessage.tvNoDataMessage.text = response.body()!!.message
                        } else {
                            binding.inclMessage.root.visibility = View.GONE
                            binding.inclMessage.tvNoDataMessage.text = ""
                        }

                        checkDocumentVerified()
                        
                        myVenuesAdapter!!.notifyDataSetChanged()
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<VenueListRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun checkDocumentVerified() {
        var isHidden : Boolean=true
        for (venue in venues) {
            if(venue.document == null){
                isHidden=false
                break
            }else if(venue.document.verificationStatus == 0){
                    isHidden=false
                    break
                }
        }
        if(isHidden){
            binding.llVenueVerificationStatus.visibility=View.GONE
        }else{
            binding.llVenueVerificationStatus.visibility=View.VISIBLE
        }
    }

    private var venuesListener: AdapterMyVenuesListener<VenueListRes.Venue?> =
        object : AdapterMyVenuesListener<VenueListRes.Venue?>() {
            override fun onEvenCopyClick(item: VenueListRes.Venue?, position: Int) {
                super.onEvenCopyClick(item, position)

                val clipboard: ClipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied", item!!.venue_share_link)
                clipboard.setPrimaryClip(clip)
                common.openDialog(requireActivity(), getString(R.string.venue_link_copied))
            }

            override fun onEventDeleteClick(item: VenueListRes.Venue?, position: Int) {
                super.onEventDeleteClick(item, position)
                item!!.id.openDeleteVenueDialog(position)
            }

            override fun onEventShareClick(item: VenueListRes.Venue?, position: Int) {
                super.onEventShareClick(item, position)
                if(item!!.venue_share_link!=null && item.venue_share_link!!.isNotEmpty()){
                    shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue_name, url =item.venue_share_link!! )
                }else {
                    val properties: LinkProperties = LinkProperties().addControlParameter(Parameters().venueId, "" + id)
                    shareBranchIOLink(properties, object : BranchIOListener() {
                        override fun onLinkCreate(url: String?) {
                            super.onLinkCreate(url)
                            shareMessageBuilder(type = SHARE_MESSAGE_TYPE.VENUE, title = item.venue_name, url =url!! )
                            callPostVenueUpdateLinkApi(item.id, url!!)
                        }
                    })
                }
            }

            override fun onEventEditClick(item: VenueListRes.Venue?, position: Int) {
                super.onEventEditClick(item, position)
                setFragmentAdd(
                    RegisterVenueFragment.newInstance(item, true),
                    RegisterVenueFragment::class.java.simpleName
                )
            }

            override fun onDocumentVerifyClick(item: VenueListRes.Venue?, position: Int) {
                super.onDocumentVerifyClick(item, position)
                if (item!!.document != null) {
                    setFragmentWithStack(
                        VenueVerificationFragment.newInstance(item, true),
                        VenueVerificationFragment::class.java.simpleName
                    )
                } else {
                    setFragmentWithStack(
                        VenueVerificationFragment.newInstance(item, false),
                        VenueVerificationFragment::class.java.simpleName
                    )
                }

            }

            override fun onVenueItemCopyClick(item: VenueListRes.Venue?, position: Int) {
                super.onVenueItemCopyClick(item, position)
                setFragmentAdd(
                    RegisterVenueFragment.newInstance(item, true),
                    RegisterVenueFragment::class.java.simpleName
                )
            }
        }

    private fun Int.openDeleteVenueDialog(position: Int) {
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
            binding.tvPassSuccess.text = getString(R.string.txt_venue_removed)
            binding.tvSuccess.text = getString(R.string.txt_venue_has_deleteed)
        } else {
            binding.tvPassSuccess.text = getString(R.string.txt_do_you_want_to)
            binding.tvSuccess.text = getString(R.string.txt_remove_venue)
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
                callDeleteVenue(position, this)
            }/* else {
                mActivity!!.supportFragmentManager.popBackStack()
            }*/

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

    private fun callDeleteVenue(position: Int, venueId: Int) {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<CommonRes?>? = apiInterface.doDeleteVenues(
                PrefManager.getAccessToken(),
                DeleteVenuesReq("" + venueId)
            )
            call!!.enqueue(object : Callback<CommonRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {
                    if (isValidResponse(response)) {
                        if (response.body()!!.status) {
                            venues.removeAt(position)
                            myVenuesAdapter!!.notifyDataSetChanged()
                            isLoading = true
                            0.openDeleteVenueDialog(-1)

                            if (venues.size == 0) {
                                binding.inclMessage.root.visibility = View.VISIBLE
                                binding.inclMessage.tvNoDataMessage.text = "No venues to display."
                            } else {
                                binding.inclMessage.root.visibility = View.GONE
                                binding.inclMessage.tvNoDataMessage.text = ""
                            }
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
                        if (this@MyVenueFragment.isLoading) {
                            isLoading = false
                            callMyVenuesApi(false)
                        }
                    }
                }
            }
        }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

}