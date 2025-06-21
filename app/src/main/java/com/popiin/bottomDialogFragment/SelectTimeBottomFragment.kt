package com.popiin.bottomDialogFragment

import android.annotation.SuppressLint
import android.widget.EditText
import com.aigestudio.wheelpicker.WheelPicker
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentSelectTimeBinding
import com.popiin.listener.SelectDateListener
import com.popiin.util.Common
import com.popiin.util.Constant

class SelectTimeBottomFragment(var edtText: EditText, var time: String?,private var selectDateListener: SelectDateListener) :
    BaseBottomSheetDialog<BottomFragmentSelectTimeBinding>() {
    lateinit var hours: List<String>
    lateinit var minutes: List<String>
    private lateinit var amPms: List<String>
    var hour: String? = ""
    var minute: String? = ""
    private var amPm: String? = ""
    var common: Common = Common.instance!!
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_select_time
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        if (edtText.toString().isEmpty()) {
            hour = null
            minute = null
            amPm = null
        } else {
            hour = common.convertDateInFormat(
                edtText.text.toString(),
                Constant().hhMmA,
                Constant().hh
            )
            minute = common.convertDateInFormat(
                edtText.text.toString(),
                Constant().hhMmA,
                Constant().mm
            )
            amPm = common.convertDateInFormat(
                edtText.text.toString(),
                Constant().hhMmA,
                Constant().a
            )
        }

        binding!!.ivClose.setOnClickListener {
            dialog!!.dismiss()
        }

        hours = resources.getStringArray(R.array.hour).toList()
        minutes = resources.getStringArray(R.array.minutes).toList()
        amPms = resources.getStringArray(R.array.am_pm).toList()

        binding!!.inclTimePicker.wpHours.data = hours.toMutableList()
        binding!!.inclTimePicker.wpMinutes.data = minutes.toMutableList()
        binding!!.inclTimePicker.wpAmPm.data = amPms.toMutableList()


        if (hour != null && minute != null && amPm != null) {
            for (i in hours.indices) {
                if (hour == hours[i]) {
                    binding!!.inclTimePicker.wpHours.setSelectedItemPosition(i, false)
                    break
                }
            }

            for (i in minutes.indices) {
                if (minute == minutes[i]) {
                    binding!!.inclTimePicker.wpMinutes.setSelectedItemPosition(i, false)
                    break
                }
            }

            for (i in amPms.indices) {
                if (amPm == amPms[i]) {
                    binding!!.inclTimePicker.wpAmPm.setSelectedItemPosition(i, false)
                    break
                }
            }
        }


        binding!!.btnSelectTime.setOnClickListener {
            dialog!!.dismiss()

            println("openTimePickerDialog : " + binding!!.inclTimePicker.wpHours.data[binding!!.inclTimePicker.wpHours.currentItemPosition])
            println("openTimePickerDialog : " + binding!!.inclTimePicker.wpMinutes.data[binding!!.inclTimePicker.wpMinutes.currentItemPosition])
            println("openTimePickerDialog : " + binding!!.inclTimePicker.wpAmPm.data[binding!!.inclTimePicker.wpAmPm.currentItemPosition])

            edtText.setText(
                "" + binding!!.inclTimePicker.wpHours.data[binding!!.inclTimePicker.wpHours.currentItemPosition] +
                        ":" + binding!!.inclTimePicker.wpMinutes.data[binding!!.inclTimePicker.wpMinutes.currentItemPosition] +
                        "" + binding!!.inclTimePicker.wpAmPm.data[binding!!.inclTimePicker.wpAmPm.currentItemPosition]
            )

            time = common.convertDateInFormat(
                edtText.text.toString(),
                Constant().hhMmA,
                Constant().HHmmss24hrs
            )
            selectDateListener.onSelectTime(time.toString())

        }

        binding!!.inclTimePicker.wpHours.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
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

        binding!!.inclTimePicker.wpMinutes.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
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


        binding!!.inclTimePicker.wpAmPm.setOnItemSelectedListener(object :
            WheelPicker.OnWheelChangeListener, WheelPicker.OnItemSelectedListener {
            override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
                println("WheelPicker onItemSelected position : $position")
                println("WheelPicker onItemSelected data : $data")
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
}