package com.popiin.bottomDialogFragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentSelectDateBinding
import com.popiin.databinding.CalendarDayLayoutBinding
import com.popiin.util.Common
import com.popiin.util.Constant
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.popiin.listener.SelectDateListener
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import java.time.format.DateTimeFormatter
class SelectDateBottomFragment(var edtText: EditText, private var strDate: String?,private var selectDateListener: SelectDateListener) :
    BaseBottomSheetDialog<BottomFragmentSelectDateBinding>() {
    private var selectDate: LocalDate? = null
    var date = ""

    @RequiresApi(Build.VERSION_CODES.O)
    var today: LocalDate = LocalDate.now()
    var common = Common.instance
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() {
        val currentMonth = YearMonth.now()

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

        configureBinders(binding)

        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }


        binding!!.btnSelectDate.setOnClickListener {
            dialog!!.dismiss()
            if (date.isEmpty()) {
                edtText.setText(
                    common!!.convertDateInFormat(
                        today.toString(),
                        Constant().yyyyMmDd, Constant().ddMmmYyyy
                    )
                )
            } else {
                edtText.setText(
                    common!!.convertDateInFormat(
                        date,
                        Constant().yyyyMmDd,
                        Constant().ddMmmYyyy
                    )
                )
            }

            strDate = common!!.convertDateInFormat(
                edtText.text.toString(),
                Constant().ddMmmYyyy,
                Constant().yyyyMmDd
            )
            var strDate:String =getDayOfWeekFromDate(strDate.toString());
            println("@@@@@@ selected date is :::: "+strDate)
            selectDateListener.onSelectDate(strDate)
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

    }
    fun setButtonLogic(day:String){

    }

    @SuppressLint("NewApi")
    fun getDayOfWeekFromDate(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        return date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
    }

    private fun configureBinders(binding: BottomFragmentSelectDateBinding?) {
        val calendarView = binding!!.inclCalender.calenderView

        @RequiresApi(Build.VERSION_CODES.O)
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).exFourDayText

            init {
                textView.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectDate == day.date) {
                            selectDate = day.date
                            calendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectDate
                            selectDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(oldDate) }
                        }

                        date = selectDate.toString()

                    }

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
                        selectDate -> {
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