package com.popiin.util
import java.time.Duration

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import java.util.Date

import android.os.Build
import android.os.SystemClock
import android.provider.CalendarContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import carbon.widget.EditText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.popiin.annotation.CATEGORY_TYPE
import com.popiin.annotation.CONSTANT
import com.popiin.bottomDialogFragment.CommonInfoBottomFragment
import com.popiin.callback.FireBaseTokenCallback
import com.popiin.dialog.CommonDialog
import com.popiin.listener.InfoDialogListener
import com.popiin.model.CreateEventModel
import com.popiin.model.CreateWhatsOnModel
import com.popiin.model.RegisterVenueModel
import com.popiin.model.TicketBook
import com.popiin.model.TimingModel
import com.popiin.res.EventListRes
import com.popiin.res.VenueListRes.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import com.popiin.R
import com.popiin.fragment.WhatsOnQRCodeFragment.Companion.myWhatsOnItem
import com.popiin.model.VenueModel
import com.popiin.res.VenueStoryListRes
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class Common private constructor() {
    val processingFees =14
    val closedText = "Close Now"
    private val openText = "Open Now"
    var formatter: DecimalFormatter = DecimalFormatter.instance!!
    var createEventModel :CreateEventModel = CreateEventModel()
    var registerVenueModel = RegisterVenueModel()
    var createWhatsOnModel = CreateWhatsOnModel()
     val venueList: ArrayList<Venue> = ArrayList()
     var oldStoriesList: ArrayList<VenueStoryListRes> = ArrayList()

    var isSetEventData:Boolean=false
    var currencySymbol = "£"
    val yyyyMmDdHhMmSs = "yyyy-MM-dd HH:mm:ss"
    val ddMmYyyyHhMmA = "dd MMM yyyy hh:mma"
    var chartColorsId = intArrayOf(
        R.color.chartColor1,
        R.color.chartColor2,
        R.color.chartColor3,
        R.color.chartColor4
    )

    var chartDemoColorsId = intArrayOf(
        R.color.chartDemoGraphColor1,
        R.color.chartDemoGraphColor2,
        R.color.chartDemoGraphColor3,
    )

    companion object {
        private const val CLICK_TIME: Long = 1000
        var instance: Common? = null
            get() {
                if (field == null) {
                    field = Common()
                }
                return field
            }
            private set

    }

    private var clickTime: Long = 0
    val isAllowClick: Boolean
        get() {
            if (SystemClock.uptimeMillis() - clickTime <= CLICK_TIME) {
                return false
            }
            clickTime = SystemClock.uptimeMillis()
            return true
        }

    fun handleErrorView(editText: EditText, textView: TextView) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (textView.visibility == View.VISIBLE) {
                    textView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun isMoreThan72Hours(endDateTimeStr: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val endDateTime = LocalDateTime.parse(endDateTimeStr, formatter)
        val now = LocalDateTime.now()

        val duration = Duration.between(now, endDateTime)
        val hours = duration.toHours()

        return hours > 72
    }
//    fun handleCarbonEdtErrorView(editText: carbon.widget.EditText, textView: TextView) {
//        editText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                if (textView.visibility == View.VISIBLE) {
//                    textView.visibility = View.GONE
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {}
//        })
//    }

    fun handleEdtErrorView(simpleEditText: android.widget.EditText, textView: TextView) {
        simpleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (textView.visibility == View.VISIBLE) {
                    textView.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun convertDateInFormat(dates: String?, inputFormat: String?, outputFormat: String?): String? {
        return try {
            @SuppressLint("SimpleDateFormat") val format1 = SimpleDateFormat(inputFormat, Locale.US)
            @SuppressLint("SimpleDateFormat") val format2 = SimpleDateFormat(outputFormat, Locale.US)

            System.out.println("#### "+dates+" "+inputFormat+" "+outputFormat+".    "+format2.format(Objects.requireNonNull(format1.parse(dates!!))))

            format2.format(Objects.requireNonNull(format1.parse(dates!!)))

        } catch (e: Exception) {
            System.out.println("#### ")
            e.printStackTrace()
            dates
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUTCtoLocalDateInFormat(inputDate: String, inputFormat: String, outputFormat: String): String {
        // Input formatter to parse the UTC time in "yyyy-MM-dd HH:mm:ss" format
        val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)

        // Parse the input date as a LocalDateTime
        val utcDateTime = LocalDateTime.parse(inputDate, inputFormatter)

        // Convert it to UTC ZonedDateTime
        val utcZonedDateTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"))

        // Convert to the system's local time zone
        val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

        // Output formatter for the desired format "MMM dd yyyy hh:mma"
        val outputFormatter = DateTimeFormatter.ofPattern(outputFormat)

        // Format the local time to the required format
        return localZonedDateTime.format(outputFormatter).replace("am","AM").replace("pm","PM")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTwoHoursAndFormatTime(inputDateTime: String,inputFormat: String?, outputFormat: String?): String {
        // Input format: yyyy-MM-dd HH:mm:ss
        val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
        val outputFormatter = DateTimeFormatter.ofPattern(outputFormat)

        return try {
            // Parse the input string to LocalDateTime
            val dateTime = LocalDateTime.parse(inputDateTime, inputFormatter)

            // Add 2 hours
            val newDateTime = dateTime.plusHours(2)

            // Format the output in 12-hour format (hh:mm a)
            newDateTime.format(outputFormatter).toUpperCase()
        } catch (e: DateTimeParseException) {
            "Invalid date format"
        }
    }

    fun convert24HrsTimes(): ArrayList<TimingModel>{
        System.out.println("####. convert24HrsTimes")
       var tempTiming : ArrayList<TimingModel> =ArrayList()
        for (i in registerVenueModel.timingList.indices) {
            if (registerVenueModel.timingList.get(i).open_time!!.isNotEmpty() && registerVenueModel.timingList.get(i).close_time!!.isNotEmpty()) {

                tempTiming.add(
                    TimingModel(
                        day = registerVenueModel.timingList.get(i).day,
                        open_time = convertDateInFormat(
                            registerVenueModel.timingList.get(i).open_time,
                            Constant().hhMmA,
                            Constant().HHmmss24hrs
                        ),
                        close_time = convertDateInFormat(
                            registerVenueModel.timingList.get(i).close_time,
                            Constant().hhMmA,
                            Constant().HHmmss24hrs
                        ),
                        is_24hours = registerVenueModel.timingList.get(i).is_24hours,
                        isClear = registerVenueModel.timingList.get(i).isClear
                    ))

            }
        }
        return tempTiming
    }

    fun getFirebaseToken(listener: FireBaseTokenCallback) {
//        Log.d(FireBaseMessagingService.TAG, "TOKEN FORM CALL START : ")
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful || task.result == null) {
                    Log.e("FirebaseToken", "Failed to get token", task.exception)
                    return@addOnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token ?: ""
                Log.d("FirebaseToken", "Token received: $token")

                if (token.isNotEmpty()) {
                    PrefManager.setFirebaseToken(token)
                    listener.onTokenGenerateSuccess(token)  // Ensure listener is not null
                } else {
                    Log.e("FirebaseToken", "Token is empty")
                }
            }


    }

    fun loadImageFromUrl(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.bg_rv_item)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(
            context, vectorResId)

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight)

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @Suppress("DEPRECATION")
    fun getActiveOffer(offersLive: List<Offerslive>): List<Offerslive> {
        val returnList: MutableList<Offerslive> = ArrayList()
        for (offer in offersLive) {
            if (offer.working_day.equals(formatter.getWeekDayInWord(), ignoreCase = true)) {
                var openDate: Date = Common().getDate(offer.open_time, Constant().HHmmss24hrs)!!
                var closeDate: Date = Common().getDate(offer.close_time, Constant().HHmmss24hrs)!!
                val openDateCalender = Calendar.getInstance()
                openDateCalender[Calendar.HOUR_OF_DAY] = openDate.hours
                openDateCalender[Calendar.MINUTE] = openDate.minutes
                openDateCalender[Calendar.SECOND] = openDate.seconds
                val closeDateCalender = Calendar.getInstance()
                closeDateCalender[Calendar.HOUR_OF_DAY] = closeDate.hours
                closeDateCalender[Calendar.MINUTE] = closeDate.minutes
                closeDateCalender[Calendar.SECOND] = closeDate.seconds
                val currentDate = Calendar.getInstance().time
                openDate = openDateCalender.time
                closeDate = closeDateCalender.time
                if (formatter.checkIsBetweenGivenDate(currentDate, openDate, closeDate)) {
                    returnList.add(offer)
                }
            }
        }
        return returnList
    }


    @Suppress("DEPRECATION")
    fun getOpenCloseText(venue: Venue): String? {
        var returnText: String? = closedText
        for (timing in venue.timings!!) {
            if (timing.working_day.equals(formatter.getWeekDayInWord(), ignoreCase = true)) {
                var openDate: Date = getDate(timing.open_time, Constant().HHmmss24hrs)!!
                var closeDate: Date = getDate(timing.close_time, Constant().HHmmss24hrs)!!
                val openDateCalender = Calendar.getInstance()
                openDateCalender[Calendar.HOUR_OF_DAY] = openDate.hours
                openDateCalender[Calendar.MINUTE] = openDate.minutes
                openDateCalender[Calendar.SECOND] = openDate.seconds
                val closeDateCalender = Calendar.getInstance()
                closeDateCalender[Calendar.HOUR_OF_DAY] = closeDate.hours
                closeDateCalender[Calendar.MINUTE] = closeDate.minutes
                closeDateCalender[Calendar.SECOND] = closeDate.seconds
                val currentDate = Calendar.getInstance().time
                openDate = openDateCalender.time
                closeDate = closeDateCalender.time
                if (openDate.after(closeDate)) {
                    closeDateCalender.add(Calendar.DAY_OF_YEAR, 1)
                    closeDate = closeDateCalender.time
                }
                if (timing.is_24hours == 1 || formatter.checkIsBetweenGivenDate(currentDate,
                        openDate,
                        closeDate)
                ) {
                    returnText = openText
                }
                break
            }
        }
        return returnText
    }



    fun getCategoryListFromVenue(venue: Venue, topThreeVenueType: List<String?>?): String {
        var returnString = ""
        if (checkIsMusic(venue, topThreeVenueType)) {
            if ((venue.venue_music != null) && venue.venue_music.isNotEmpty()) {
                return venue.venue_music
                    .replace(CONSTANT.SEPRATEOR, ", ")
            } else {
                return "";
            }
        } else {
            val foods = getFoodGroupCategoriesList(venue)
            var venueCategories = ""
            for (food in foods) {
                venueCategories += (if (venueCategories.isEmpty()) "" else ", ") + food
            }
            if (venueCategories.isEmpty()) {
                venueCategories = "Food"
            }

            return venueCategories
        }
        if (returnString.isEmpty()) {
            returnString = "N/A"
        }
        return returnString
    }

    private fun getFoodGroupCategoriesList(venue: Venue): List<String> {
        val returnList: MutableList<String> = ArrayList()
        for (venueCategory in venue.venuecategories!!) {
            val foods: Array<String> =
                venueCategory.category_name!!.replace(CONSTANT.SEPRATEOR, ", ").split(", ")
                    .toTypedArray()
            returnList.addAll(foods)
        }
        return returnList
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(dates: String?, format: String?): Date? {
        try {
            return SimpleDateFormat(format).parse(dates!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun getTopThreeVenueTypeList(venue: Venue): List<String?> {
        val venueTypeInString: ArrayList<String?> = ArrayList()
        if (venue.venuetypes != null) {

            for (i in 0 until venue.venuetypes.size){
                    venueTypeInString.add(venue.venuetypes[i].venue_type)
            }
        }
        return venueTypeInString
    }

    fun getTopThreeVenueTypeList(venue: VenueModel): List<String?> {
        val venueTypeInString: ArrayList<String?> = ArrayList()
        if (venue.venuetypes != null) {
            for (i in 0 until venue.venuetypes.size){
                venueTypeInString.add(venue.venuetypes[i].venue_type)
            }
        }
        return venueTypeInString
    }

    @Suppress("KotlinConstantConditions")
    fun checkIsMusic(venue: Venue, topThreeVenueType: List<String?>?): Boolean {
        if (venue.venuetypes != null) {
            if (topThreeVenueType!!.contains(CATEGORY_TYPE.RESTAURANT) && topThreeVenueType.contains(
                    CATEGORY_TYPE.HOTEL)
            ) {
                return false
            } else if (topThreeVenueType.size > 0 && topThreeVenueType[0].equals(CATEGORY_TYPE.RESTAURANT,
                    ignoreCase = true)
            ) {
                return false
            } else if (topThreeVenueType.size > 0 && topThreeVenueType[0].equals(CATEGORY_TYPE.HOTEL,
                    ignoreCase = true)
            ) {
                return false
            } else if (topThreeVenueType.size > 0 && topThreeVenueType[0].equals(CATEGORY_TYPE.BAR,
                    ignoreCase = true)
            ) {
                return true
            } else if (topThreeVenueType.size > 0 && topThreeVenueType[0].equals(CATEGORY_TYPE.CAFE,
                    ignoreCase = true)
            ) {
                return false
            }
        }
        return true
    }

    @Suppress("DEPRECATION")
    fun getOpenCloseTimingsWithTime(venue: Venue) : String{
        var returnText = ""
        for (timings in venue.timings!!){
            if (timings.is_24hours != 1) {
                var openDate: Date = getDate(timings.open_time, Constant().HHmmss24hrs)!!
                var closeDate: Date = getDate(timings.close_time, Constant().HHmmss24hrs)!!

                val openDateCalender = Calendar.getInstance()
                openDateCalender[Calendar.HOUR_OF_DAY] = openDate.hours
                openDateCalender[Calendar.MINUTE] = openDate.minutes
                openDateCalender[Calendar.SECOND] = openDate.seconds

                val closeDateCalender = Calendar.getInstance()
                closeDateCalender[Calendar.HOUR_OF_DAY] = closeDate.hours
                closeDateCalender[Calendar.MINUTE] = closeDate.minutes
                closeDateCalender[Calendar.SECOND] = closeDate.seconds

                val currentDate = Calendar.getInstance().time

                openDate = openDateCalender.time
                closeDate = closeDateCalender.time


                if (timings.working_day!!.equals(formatter.weekDayInWord)) {
                    if (openDate.after(closeDate)) {
                        closeDateCalender.add(Calendar.DAY_OF_YEAR, 1)
                        closeDate = closeDateCalender.time
                    }

                    if (formatter.checkIsBetweenGivenDate(currentDate, openDate, closeDate)) {

                        val diff = closeDate.time - currentDate.time

                        returnText = "${Constant().close} $diff"

                        break
                    } else {

                        if (currentDate.before(openDate)) {
                            returnText = "(Open in ####)"

                            val diff = openDate.time - currentDate.time
                            (diff / (1000 * 60 * 60 * 24)).toInt()
                            val hours = (diff / (1000 * 60 * 60 * 24)).toInt()
                            val minutes = (diff / (1000 * 60 * 60 * 24)).toInt()
                            (diff / (1000 * 60 * 60 * 24)).toInt()
                            val timeText =
                                if (hours > 0) hours.toString() + (if (hours == 1) " ${Constant().hour}" else " ${Constant().hours}") else "$minutes ${Constant().minutes}"
                            returnText = returnText.replace(Constant().hash, timeText)
                            break
                        }
                    }
                }else{
                    if (!(timings.open_time.equals(timings.close_time))) {
                        timings.working_day
                        val workingDayIndex = formatter.weekDayInWord.indexOf(timings.working_day)
                        val toDayIndex =
                            formatter.weekDayInWord.indexOf(formatter.getWeekDayInWord())

                        val diffIndex = workingDayIndex - toDayIndex

                        if (diffIndex == -6 || diffIndex == 1) {
                            val diff = openDate.time - currentDate.time
                            (diff / (1000 * 60 * 60 * 24))
                            val hours = (diff / (1000 * 60 * 60)).toInt()
                            val minutes = (diff / (1000 * 60)) % 60
                            val seconds = (diff / (1000))

                            if (hours in 0..23 && minutes >= 0 && seconds >= 0) {
                                returnText = "(Open in ####)"
                                val timeText =
                                    if (hours > 0) hours.toString() + (if (hours == 1) " hour" else " hours") else "$minutes minutes"
                                returnText = returnText.replace("####", timeText)
                                break
                            }
                        }

                    }
                }

            }
        }

        return returnText
    }

    fun getOneDecimalFormatValueWithMiles(input: Double): String {
        return if (input > 1) {
            formatter.getOneDecimalFormatValue(input) + " miles"
        } else {
            if(input < 0.1) {
                formatter.getOneDecimalFormatValue(0.1) + " mile"
            }else{
                formatter.getOneDecimalFormatValue(input) + " mile"
            }
        }
    }

    fun getDecimalFormatValue(input: Double): String? {
        return try {
            String.format("%.2f", input)
        } catch (e: Exception) {
            e.printStackTrace()
            input.toString()
        }
    }

    fun openDialog(activity: Activity, message: String?) {
        val commonDialog = CommonDialog(
            activity,
            activity.getString(R.string.lbl_ok), "", "", message!!
        )
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        if (commonDialog.binding.title!!.isEmpty()) {
            params.setMargins(0, 15, 0, 0)
            commonDialog.binding.tvMessage.layoutParams = params
        } else {
            params.setMargins(0, -20, 0, 0)
            commonDialog.binding.tvMessage.layoutParams = params
        }
        commonDialog.binding.btnPositive.setOnClickListener {
            commonDialog.dismiss()
        }
        commonDialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun checkVenueTypeValidate(
        iterator: Iterator<Int>?,
        types: Array<String>,
        edtName: android.widget.EditText,
        tvError: TextView,
    ): Boolean {
        var isValidate = true
        if (iterator != null) {
            var index: Int
            while (iterator.hasNext()) {
                index = iterator.next()
                if (index == types.size - 1) {
                    if (edtName.text.toString().isEmpty()) {
                        isValidate = false
                        tvError.visibility = View.VISIBLE
                        tvError.text = "Please enter details"
                    }
                }
            }
        }
        return isValidate
    }

    @SuppressLint("SimpleDateFormat")
    fun convertStringToDate(dtStart: String, format: String): Date {
        val dateFormat = SimpleDateFormat(format)
        var date = Date()
        try {
            date = dateFormat.parse(dtStart) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun addEvents(
        context: Context,
        strStartDate: String?,
        strEndDate: String?,
        name: String?,
        description: String?,
    ) {
        val cr = context.contentResolver
        val values = ContentValues()
        val formatter = SimpleDateFormat(Constant().yyyyMmDdHhMmSs)
        val startDateCalender = Calendar.getInstance()
        val endDateCalender = Calendar.getInstance()
        val startDate: Date
        val endDate: Date
        if (strStartDate == null) {
            return
        }
        try {
            startDate = formatter.parse(strStartDate) as Date
            endDate = formatter.parse(strEndDate!!)!!
            startDateCalender.time = startDate
            endDateCalender.time = endDate
        } catch (e: android.net.ParseException) {
            e.printStackTrace()
        }
        values.put(CalendarContract.Events.DTSTART, startDateCalender.timeInMillis)
        values.put(CalendarContract.Events.DTEND, endDateCalender.timeInMillis)
        values.put(CalendarContract.Events.TITLE, name)
        values.put(CalendarContract.Events.DESCRIPTION, description)
        val timeZone = TimeZone.getDefault()
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.id)

        // default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1)


        // for one hour
        //  values.put(CalendarContract.Events.DURATION, "+P1H");
        values.put(CalendarContract.Events.HAS_ALARM, 1)

        // insert event to calendar
        cr.insert(CalendarContract.Events.CONTENT_URI, values)

    }

    fun refreshCreateEventModel() {
        createEventModel = CreateEventModel()
    }

    fun refreshRegisterVenueModel() {
        registerVenueModel = RegisterVenueModel()
    }

    fun refreshWhatsOnModel() {
        createWhatsOnModel = CreateWhatsOnModel()
    }

    fun getZoomLevel(circle: Circle?): Int {
        var zoomLevel = 11
        if (circle != null) {
            val radius = circle.radius + circle.radius / 2
            val scale = radius / 500
            zoomLevel = (16 - ln(scale) / ln(2.0)).toInt()
        }
        return zoomLevel
    }

    fun openErrorDialog(
        context: Context,
        manager: FragmentManager,
        image: Drawable?,
        title: String,
        desc: String,
    ) {
        val commonInfoBottomFragment =
            CommonInfoBottomFragment(image,
                title, desc, "", context.getString(R.string.txt_ok),
                ContextCompat.getColor(context, R.color.colorBlack),
                ContextCompat.getColor(context, R.color.colorBlack), true, infoDialogListener)
        commonInfoBottomFragment.isCancelable = false
        commonInfoBottomFragment.show(manager, "")
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

        @SuppressLint("NotifyDataSetChanged")
        override fun onInfoPositiveClick(bottomSheetDialog: CommonInfoBottomFragment) {
            super.onInfoPositiveClick(bottomSheetDialog)
            bottomSheetDialog.dismiss()
        }

    }

    fun calculateDistanceInMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Radius of the Earth in miles
        val earthRadius = 3958.8

        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Calculate differences
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        // Haversine formula
        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Distance in miles
        return earthRadius * c
    }


    fun metersToMiles(meters: Float): Double {
        return meters / 1609.34  // 1 mile = 1609.34 meters
    }

    fun kilometersToMiles(kilometers: Double): Double {
        return kilometers * 0.621371
    }

    fun isCurrentDateBeforeStartDate(startDateStr: String): Boolean {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val startDate: Date? = formatter.parse(startDateStr)
        val currentDate: Date = Calendar.getInstance().time // gets current system date and time

        return if (startDate != null) {
            currentDate.before(startDate)
        } else {
            false
        }
    }

    fun dateInBitween(startTime:String ,endTime:String) : Boolean{
        var isVaild:Boolean=false
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        } else {
            TODO("VERSION.SDK_INT < O")
        }


        // Parse the date-time strings to LocalDateTime
        val startDateTime = LocalDateTime.parse(startTime, formatter)
        val endDateTime = LocalDateTime.parse(endTime, formatter)

        val currentDateTime = LocalDateTime.now()

        // Check if the current date/time is within the start and end date-time range
        if (currentDateTime.isEqual(startDateTime) || currentDateTime.isEqual(endDateTime) ||
            (currentDateTime.isAfter(startDateTime) && currentDateTime.isBefore(endDateTime))) {
            System.out.println("@@@ "+startDateTime+" "+endDateTime+" "+currentDateTime )

            isVaild =true
        }
        return isVaild
    }

    @SuppressLint("NewApi")
    fun isOfferAvailable(day:String, startTime: String, endTime: String):Boolean{

        if (isToday(day)) {
            if(isCurrentTimeBetween(startTime,endTime)){
                return true
            }else{
                return false
            }
        }else{
            return false
        }
    }

    @SuppressLint("NewApi")
    fun isToday(dayString: String): Boolean {
        // Get the current day of the week
        val currentDay = LocalDate.now().dayOfWeek.name.toLowerCase()  // e.g., "monday", "tuesday", etc.
        // Compare the provided day string with the current day
        System.out.println("#### isToday  "+currentDay+". "+dayString.toLowerCase())

        return currentDay == dayString.toLowerCase()
    }

    @SuppressLint("NewApi")
    fun isCurrentTimeBetween(startTime: String, endTime: String): Boolean {
        val currentTime = LocalTime.now()

        val start = LocalTime.parse(startTime)
        val end = LocalTime.parse(endTime)
        System.out.println("#### isCurrentTimeBetween  "+currentTime+"   "+start+"  "+end )

        // If the end time is earlier than the start time, it means it spans over midnight
         if (start.isBefore(end) &&  currentTime.isAfter(start) && currentTime.isBefore(end)) {
            return true
        } else{
            return false
        }
    }


    fun convertEventModelToModel(eventModel: EventListRes.Event,isEdit:Boolean, isCopyTickets:Boolean = true){

        val images = arrayOfNulls<String>(eventModel.images!!.size)
        for (i in 0 until eventModel.images.size) {
            images[i] = eventModel.images[i].image
        }

        val sendTicket: ArrayList<TicketBook> = ArrayList()
        // is isCopyTickets False then don't copy old tickets
        if(isCopyTickets) {
            for (i in 0 until eventModel.tickets!!.size) {
                sendTicket.add(
                    TicketBook(
                        name = eventModel.tickets[i].name,
                        price = "" + eventModel.tickets[i].price,
                        quantity = "" + eventModel.tickets[i].quantity,
                        booking_type = "" + eventModel.tickets[i].ticket_type,
                        position = 0,
                        isNew = !isEdit

                    )
                )
            }
        }

       createEventModel = CreateEventModel(
            eventName = eventModel.name!!,
            venueName= eventModel.venue!!,
            "",
            age = eventModel.age!!,
            description =  eventModel.description!!,
            imagePath = images,
            address =  eventModel.address!!,
            city = eventModel.city!!,
            postCode =  eventModel.postal_code!!,
            startDateTime = convertDateInFormat(
                eventModel.start_date_time!!,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmYyyyHhMmA
            )!!,
            endDateTime = convertDateInFormat(
                eventModel.end_date_time!!,
                Constant().yyyyMmDdHhMmSs,
                Constant().ddMmYyyyHhMmA
            )!!,
            music = eventModel.music!!,
            entertainment =  eventModel.entertainment!!,
            category = eventModel.category ?: "",
            dressCode =  eventModel.dress_code!!,
            entryPolicy =  eventModel.entry_policy!!,
            lastEntryPolicy =  eventModel.last_entry!!,
            totalCapacity =  eventModel.total_capacity,
            is_public =  eventModel.is_public,
            latitude =  "" + eventModel.lat,
            longitude =  "" + eventModel.longi,
            eventMusicName= eventModel.music_djlineup!!,
            eventEntertainmentNamr="",
            otherInfo= eventModel.other_information ?: "",
            ticketBooks =  sendTicket,
            isBasicInfoVerified = true,
            isAddressVerfied = true,
            isEventTimeVerified = true,
            isOtherInfoVerified = true,
            is_draft = eventModel.is_draft,
            id = eventModel.id

        )
    }

    fun copyQrCode(context: Context,qrId:String){
        val clipboard: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(Constant().label, "QR ID: "+ qrId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, context.getString(R.string.txt_code_code_copied), Toast.LENGTH_SHORT).show()
    }
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String = "image_${System.currentTimeMillis()}.png") {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            resolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

    }
    fun getFileSize(context: Context,uri: Uri): Long {
        var size: Long = 0
        try {
            val cursor = context!!.contentResolver.query(uri, null, null, null, null)
            cursor?.apply {
                moveToFirst()
                val columnIndex = getColumnIndex(OpenableColumns.SIZE)
                size = getLong(columnIndex)
                close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }


    fun isValidDateRange(startDate: String, endDate: String,patternDate:String): Boolean {
        val dateFormat = SimpleDateFormat(patternDate, Locale.getDefault())

        return try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)
            start == end || start.before(end)  // Returns true if start is before end
        } catch (e: Exception) {
            false  // Return false if parsing fails
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isValidUserTime(userTime: String, userTimeFormat: String): Boolean {
        return try {
            println("Parsing: $userTime with format: $userTimeFormat")
            val formatter = DateTimeFormatter.ofPattern(userTimeFormat, Locale.ENGLISH)

            val userDateTime = LocalDateTime.parse(userTime, formatter)
            val currentDateTime = LocalDateTime.now()

            System.out.println(userDateTime.toString())
            System.out.println(currentDateTime.toString())
            currentDateTime.isBefore(userDateTime)
        } catch (e: DateTimeParseException) {
            println("Error: ${e.message}")
            false
        }
    }



}