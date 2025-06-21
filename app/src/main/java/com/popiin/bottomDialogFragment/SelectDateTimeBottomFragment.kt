package com.popiin.bottomDialogFragment

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentSelectDateTimeBinding
import com.popiin.databinding.CalendarDayLayoutBinding
import com.popiin.util.Common
import com.popiin.util.Constant
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

class SelectDateTimeBottomFragment(var edtText: EditText) :
    BaseBottomSheetDialog<BottomFragmentSelectDateTimeBinding>() {
    private var today: LocalDate? = null
    private var selectedDate: LocalDate? = null
    private var currentTime = ""
    private var eventTime = ""
    private var hour: String = ""
    private var minute: String = ""
    private var amPm: String = ""
    private var startDate: String = ""
    private lateinit var hours: List<String>
    private lateinit var minutes: List<String>
    private lateinit var amPms: List<String>
    private val common = Common.instance!!
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_date_time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {
        val currentMonth = YearMonth.now()
        today = LocalDate.now()

        val monthScroll: Function1<CalendarMonth, Unit> = { calendarMonth: CalendarMonth ->
            binding!!.inclCalender.tvMonthYear.text =
                String.format(calendarMonth.yearMonth.month.toString() + " " + calendarMonth.yearMonth.year.toString())
                    .lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        binding!!.inclCalender.calenderView.setup(
            currentMonth,
            currentMonth.plusMonths(100),
            DayOfWeek.SUNDAY
        )
        binding!!.inclCalender.calenderView.scrollToMonth(currentMonth)
        binding!!.inclCalender.calenderView.monthScrollListener = monthScroll

        val cal = Calendar.getInstance()
        val timeHour = cal.get(Calendar.HOUR_OF_DAY)
        val timeMinute = cal.get(Calendar.MINUTE)
        val timeSeconds = cal.get(Calendar.SECOND)

        val time = "$timeHour:$timeMinute:$timeSeconds"

        currentTime = common.convertDateInFormat(time, Constant().HHmmss24hrs, Constant().hhMmA)!!

        binding!!.tvCalTime.text = currentTime

        configureBinders(binding)

        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }

        var combinedDate: String
        var formattedDate: String?

        binding!!.btnSelectDate.setOnClickListener {
            dialog!!.dismiss()
            if (startDate.isEmpty() && eventTime.isEmpty()) {
                combinedDate = "$today $currentTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            } else if (startDate.isEmpty()) {
                combinedDate = "$today $eventTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            } else if (eventTime.isEmpty()) {
                combinedDate = "$startDate $currentTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            } else {
                combinedDate = "$startDate $eventTime"
                formattedDate = common.convertDateInFormat(
                    combinedDate,
                    Constant().yyyyMmDdHhMmA,
                    Constant().ddMmYyyyHhMmA
                )
                edtText.setText(formattedDate)
            }

        }

        binding!!.inclCalender.ivCalLeft.setOnClickListener {
            binding!!.inclCalender.ivCalLeft.isEnabled = false
            Handler(Looper.myLooper()!!).postDelayed({
                val leftMonth = binding!!.inclCalender.calenderView.findFirstVisibleMonth()
                val leftYearMonth = leftMonth!!.yearMonth

                if (leftYearMonth != currentMonth) {
                    binding!!.inclCalender.calenderView.smoothScrollToMonth(leftYearMonth.previousMonth)
                }

                binding!!.inclCalender.tvMonthYear.text =
                    String.format(leftMonth.yearMonth.month.toString() + " " + leftMonth.yearMonth.year.toString())
                        .lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                binding!!.inclCalender.ivCalLeft.isEnabled = true

            }, 200)
        }


        binding!!.inclCalender.ivCalRight.setOnClickListener {
            binding!!.inclCalender.ivCalRight.isEnabled = false

            Handler(Looper.myLooper()!!).postDelayed({
                val rightMonth = binding!!.inclCalender.calenderView.findFirstVisibleMonth()
                val rightYearMonth = rightMonth!!.yearMonth

                binding!!.inclCalender.calenderView.smoothScrollToMonth(rightYearMonth.nextMonth)


                binding!!.inclCalender.tvMonthYear.text =
                    String.format(rightMonth.yearMonth.month.toString() + " " + rightMonth.yearMonth.year.toString())
                        .lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                binding!!.inclCalender.ivCalRight.isEnabled = true

            }, 200)
        }


        binding!!.tvCalTime.setOnClickListener {
            if (binding!!.clTimeDialog.visibility == View.GONE) {
                binding!!.clTimeDialog.visibility = View.VISIBLE
            } else {
                binding!!.clTimeDialog.visibility = View.GONE
            }
        }

        hours = resources.getStringArray(R.array.hour).toList()
        minutes = resources.getStringArray(R.array.minutes).toList()
        amPms = resources.getStringArray(R.array.am_pm).toList()

        binding!!.inclTime.wpHours.data = hours.toMutableList()
        binding!!.inclTime.wpMinutes.data = minutes.toMutableList()
        binding!!.inclTime.wpAmPm.data = amPms.toMutableList()

        binding!!.inclTime.wpHours.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
                hour = data.toString()
                binding!!.tvCalTime.text = "$hour:$minute $amPm"
                eventTime = "$hour:$minute $amPm"
            }

            override fun onWheelScrolled(offset: Int) {
                println("WheelPicker onWheelScrolled : $offset")
            }

            override fun onWheelSelected(position: Int) {
                println("WheelPicker onWheelSelected : $position")
            }

            override fun onWheelScrollStateChanged(state: Int) {
                println("WheelPicker onWheelScrollStateChanged : $state")
            }

        })

        binding!!.inclTime.wpMinutes.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
                minute = data.toString()
                binding!!.tvCalTime.text = "$hour:$minute $amPm"
                eventTime = "$hour:$minute $amPm"
            }

            override fun onWheelScrolled(offset: Int) {
                println("WheelPicker onWheelScrolled : $offset")
            }

            override fun onWheelSelected(position: Int) {
                println("WheelPicker onWheelSelected : $position")
            }

            override fun onWheelScrollStateChanged(state: Int) {
                println("WheelPicker onWheelScrollStateChanged : $state")
            }

        })


        binding!!.inclTime.wpAmPm.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
                amPm = data.toString()
                binding!!.tvCalTime.text = "$hour:$minute $amPm"
                eventTime = "$hour:$minute $amPm"
            }

            override fun onWheelScrolled(offset: Int) {
                println("WheelPicker onWheelScrolled : $offset")
            }

            override fun onWheelSelected(position: Int) {
                println("WheelPicker onWheelSelected : $position")
            }

            override fun onWheelScrollStateChanged(state: Int) {
                println("WheelPicker onWheelScrollStateChanged : $state")
            }

        })
    }

    private fun configureBinders(binding: BottomFragmentSelectDateTimeBinding?) {
        val calendarView = binding!!.inclCalender.calenderView

        @RequiresApi(Build.VERSION_CODES.O)
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).exFourDayText

            init {
                textView.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate == day.date) {
                            selectedDate = day.date
                            calendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(oldDate) }
                        }

                    }

                    // set date text here

                    startDate = selectedDate.toString()

                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun create(view: View) = DayViewContainer(view)

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                Log.e(TAG, "bind: data: $data")
                container.day = data
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()

                if (container.day.position == DayPosition.OutDate || container.day.position == DayPosition.InDate) {
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(), R.color.colorGrey
                        )
                    )
                }

                if (data.position == DayPosition.MonthDate) {
                    when (data.date) {
                        selectedDate -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(), R.color.colorLightBlue
                                )
                            )
                            textView.background =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.bg_calender_day
                                )
                        }
                        today -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(), R.color.colorWhite
                                )
                            )
                            textView.background =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.bg_calender_today
                                )
                        }
                        else -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(), R.color.colorBlack
                                )
                            )
                            textView.background = null
                        }
                    }
                }

                if (container.day.date.isBefore(LocalDate.now())) {
                    container.textView.setOnClickListener(null)
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(), R.color.colorGrey
                        )
                    )
                }

            }
        }

    }
}