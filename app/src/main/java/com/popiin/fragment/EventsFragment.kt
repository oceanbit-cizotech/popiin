package com.popiin.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.tech.MifareUltralight
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.activity.EventDetailFragment
import com.popiin.adapter.EventAdapter
import com.popiin.adapter.EventSearchAdapter
import com.popiin.adapter.EventTagAdapter
import com.popiin.adapter.WhatsOnPreviewImageAdapter
import com.popiin.annotation.CONSTANT
import com.popiin.annotation.FILTER_EVENT
import com.popiin.annotation.SHARE_MESSAGE_TYPE
import com.popiin.bottomDialogFragment.ChooseDateBottomFragment
import com.popiin.bottomDialogFragment.SelectEventFilterBottomFragment
import com.popiin.databinding.*
import com.popiin.listener.AdapterEventListener
import com.popiin.listener.BranchIOListener
import com.popiin.listener.ChooseDateListener
import com.popiin.listener.FavoriteListener
import com.popiin.listener.MapFilterListener
import com.popiin.listener.PaginationListener
import com.popiin.listener.VenueSearchItemClickListener
import com.popiin.model.TagModel
import com.popiin.realm.EventFavorites
import com.popiin.realm.VenuesFavorites
import com.popiin.req.EventReq
import com.popiin.req.MarkFavouriteReq
import com.popiin.res.CommonRes
import com.popiin.res.EventListRes
import com.popiin.util.Constant
import com.popiin.util.LogHandler
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import io.branch.referral.util.LinkProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventsFragment : BaseFragment<FragmentEventsBinding>(), FavoriteListener{
    private lateinit var tagAdapter: EventTagAdapter
    private var tagList: ArrayList<TagModel> = ArrayList()
    private var searchText = ""
    private var filter: String? = null
    private var fromDate: String? = null
    private var toDate: String? = null
    private var pageNo = defaultPage
    private var list: ArrayList<EventListRes.Event?> = ArrayList()
    private var searchEventLists: ArrayList<EventListRes.Event?> = ArrayList()
    private var eventListAdapter: EventAdapter? = null
    private var eventFilterBinding: DialogEventFilterBinding? = null
    private var isLastPage = false
    private var isLoading = true
    private var eventSearchAdapter : EventSearchAdapter ? = null

    companion object {
        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_events
    }

    override fun initViews() {
        LogHandler.writeLog(mActivity!!, "EventsFragment initViews()", "INFO")

        layoutManager = LinearLayoutManager(mActivity)

        tagAdapter = EventTagAdapter(tagList)
        PrefManager.setEventFilterSelected(FILTER_EVENT.NONE)

        binding.rvTags.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTags.adapter = tagAdapter

        tagList.clear()
        tagList.add(TagModel("All Events", false,""))
        tagList.add(TagModel("\uD83C\uDFB6 Nightclub", false,""))
        tagList.add(TagModel("\uD83C\uDF7B Bar", false,""))

        eventListAdapter = EventAdapter(list, adapterEventListener)

        eventFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.dialog_event_filter,
            null,
            false
        )

        binding.inclMessage.tvNoData.text = getString(R.string.txt_oops_error)

        binding.inclMessage.tvReload.setOnClickListener {
            if(isInternetConnect()) {
                updateList(pageNo, true)
            }
        }

        binding.rvEvents.layoutManager = layoutManager
        binding.rvEvents.adapter = eventListAdapter
        binding.rvEvents.addOnScrollListener(recyclerViewOnScrollListener)

        if (list.size == 0) {
            if(isInternetConnect()) {
                LogHandler.writeLog(mActivity!!, "EventsFragment isInternetConnect true", "INFO")
                updateList(pageNo, true)
            }
        }

        binding.srl.setOnRefreshListener {
            pageNo = defaultPage
            if(isInternetConnect()) {
                updateList(pageNo, true)
            }
        }

        binding.ivFilter.setOnClickListener {
            openEventFilterDialog()
        }

        binding.edtSearchEvents.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
               if (searchText.equals(charSequence.toString(), ignoreCase = true)) {
                    return
                }
                searchText = charSequence.toString()
                searchEvents( false)
                if (charSequence.isNotEmpty()) {
                    binding.ivCancel.visibility = View.VISIBLE
                } else {
                    searchEventLists.clear()
                    binding.ivCancel.visibility = View.GONE
                    binding.rvSearch.visibility = View.GONE
                    mActivity!!.runOnUiThread { hideKeyBoard(getBaseActivity()) }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.ivCancel.setOnClickListener {
            binding.ivCancel.visibility = View.GONE
            binding.edtSearchEvents.setText("")
        }

        val linearLayoutManager = LinearLayoutManager(getBaseActivity())
        linearLayoutManager.isAutoMeasureEnabled
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvSearch.layoutManager = linearLayoutManager
        eventSearchAdapter = EventSearchAdapter(searchEventLists!!, eventSearchListener)
        binding.rvSearch.adapter = eventSearchAdapter

        val animator: ItemAnimator? = binding.rvEvents.itemAnimator

        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }
    var layoutManager: LinearLayoutManager? = null

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                if(isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= MifareUltralight.PAGE_SIZE) {
                        if (this@EventsFragment.isLoading) {
                            isLoading = false
                            if(isInternetConnect()) {
                                updateList(pageNo, true)
                            }
                        }
                    }
                }

            }
        }

    private var eventSearchListener: VenueSearchItemClickListener<EventListRes.Event?> =
        object : VenueSearchItemClickListener<EventListRes.Event?>() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(event: EventListRes.Event?) {
                super.onItemClick(event)
                hideKeyBoard(requireActivity())
                binding.edtSearchEvents.setText("")
                eventSearchAdapter?.notifyDataSetChanged()
                    binding.rvSearch.visibility = View.GONE
                PrefManager.setEvent(event)
                if (event!!.type.equals(Constant().whatson)) {
                    openWhatDetailOnBottomSheetDialog(event)
                } else {
                    var eventDetailFragment= EventDetailFragment.newInstance();
                    eventDetailFragment.favorites=this@EventsFragment
                    setFragmentWithStack(
                        eventDetailFragment,
                        EventDetailFragment::class.java.simpleName
                    )
                    mActivity?.overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                }
                requireActivity().runOnUiThread { hideKeyBoard(getBaseActivity()) }
            }
        }

    private fun openEventFilterDialog() {
        val selectMapFilterFragment = SelectEventFilterBottomFragment(mapFilterListener)
        selectMapFilterFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectMapFilterFragment.show(it, "") }
    }

    private var mapFilterListener: MapFilterListener = object : MapFilterListener() {
        override fun onFilterEventItemClick(
            binding: BottomFragmentSelectEventFilterBinding,
            filter: String?,
            dialog: Dialog?,
            isReset: Boolean
        ) {
            super.onFilterEventItemClick(binding, filter, dialog, isReset)
            performFilterSearch(binding, dialog!!, isReset)
        }


    }

    fun setFavoriteData(item: EventListRes.Event) {
        val userId: Int = PrefManager.getUser()!!.user!!.id
        if (realmController!!.isFavoritesEvents(userId, item.id) == 1) {
            realm!!.executeTransaction { realm ->
                val rl =
                    realm.where(EventFavorites::class.java).equalTo("id", item.id)
                        .findFirst()
                rl!!.status = 0
            }
        } else if (realmController!!.isFavoritesEvents(userId, item.id) == 0) {
            realm!!.executeTransaction { realm ->
                val rl: EventFavorites? =
                    realm.where(EventFavorites::class.java).equalTo("id", item.id).findFirst()
                rl!!.status = 1
            }
        } else {
            val rl = EventFavorites(PrefManager.getUser()!!.user!!.id, item.id, 0)
            realm!!.beginTransaction()
            realm!!.copyToRealm(rl)
            realm!!.commitTransaction()
        }
    }


    var adapterEventListener: AdapterEventListener<EventListRes.Event?> =
        object : AdapterEventListener<EventListRes.Event?>() {
            override fun onFavouriteClick(
                item: EventListRes.Event?,
                position: Int,
                isFavourite: Boolean
            ) {
                list[position]!!.isFavorite=if (isFavourite) 1 else 0
                setFavoriteData(item!!)
                callEventFavourite(item, if (isFavourite) 1 else 0)
            }

            override fun onItemClick(item: EventListRes.Event?, position: Int) {
                super.onItemClick(item, position)
                 PrefManager.setEvent(item)
                hideKeyBoard(requireActivity())
                binding.edtSearchEvents.setText("")
                eventSearchAdapter?.notifyDataSetChanged()
                if (item!!.type.equals(Constant().whatson)) {
                    openWhatDetailOnBottomSheetDialog(item)
                } else {
                    var eventDetailFragment= EventDetailFragment.newInstance();
                    eventDetailFragment.favorites=this@EventsFragment
                    setFragmentWithStack(
                        eventDetailFragment,
                        EventDetailFragment::class.java.simpleName
                    )
                    mActivity?.overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                }

            }

            override fun onShareClick(item: EventListRes.Event?, position: Int) {
                super.onShareClick(item, position)
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
        }


    private fun callEventFavourite(item: EventListRes.Event, status: Int) {
        if (!isInternetConnect()) {
            return
        }
        val call: Call<CommonRes?>? = apiInterface.marFavourite(PrefManager.getAccessToken(),
           MarkFavouriteReq("" + item.id, 2, status)
        )
        call!!.enqueue(object : Callback<CommonRes?> {
            override fun onResponse(call: Call<CommonRes?>, response: Response<CommonRes?>) {

            }

            override fun onFailure(call: Call<CommonRes?>, t: Throwable) {
                onApiFailure(throwable = t)
            }
        })
    }

    private fun updateList(page: Int, isLoaderDisplay: Boolean) {
        if (isLoaderDisplay) {
            showProgress()
        }
        if (fromDate != null && fromDate!!.isNotEmpty()) {
            fromDate = common.convertDateInFormat(fromDate, "dd MMM yyyy", "yyyy-MM-dd ")
            toDate = common.convertDateInFormat(toDate, "dd MMM yyyy", "yyyy-MM-dd ")
        }
        val call: Call<EventListRes?>? = apiInterface.getEventList(
            PrefManager.getAccessToken(),
            EventReq(pageLimit, searchText, page, filter, fromDate, toDate))
        call!!.enqueue(object : Callback<EventListRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<EventListRes?>, response: Response<EventListRes?>) {
                if (mActivity!!.isValidResponse(response)) {
                    LogHandler.writeLog(mActivity!!, "EventsFragment response true", "INFO")
                    if (page == defaultPage) {
                        list.clear()
                    }
                    if (response.body()!!.getSuccess()) {
                        list.addAll(response.body()!!.getEvents())
                        isLoading = true
                    }else{
                        isLoading = false
                    }
                    pageNo++

                    val userId: Int = PrefManager.getUser()!!.user!!.id
                    for (i in 0 until list.size) {
                        if (realmController!!.isFavoritesEvents(userId, list[i]!!.id) == 1) {
                            list[i]!!.isFavorite = 1
                        } else if (realmController!!.isFavoritesEvents(userId, list[i]!!.id) == 0) {
                            list[i]!!.isFavorite = 0
                        } else {
                            list[i]!!.isFavorite = 0
                            val rl = EventFavorites(userId, list[i]!!.id, 0)
                            realm!!.beginTransaction()
                            realm!!.copyToRealm(rl)
                            realm!!.commitTransaction()

                        }
                    }


                    if (list.size == 0) {
                        binding.inclMessage.root.visibility = View.VISIBLE
                        binding.inclMessage.ivNoData.visibility = View.GONE
                        binding.inclMessage.tvNoData.text = getString(R.string.txt_oops)
                        binding.inclMessage.tvNoDataMessage.text = getString(R.string.txt_no_events)
                    } else {
                        binding.inclMessage.root.visibility = View.GONE
                    }
                    if (isLoaderDisplay) {
                        hideProgress()
                    }

                    eventListAdapter!!.notifyDataSetChanged()
                    if (eventFilterBinding!!.selectedFilter.equals(
                            FILTER_EVENT.PRICE_LOW_HIGH,
                            true
                        )
                    ) {
                        eventListAdapter!!.sortListByPrice(1)
                    } else if (eventFilterBinding!!.selectedFilter.equals(
                            FILTER_EVENT.PRICE_HIGH_LOW,
                            true
                        )
                    ) {
                        eventListAdapter!!.sortListByPrice(-1)
                    }
                    if (list.size >= response.body()!!.getTotal()) {
                        isLastPage = true
                    }
                }else{
                    isLoading = false
                }

                binding.srl.isRefreshing = false
            }

            override fun onFailure(call: Call<EventListRes?>, t: Throwable) {
               binding.srl.isRefreshing = false
                isLoading = false
                LogHandler.writeLog(mActivity!!, "EventsFragment onFailure "+t.printStackTrace(), "INFO")
                onApiFailure(throwable = t)
            }
        })
    }

    private fun performFilterSearch(
        binding: BottomFragmentSelectEventFilterBinding,
        dialog: Dialog,
        isReset: Boolean
    ) {
        filter = null
        if (binding.selectedFilter.equals(FILTER_EVENT.NONE, true)) {
            fromDate = null
            toDate = null
        } else if (binding.selectedFilter.equals(FILTER_EVENT.PRICE_LOW_HIGH, true)) {
            eventListAdapter!!.sortListByPrice(1);
  //          binding.searchBar.cbFilter.setChecked(false);
            return;
        } else if (binding.selectedFilter.equals(FILTER_EVENT.PRICE_HIGH_LOW, true)) {
            eventListAdapter!!.sortListByPrice(-1);
//            binding.searchBar.cbFilter.setChecked(false);
            return;
        } else if (binding.selectedFilter.equals(FILTER_EVENT.CHOOSE_A_DATE,true)) {
            filter = binding.selectedFilter
            if (!isReset) {
                showDateBottomFragment(dialog)
            }
            return
        } else {
            filter = binding.selectedFilter
        }
        pageNo = defaultPage
        if(isInternetConnect()) {
            updateList(pageNo, true)
        }
    }

    var dialog: Dialog? = null


    private fun showDateBottomFragment(eventFilterDialog: Dialog) {
        val showDateBottomFragment = ChooseDateBottomFragment(chooseDateListener, eventFilterDialog)
        showDateBottomFragment.isCancelable = false
        mActivity!!.supportFragmentManager.let { showDateBottomFragment.show(it, "") }
    }

    private var chooseDateListener: ChooseDateListener = object : ChooseDateListener() {
        override fun onSearchClick(dateFrom: String, dateTo: String) {
            super.onSearchClick(dateFrom, dateTo)
            fromDate = dateFrom
            toDate = dateTo
            pageNo = defaultPage
            if(isInternetConnect()) {
                updateList(pageNo, true)
            }
        }
    }

    private fun openWhatDetailOnBottomSheetDialog(item: EventListRes.Event) {
        val dialog = PopupWindow(mActivity)
        val binding: DialogWhatsOnDetailBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(mActivity),
                R.layout.dialog_whats_on_detail,
                null,
                false
            )

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        if(item.whatsonticket!=null && item.whatsonticket.size>0){
            binding.btnBookNow.visibility=View.VISIBLE
        }else{
            binding.btnBookNow.visibility=View.GONE
        }

        binding.btnBookNow.setOnClickListener {
            dialog.dismiss()
            setFragmentWithStack(
                WhatsOnEventBookFragment.newInstance(item),
                WhatsOnEventBookFragment::class.java.simpleName
            )
            mActivity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        }

        val displaymetrics = DisplayMetrics()

        @Suppress("DEPRECATION")
        mActivity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels

        binding.clPass.height = (height * 0.85).toInt()
        binding.clPass.requestLayout()


        val list: ArrayList<String> = ArrayList()

        list.clear()
        for (i in 0 until item.whatsonimages!!.size) {
            list.add(item.whatsonimages[i].image_url!!)
        }
        val imageViewAdapter = WhatsOnPreviewImageAdapter(list) {
            setFragmentAdd(
                ImagePreviewFragment.newInstance(list,0),
                ImagePreviewFragment::class.java.simpleName
            )
            dialog.dismiss()
        }
        binding.rvEventImages.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvEventImages.adapter = imageViewAdapter
        binding.rvEventImages.onFlingListener = null

        if(list.size==0){
            binding.ivDefault.visibility=View.VISIBLE
        }else{
            binding.ivDefault.visibility=View.GONE
        }

        binding.arIndicator.attachTo(binding.rvEventImages, true)
        binding.arIndicator.numberOfIndicators = if (list.size > 1) list.size else 0

        binding.inclName.desc = item.title
        binding.inclDesc.desc = item.description

        binding.inclStartDate.desc = common.convertDateInFormat(
            item.what_datetime,
            Constant().yyyyMmDdHhMmSs,
            Constant().eeeDdMmmYyyySpace
        )
        binding.inclEndDate.desc = common.convertDateInFormat(
            item.end_time,
            Constant().yyyyMmDdHhMmSs,
            Constant().eeeDdMmmYyyySpace
        )
        binding.inclStartTime.desc = common.convertDateInFormat(
            item.what_datetime,
            Constant().yyyyMmDdHhMmSs,
            Constant().hhMmA
        )
        binding.inclEndTime.desc = common.convertDateInFormat(
            item.end_time,
            Constant().yyyyMmDdHhMmSs,
            Constant().hhMmA
        )
        if (item.music != null) {
            binding.inclEventMusic.root.visibility = View.VISIBLE
            binding.inclEventMusic.desc =
                item.music!!
                    .replace(
                        CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER,
                        ", "
                    )
                    .replace(CONSTANT.SEPRATEOR, ", ")
                    .ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventMusic.root.visibility = View.GONE
        }


        if (item.entertainment != null) {
            binding.inclEventEntertainment.root.visibility = View.VISIBLE
            binding.inclEventEntertainment.desc = item.entertainment!!
                .replace(CONSTANT.SEPRATEOR + Constant().otherConst + CONSTANT.SEPRATEOR_OTHER, ", ")
                .replace(CONSTANT.SEPRATEOR, ", ")
                .ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventEntertainment.root.visibility = View.GONE
        }

        if (item.whatson_djline_up != null) {
            binding.inclEventDjLineUp.root.visibility = View.VISIBLE
            binding.inclEventDjLineUp.desc =
                item.whatson_djline_up.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventDjLineUp.root.visibility = View.GONE
        }

        if (item.whatson_dress_code != null) {
            binding.inclEventDressCode.root.visibility = View.VISIBLE
            binding.inclEventDressCode.desc =
                item.whatson_dress_code.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventDressCode.root.visibility = View.GONE
        }
        if (item.whatson_entry_policy != null) {
            binding.inclEventEntryPolicy.root.visibility = View.VISIBLE
            binding.inclEventEntryPolicy.desc =
                item.whatson_entry_policy.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventEntryPolicy.root.visibility = View.GONE
        }

        if (item.other_information != null) {
            binding.inclEventOtherInfo.root.visibility = View.VISIBLE
            binding.inclEventOtherInfo.desc =
                item.other_information.ifEmpty { getString(R.string.txt_not_available) }
        } else {
            binding.inclEventOtherInfo.root.visibility = View.GONE
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

    private fun searchEvents( isLoaderDisplay: Boolean) {

        if (isLoaderDisplay) {
            showProgress()
        }
        if (!isInternetConnect()) {
            return
        }

        val call = this.apiInterface.getEventList(
            PrefManager.getAccessToken(),
            EventReq(500, searchText, 0, "", "", ""))
          call!!.enqueue(object : Callback<EventListRes?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<EventListRes?>, response: Response<EventListRes?>) {
                if (isValidResponse(response)) {
                    searchEventLists.clear()
                    if (response.body()!!.getSuccess()) {
                        searchEventLists.addAll(response.body()!!.getEvents())
                        eventSearchAdapter?.filter(searchText)
                        eventSearchAdapter?.notifyDataSetChanged()
                        binding.rvSearch.visibility = View.VISIBLE
                        binding.ivCancel.visibility = View.VISIBLE
                    }
                }
            }
            override fun onFailure(call: Call<EventListRes?>, t: Throwable) {
                onApiFailure(throwable = t)

            }
        })
    }
    override fun onStatusUpdates(eventId: Int, status: Int, type:Int) {
        super.onStatusUpdates(eventId, status,type)
        val venue = list.find { it?.id == eventId }
        venue?.let {
            it.isFavorite = status
        }
        eventListAdapter?.notifyDataSetChanged()
    }
}