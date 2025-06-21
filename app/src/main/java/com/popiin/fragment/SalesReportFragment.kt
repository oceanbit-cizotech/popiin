package com.popiin.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.popiin.BaseFragment
import com.popiin.R
import com.popiin.adapter.BookingSummaryReportAdapter
import com.popiin.adapter.DemographicSummaryReportAdapter
import com.popiin.adapter.ReportListAdapter
import com.popiin.bottomDialogFragment.SelectDurationFragment
import com.popiin.bottomDialogFragment.SelectReportEventBottomFragment
import com.popiin.databinding.FragmentSalesReportBinding
import com.popiin.listener.AdapterMyVenuesListener
import com.popiin.listener.AdapterReportListItemListner
import com.popiin.model.DurationModel
import com.popiin.model.ReportListModel
import com.popiin.req.SalesReportReq
import com.popiin.res.GetEventVenueRes
import com.popiin.res.SalesReportRes
import com.popiin.res.SalesReportRes.Demographics_reports
import com.popiin.res.SalesReportRes.ReportData
import com.popiin.res.WhatsOnListForSalesReportRes
import com.popiin.util.Constant
import com.popiin.util.PrefManager
import com.mindorks.Screenshot
import com.mindorks.properties.Quality
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.model.Axis.generateAxisFromRange
import lecho.lib.hellocharts.util.ChartUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SalesReportFragment : BaseFragment<FragmentSalesReportBinding>() {
    private var eventList: ArrayList<ReportListModel> = ArrayList()
    private var venueList: ArrayList<ReportListModel> = ArrayList()
    var durations = ArrayList<DurationModel>()
   // private var eventListAdapter: ReportListAdapter? = null
   // private var venueListAdapter: ReportListAdapter? = null
    var selectedVenue = 0
    var selectedEvent = 0
    var selectedDuration = 0
    var isEventSelected = false
    var isVenueSelected = false

    private lateinit var months: Array<String>
    private lateinit var days: Array<String>

    private var lineData: LineChartData? = null
    override fun getLayout(): Int {
        return R.layout.fragment_sales_report
    }

    companion object {
        fun newInstance(): SalesReportFragment {
            return SalesReportFragment()
        }
    }

    override fun initViews() {

        binding.inclAllEvents.edtText.setOnClickListener {
            // openAllEventsDialog()
            showReportBottomFragment(eventList,
                eventItemListener,
                selectedEvent,
                getString(R.string.txt_filter))
        }

        days = resources.getStringArray(R.array.days)
        months = resources.getStringArray(R.array.months)

        binding.inclAllVenues.edtText.setOnClickListener {
            //  openAllVenuesDialog()
           /* showReportBottomFragment(venueList,
                venueItemListener,
                selectedVenue,
                getString(R.string.txt_filter))*/
        }

        binding.topHeader.ivBack.setOnClickListener {
            mActivity!!.supportFragmentManager.popBackStack()
        }

        binding.tvDuration.setOnClickListener {
            showSelectDurationFragment()
        }

        binding.btnDownload.setOnClickListener {
            val bitmap: Bitmap = Screenshot.with(requireActivity())
                .setView(binding.llScreenShot)
                .setQuality(Quality.HIGH)
                .getScreenshot()
            shareImageUri(saveImageAndFileProviderUri(bitmap))
        }

        durations.clear()
        durations.add(DurationModel(Constant().oneWeek, false))
        durations.add(DurationModel(Constant().oneMonth, false))
        durations.add(DurationModel(Constant().threeMonth, false))
        durations.add(DurationModel(Constant().sixMonth, false))
        durations.add(DurationModel(Constant().oneYear, false))

        binding.common = common

        binding.rgAgeChart.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_age_16_20 -> {
                    minAge = 16
                    maxAge = 20
                }
                R.id.rb_age_21_25 -> {
                    minAge = 21
                    maxAge = 25
                }
                R.id.rb_age_26_30 -> {
                    minAge = 26
                    maxAge = 30
                }
                R.id.rb_age_31_35 -> {
                    minAge = 31
                    maxAge = 35
                }
                R.id.rb_age_36_40 -> {
                    minAge = 36
                    maxAge = 40
                }
                else -> {
                    minAge = 41
                    maxAge = 100
                }
            }

            setDataOnBarChart()
        }

       // binding.rbOneWeek.setChecked(true)
        reportDays = 7
        setDataOnChart()
        binding.tvDuration.text = durations[selectedDuration].title
        binding.rbAge1620.isChecked = true

        val sdf = SimpleDateFormat(Constant().ddMmm, Locale.ENGLISH)
        binding.tvSalesReportDate.text = sdf.format(getCurrentDateWithStartOfDay()!!)


        generateInitialLineData()
        callGetEventVenuesApi()
        callGetVenueWhatsListBusinessApi();

    }

    private fun showSelectDurationFragment() {
        val selectDurationFragment =
            SelectDurationFragment(durations, durationListener, selectedDuration)
        selectDurationFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectDurationFragment.show(it, "") }
    }

    private var durationListener: AdapterMyVenuesListener<DurationModel> =
        object : AdapterMyVenuesListener<DurationModel>() {
            override fun onEventSelected(item: DurationModel, position: Int) {
                super.onEventSelected(item, position)
                durations[position]
                selectedDuration = position
                durations[position].isSelected = true
                binding.tvDuration.text = durations[position].title

                when (position) {
                    0 -> {
                        reportDays = 7
                    }
                    1 -> {
                        reportDays = 30
                    }
                    2 -> {
                        reportDays = 90
                    }
                    3 -> {
                        reportDays = 180
                    }
                    4 -> {
                        reportDays = 365
                    }
                }

                setDataOnChart()

            }
        }


    private fun showReportBottomFragment(
        venues: ArrayList<ReportListModel>,
        listener: AdapterReportListItemListner<ReportListModel>,
        selectedVenue: Int,
        name: String,
    ) {

        val selectVenueBottomFragment = SelectReportEventBottomFragment(venues, listener, selectedVenue, name)
        selectVenueBottomFragment.isCancelable = false
        mActivity?.supportFragmentManager?.let { selectVenueBottomFragment.show(it, "") }
    }

    private fun callGetEventVenuesApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<GetEventVenueRes?>? =
                apiInterface.doGetEventVenues(PrefManager.getAccessToken())
            call!!.enqueue(object : Callback<GetEventVenueRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<GetEventVenueRes?>,
                    response: Response<GetEventVenueRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            eventList.clear()
                            for (event in response.body()!!.eventVenueData!!.events!!) {
                                eventList.add(ReportListModel(event.name!!, event.id,true))
                            }
                            isEventSelected=true
                            isVenueSelected=false
                            eventDataHandlingApi()
                        }
                    }
                }

                override fun onFailure(call: Call<GetEventVenueRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private fun callGetVenueWhatsListBusinessApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<WhatsOnListForSalesReportRes?>? =
                apiInterface.doGetVenueWhatsListBusiness(PrefManager.getAccessToken())
            call!!.enqueue(object : Callback<WhatsOnListForSalesReportRes?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<WhatsOnListForSalesReportRes?>,
                    response: Response<WhatsOnListForSalesReportRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            venueList.clear()
                            for (venues in response.body()!!.data!!) {
                                venueList.add(ReportListModel(venues!!.title!!, venues!!.id!!))
                            }
                           // venueListAdapter?.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<WhatsOnListForSalesReportRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }


    private val eventItemListener: AdapterReportListItemListner<ReportListModel> =
        object : AdapterReportListItemListner<ReportListModel>() {
            @SuppressLint("SetTextI18n")
            override fun onSelectEvent(list: ArrayList<ReportListModel>) {
                super.onSelectEvent(list)
                isEventSelected=true
                isVenueSelected=false
                eventList=list
                eventDataHandlingApi()
            }
        }

    fun eventDataHandlingApi(){
        eventName=""
        for(i in 0 until eventList.size){
            if (eventList[i].isSelected) {
                // venueListAdapter?.setAll(false)
                eventName+=eventList[i].name+","
            }
        }
        if(eventName.length>0){
            eventName=eventName.dropLast(1)
        }
        binding.inclAllEvents.edtText.setText(eventName)
        binding.inclAllVenues.edtText.setText("")
        hitAPIGetSalesReport()
    }

    private val venueItemListener: AdapterReportListItemListner<ReportListModel> =
        object : AdapterReportListItemListner<ReportListModel>() {

            override fun onSelectEvent(list: ArrayList<ReportListModel>) {
                super.onSelectEvent(list)
                venueList=list
                isVenueSelected = true
                isEventSelected = false

                var venueName = ""
                for(i in 0 until venueList.size){
                    if (i < 5){
                        venueName += venueList[i].name + ","
                    }
                }
                if(venueName.length>0){
                    venueName.dropLast(1)
                }
                binding.inclAllVenues.edtText.setText(venueName)
                binding.inclAllEvents.edtText.setText("")
                hitAPIGetSalesReport()
            }
        }

    private var reportData: List<ReportData>? = null

    private var reportDays = 0
    private var minAge = 0
    private var maxAge = 0
    private var type = 0
    private var _id = ""
    private var eventName = ""
    private var venueName = ""
    private fun hitAPIGetSalesReport() {
        _id=""
        eventName=""
        if (isInternetConnect()) {
            if (isEventSelected) {
                type = 1
                for (reportListModel in eventList) {
                    if (reportListModel.isSelected && reportListModel.id >= 0) {
                        _id = _id + (if (_id.isEmpty()) "" else ",") + reportListModel.id
                        eventName = eventName + (if (_id.isEmpty()) "" else ",") + reportListModel.name
                    }
                }

            } else if (isVenueSelected) {
                type = 3
                for (reportListModel in venueList) {
                    if (reportListModel.isSelected && reportListModel.id >= 0) {
                        _id = _id + (if (_id.isEmpty()) "" else ",") + reportListModel.id
                        venueName = venueName + (if (_id.isEmpty()) "" else ",") + reportListModel.name
                    }
                }
            }
            showProgress()
            val call: Call<SalesReportRes?>? = apiInterface.doGetSalesReport(
                PrefManager.getAccessToken(),
                SalesReportReq("" + type, _id, _id)
            )
            call!!.enqueue(object : Callback<SalesReportRes?>{
                override fun onResponse(
                    call: Call<SalesReportRes?>,
                    response: Response<SalesReportRes?>,
                ) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.body()!!.success) {
                            binding.report = response.body()
                            reportData = response.body()!!.reportData
                            setDataOnChart()
                            if (response.body()!!.summary!!.sales_Summary != null) {
                                initBookingSummaryPieChart()
                                initDemographicSummaryPieChart()
                            }
                            if (response.body()!!.ticket_types != null) {
                                setDataOnBarChart()
                            }
                        } else {
                            clearReports()
                        }
                    }
                }

                override fun onFailure(call: Call<SalesReportRes?>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }

    private var chartsDate: ArrayList<Calendar>? = null
    private var dayChartValues: MutableList<PointValue>? = null
    private var priceList: MutableList<Double>? = null

    @SuppressLint("DefaultLocale")
    private fun setDataOnChart() {
        if (reportData != null && reportData!!.isNotEmpty()) {
            binding.chartMonth.cancelDataAnimation()
            binding.tvDuration.text = durations[selectedDuration].title
            chartsDate = ArrayList()
            dayChartValues = ArrayList()
            priceList = ArrayList()
            val currentDate: Calendar = getCurrentCalenderWithStartOfDay()
            chartsDate!!.add(currentDate)
            var price = getGivenDateRevenueFromReportData(currentDate)
            (priceList as ArrayList<Double>).add(price)
            (dayChartValues as ArrayList<PointValue>).add(PointValue((reportDays - 1).toFloat(),
                price.toFloat()))
            for (i in 1 until reportDays) {
                val cal: Calendar = getCopyOfCalender(currentDate)
                cal.add(Calendar.DATE, -i)
                chartsDate!!.add(cal)
                price = getGivenDateRevenueFromReportData(cal)
                (priceList as ArrayList<Double>).add(price)
                (dayChartValues as ArrayList<PointValue>).add(PointValue((reportDays - i - 1).toFloat(), price.toFloat()))

            }
            val line: Line = lineData!!.lines[0]
            line.values = dayChartValues
            showDayReport()
            setSelectedDayData()
        } else {
            clearDayReports()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setSelectedDayData() {
        val maxYValue: Double = Collections.max(priceList!!)
        val minYValue = 0.0
        var stepCount = 0

        println("setSelectedDayData : maxYValues $maxYValue")

        for (i in priceList!!.indices) {
            if (priceList!![i] > 0) {
                stepCount++
            }
        }
        stepCount = if (stepCount > 0) {
            (maxYValue / stepCount).toInt()
        } else {
            10
        }

        println("setSelectedDayData : maxYValues $stepCount")

        //  lineData.getAxisYLeft().setValues(generateAxisFromRange((float) minYValue, (float) maxYValue + 5, (float) 10).getValues());
        lineData!!.axisYLeft.values = generateAxisFromRange(
            minYValue.toFloat(),
            maxYValue.toFloat() + .5f,
            5f
        ).values

        println("setSelectedDayData : lineData!!.axisYLeft.values " + lineData!!.axisYLeft.values)
        val axisValues: MutableList<AxisValue> = ArrayList()
        when (reportDays) {
            7 -> {
                for (i in 0 until chartsDate!!.size) {
                    axisValues.add(
                        AxisValue(dayChartValues!![i].x).setLabel(
                            days[chartsDate!![i].get(
                                Calendar.DAY_OF_WEEK
                            )]
                        )
                    )
                }
                lineData!!.axisXBottom.values = axisValues
            }
            30 -> {
                var index = reportDays - 1
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index -= 7
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index -= 7
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index -= 7
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                axisValues.add(
                    AxisValue(dayChartValues!![0].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![0].get(Calendar.DATE),
                            months[chartsDate!![0].get(Calendar.MONTH)]
                        )
                    )
                )
                lineData!!.axisXBottom.values = axisValues
            }
            90 -> {
                var index = 0
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index += 45
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index = reportDays - 1
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                lineData!!.axisXBottom.values = axisValues
            }
            180 -> {
                var index = 0
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index += 36
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index += 36
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index += 36
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index += 35
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                index = reportDays - 1
                axisValues.add(
                    AxisValue(dayChartValues!![index].x).setLabel(
                        String.format(
                            "%d %s", chartsDate!![index].get(Calendar.DATE),
                            months[chartsDate!![index].get(Calendar.MONTH)]
                        )
                    )
                )
                lineData!!.axisXBottom.values = axisValues
            }
            else -> {
                for (i in 0 until chartsDate!!.size) {
                    axisValues.add(
                        AxisValue(dayChartValues!![i].x).setLabel(
                            months[chartsDate!![i].get(
                                Calendar.MONTH
                            )]
                        )
                    )
                }
                lineData!!.axisXBottom.values = axisValues
            }
        }
        binding.chartMonth.startDataAnimation()
        val v = binding.chartMonth.maximumViewport
        v[v.left, (maxYValue + 5).toFloat(), (reportDays - 1 + 0.05 * reportDays).toFloat()] = 0f
        binding.chartMonth.maximumViewport = v
        binding.chartMonth.currentViewport = v
    }

    private fun getGivenDateRevenueFromReportData(cal: Calendar): Double {
        var returnPrice = 0.0
        for (data in reportData!!) {
            if (getGivenDateWithFormat(
                    data.created_at,
                    "yyyy-MM-dd hh:mm:ss"
                ).compareTo(cal) == 0
            ) {
                returnPrice += data.price
            }
        }
        return returnPrice
    }


    private fun clearReports() {
        reportData = null
        clearAgeReports()
        clearDayReports()
    }

    private fun clearDayReports() {
        binding.chartMonth.visibility = View.GONE
        binding.tvNoInfoMonthChart.visibility = View.VISIBLE
    }

    private fun showDayReport() {
        binding.chartMonth.visibility = View.VISIBLE
        binding.tvNoInfoMonthChart.visibility = View.GONE
    }

    private fun clearAgeReports() {
        binding.barChartAge.visibility = View.GONE
        binding.tvNoInfoAgeChart.visibility = View.VISIBLE
    }

    private fun showAgeReport() {
        binding.barChartAge.visibility = View.VISIBLE
        binding.tvNoInfoAgeChart.visibility = View.GONE
    }

    private fun generateInitialLineData() {
        val numValues = 7
        val axisValues: MutableList<AxisValue> = ArrayList()
        val values: MutableList<PointValue> = ArrayList()
        for (i in 0 until numValues) {
            values.add(PointValue(i.toFloat(), i.toFloat()))
            axisValues.add(AxisValue(i.toFloat()).setLabel(days[i]))
        }
        val line = Line(values)
        line.setHasPoints(false)
        line.strokeWidth = 2
        line.setColor(ContextCompat.getColor(requireActivity(), R.color.colorSwitch)).isCubic = true
        val lines: MutableList<Line> = ArrayList<Line>()
        lines.add(line)
        lineData = LineChartData(lines)
        lineData!!.axisXBottom = Axis(axisValues)
            .setHasLines(false)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
        lineData!!.axisYLeft = Axis()
            .setHasLines(true)
            .setLineColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
            .setFormatter(
                SimpleAxisValueFormatter().setPrependedText(common.currencySymbol.toCharArray())
                    .setAppendedText(" ".toCharArray())
            )
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
        binding.chartMonth.lineChartData = lineData

        // For build-up animation you have to disable viewport recalculation.
        binding.chartMonth.isViewportCalculationEnabled = false
        val v = binding.chartMonth.maximumViewport
        v[0f, 0f, 0f] = 0f
        binding.chartMonth.maximumViewport = v
        binding.chartMonth.currentViewport = v
        binding.chartMonth.isZoomEnabled = false

//        generateColumnData();
    }

    private lateinit var columnData: ColumnChartData

    private fun setDataOnBarChart() {
        if (reportData != null && reportData!!.isNotEmpty()) {
            val axisValues: MutableList<AxisValue> = ArrayList()
            val columns: MutableList<Column> = ArrayList<Column>()
            var values: MutableList<SubcolumnValue?>
            val countList: MutableList<Int> = ArrayList()
            for (i in 0 until binding.report!!.ticket_types!!.size) {
                val today: Calendar = getCurrentCalenderWithStartOfDay()
                values = ArrayList()
                var count = 0
                for (report in reportData!!) {
                    if (report.user != null) {
                        val dob: Calendar =
                            getGivenDateWithFormat(report.user.dob, Constant().yyyyMmDd)
                        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                            age--
                        }
                        if (age in minAge..maxAge) {
                            if (type == 1) {
                                if (binding.report!!.ticket_types!![i]
                                        .booking_type != null && report.booking_type != null
                                ) {
                                    if (binding.report!!.ticket_types!![i].booking_type
                                            .equals(report.booking_type,true)
                                    ) {
                                        count++
                                        countList.add(count)
                                    }
                                }
                            } else {
                                if (binding.report!!.ticket_types!![i].ticket_name != null && report.ticket != null && report.ticket.name != null
                                ) {
                                    if (binding.report!!.ticket_types!![i].ticket_name
                                            .equals(
                                                report.ticket.name, true)
                                    ) {
                                        count++
                                        countList.add(count)
                                    }
                                }
                            }
                        }
                    }
                }
                if (count > 0) {
                    if (type == 1) {
                        values.add(
                            SubcolumnValue(
                                count.toFloat(),
                                ContextCompat.getColor(requireActivity(), R.color.barchartColor)
                            )
                        )
                        axisValues.add(
                            AxisValue(columns.size.toFloat()).setLabel(
                                binding.report!!.ticket_types!![i].booking_type
                            )
                        )
                        columns.add(Column(values) /*.setHasLabelsOnlyForSelected(false)*/)
                    } else {
                        values.add(
                            SubcolumnValue(
                                count.toFloat(),
                                ContextCompat.getColor(requireActivity(), R.color.barchartColor)
                            )
                        )
                        axisValues.add(
                            AxisValue(columns.size.toFloat()).setLabel(binding.report!!.ticket_types!![i].ticket_name
                            )
                        )
                        columns.add(Column(values) /*.setHasLabelsOnlyForSelected(false)*/)
                    }
                }
            }
            if (columns.isNotEmpty()) {
                showAgeReport()
                val maxYValue: Int = Collections.max(countList)
                val minYValue = 0
                columnData = ColumnChartData(columns)
                columnData.isValueLabelBackgroundEnabled = false
                columnData.axisXBottom = Axis(axisValues)
                    .setHasLines(false)
                    .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
                columnData.axisYLeft = Axis()
                    .setHasLines(true)
                    .setValues(
                        generateAxisFromRange(
                            minYValue.toFloat(),
                            maxYValue.toFloat() + .2f,
                            1f
                        ).values
                    )
                    .setLineColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
                    .setName(getString(R.string.txt_num_of_bookings))
                    .setFormatter(SimpleAxisValueFormatter().setAppendedText(" ".toCharArray()))
                    .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
                binding.barChartAge.columnChartData = columnData

                // Set selection mode to keep selected month column highlighted.
                binding.barChartAge.isValueSelectionEnabled = false
                binding.barChartAge.isViewportCalculationEnabled = false
                val v = binding.barChartAge.maximumViewport
                v[v.left, maxYValue.toFloat() + .2f, columnData.columns.size - .5f] = 0f
                binding.barChartAge.maximumViewport = v
                binding.barChartAge.currentViewport = v
                binding.barChartAge.isValueTouchEnabled = false
                binding.barChartAge.isZoomEnabled = false
            } else {
                clearAgeReports()
            }
        } else {
            clearAgeReports()
        }
    }


    private var dataBooking: PieChartData? = null
    private var bookingSummaryReportAdapter: BookingSummaryReportAdapter? = null

    @SuppressLint("SetTextI18n")
    private fun initBookingSummaryPieChart() {
        if (binding.report == null) {
            return
        }
        val values: MutableList<SliceValue> = ArrayList()
        for (i in 0 until binding.report!!.ticket_types!!.size) {
            @Suppress("DIVISION_BY_ZERO") val sliceValue = SliceValue(
                (binding.report!!.ticket_types!![i].total * 100 / binding.report!!.summary!!.sales_Summary!![0].income).toFloat(),
                ContextCompat.getColor(
                    requireActivity(),
                    common.chartColorsId[i % common.chartColorsId.size]
                )
            )
            values.add(sliceValue)
        }
        dataBooking = PieChartData(values)
        dataBooking!!.setHasLabels(false)
        dataBooking!!.slicesSpacing = 1
        dataBooking!!.setHasCenterCircle(true)
        dataBooking!!.centerText1 =
            common.currencySymbol + common.getDecimalFormatValue(binding.report!!.summary!!.sales_Summary!![0].income)
        dataBooking!!.centerText2 = getString(R.string.txt_total)
        dataBooking!!.centerText2Color =
            ContextCompat.getColor(requireActivity(), R.color.colorSecondaryText)
        dataBooking!!.centerText1FontSize =
            ChartUtils.px2sp(
                resources.displayMetrics.scaledDensity,
                resources.getDimension(R.dimen.medium_text).toInt()
            )
        dataBooking!!.centerText2FontSize =
            ChartUtils.px2sp(
                resources.displayMetrics.scaledDensity,
                resources.getDimension(R.dimen.small_text).toInt()
            )
        dataBooking!!.centerText1Typeface =
            ResourcesCompat.getFont(requireActivity(), R.font.inter_semi_bold)
        binding.pieChartBooking.isValueTouchEnabled = false
        binding.pieChartBooking.circleFillRatio = 1f
        binding.pieChartBooking.setChartRotation(-90, false)
        binding.pieChartBooking.isChartRotationEnabled = false
        binding.pieChartBooking.pieChartData = dataBooking
        binding.tvBookingRevenue.text =
            (common.currencySymbol + common.getDecimalFormatValue(binding.report!!.summary!!.sales_Summary!![0].income))
        bookingSummaryReportAdapter = BookingSummaryReportAdapter(
            binding.report!!.ticket_types!!,
            binding.report!!.summary!!.sales_Summary!![0].bookings
        )
        binding.rvBookingSummary.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.rvBookingSummary.adapter = bookingSummaryReportAdapter
    }

    private var dataDemographic: PieChartData? = null
    private var demographicSummaryReportAdapter: DemographicSummaryReportAdapter? = null

    @SuppressLint("SetTextI18n")
    private fun initDemographicSummaryPieChart() {
        if (binding.report == null) {
            return
        }
        val values: MutableList<SliceValue> = ArrayList()
        binding.report!!.demographics_reports!!.sort()
        for (i in 0 until binding.report!!.demographics_reports!!.size) {
            val reports: Demographics_reports = binding.report!!.demographics_reports!![i]
            @Suppress("DIVISION_BY_ZERO") val sliceValue = SliceValue(
                (binding.report!!.demographics_reports!![i].total * 100 / binding.report!!.summary!!.sales_Summary!![0].income).toFloat(),
                ContextCompat.getColor(
                    requireActivity(),
                    common.chartDemoColorsId[if (reports.gender == null) 2 else if (reports.gender.equals(
                            Constant().one,
                            ignoreCase = true
                        )
                    ) 0 else 1]
                )
            )
            values.add(sliceValue)
        }
        dataDemographic = PieChartData(values)
        dataDemographic!!.setHasLabels(false)
        dataDemographic!!.slicesSpacing = 1
        dataDemographic!!.setHasCenterCircle(true)
        dataDemographic!!.centerText1 =
            common.currencySymbol + common.getDecimalFormatValue(binding.report!!.summary!!.sales_Summary!![0].income)
        dataDemographic!!.centerText2 = getString(R.string.txt_total)
        dataDemographic!!.centerText2Color =
            ContextCompat.getColor(requireActivity(), R.color.colorSecondaryText)

        dataDemographic!!.centerText1FontSize =
            ChartUtils.px2sp(
                resources.displayMetrics.scaledDensity,
                resources.getDimension(R.dimen.medium_text).toInt()
            )

        dataDemographic!!.centerText2FontSize =
            ChartUtils.px2sp(
                resources.displayMetrics.scaledDensity,
                resources.getDimension(R.dimen.small_text).toInt()
            )
        dataDemographic!!.centerText1Typeface =
            ResourcesCompat.getFont(requireActivity(), R.font.inter_semi_bold)
        binding.pieChartDemographic.isValueTouchEnabled = false
        binding.pieChartDemographic.circleFillRatio = 1f
        binding.pieChartDemographic.setChartRotation(-90, false)
        binding.pieChartDemographic.isChartRotationEnabled = false
        binding.pieChartDemographic.pieChartData = dataDemographic
        binding.tvBookingDemographRevenue.text = common.currencySymbol + common.getDecimalFormatValue(binding.report!!.summary!!.sales_Summary!![0].income)
        demographicSummaryReportAdapter = DemographicSummaryReportAdapter(
            binding.report!!.demographics_reports!!,
            binding.report!!.summary!!.sales_Summary!![0].bookings
        )
        binding.rvDemographicSummary.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.rvDemographicSummary.adapter = demographicSummaryReportAdapter
    }


}