package com.popiin.util

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri

class Constant{
    // val yyyyMMDDHHMMSS: String ="yyyy-MM-dd HH:mm:ss"
    val eeee: String = "EEEE"
    val ticket: String = "Ticket"
    val table: String = "Table"
    val mmmDdYyyy: String = "MMM dd, yyyy"
    val item: String = "item"
    val itemList: String = "itemList"
    val stories: String = "stories"
    val exploreItem: String = "exploreItem"
    val Offers: String = "Offers"

    // val venueItem: String = "venue_item"
    val yyyyMmDd = "yyyy-MM-dd"
    val ddMmmYyyy = "dd MMM yyyy"
    val ddMmmYyyySlash = "dd/MM/yyyy"
    val ddMmmYyyyDash = "dd-MM-yyyy"
    val eeeDdMmmYyyy = "EEE, dd MMM yyyy"
    val eeeDdMmmYyyySpace = "EEE dd MMM yyyy"
    val hhMmA = "hh:mma"
    val hhMM = "HH:mm"
    val HHmmss24hrs = "HH:mm:ss"
    val ddMmYyyyHhMmA = "dd MMM yyyy hh:mma"
    val yyyyMmDdHhMmSs = "yyyy-MM-dd HH:mm:ss"
    val yyyyMmDdHhMmSsCamel = "yyyy-MM-dd HH:mm:ss"
    val yyyyMmDdHhMmA = "yyyy-MM-dd hh:mma"
    val ddMmm = "dd MMM"
    val hh = "hh"
    val mm = "mm"
    val a = "a"
    val music = "Music"
    val entertainment = "Entertainment"
    val tags = "Tags"
    var imageList = "image"
    var pdfCaps = "pdf"
    var otherConst = "Other"
    var barConst = "Bar"
    var pubConst = "Pub"
    var hotelConst = "Hotel"
    var restaurantConst = "Restaurant"
    var nightClub = "Nightclub"
    var cafeConst = "Café"
    var cafeRoman = "Caf\u00e9"

    var bar = "bar"
    var pub = "pub"
    var restaurant = "restaurant"
    var nightclub = "nightclub"
    var cafe = "café"

    var venueOfferOne = "Happy hour drinks / cocktails for…"
    var venueOfferTwo = "2-for-1 drinks / cocktails…"
    var venueOfferThree = "Selected beers / wines for…"
    var venueOfferFour = "50% off food / drinks…"
    var venueOfferFive = "30% off food / drinks…"
    var venueOfferSix = "20% off food / drinks…"
    var venueOfferSeven = "Other"

    var monday = "Monday"
    var tuesday = "Tuesday"
    var wednesday = "Wednesday"
    var thurdsay = "Thursday"
    var friday = "Friday"
    var saturday = "Saturday"
    var sunday = "Sunday"

    var oneWeek = "One Week"
    var oneMonth = "One Month"
    var threeMonth = "Three Month"
    var sixMonth = "Six Month"
    var oneYear = "One Year"

    var noRestrictions = "No Restrictions"

    // var selectNumber = "Select Number"
    var one = "1"
    var two = "2"
    var three = "3"
    var four = "4"
    var five = "5"
    var six = "6"
    var seven = "7"
    var eight = "8"
    var nine = "9"
    var ten = "10"
    var other = "Other"

    var sixteen = "16+"
    var eighteen = "18+"
    var twentyOne = "21+"
    var noAgeRestriction = "No age restriction"

    //    val scanCodes = "scancodes"
//    val eventId = "event_id"
    val eventName = "event_name"
    val startTime = "start_time"
    val endTime = "end_time"
    val price = "price"
    val shareVia = "Share via"
    val share = "Share"
    val sharePattern = "text/plain"

    //    val codeType = "code_type"
//    val ticketType = "ticket_type"
//    val bookingTime = "booking_time"
    val path = "path"
    val image = "image"
    val video = "video"
    val audio = "audio"
    val auto = "auto"
    val camera = "camera"
    val pdf = "pdf"
    val primary = "primary"
    val file = "file"
    val content = "content"
    val id = "_id=?"
    val idConst = "id"
    val contentUri = "content://downloads/public_downloads"
    val selectPicture = "Select Picture"
    val applicationPdf = "application/pdf"
    val invoicePdf = "invoice.pdf"
    val shareLink = "https://ymr83.app.link/6jJCjOyFYY"
    val eventNameHeader = "Event name:"
    val venue = "Venue:"
    val address = "Address:"
    val description = "Description:"
    val firstName = "first_name"
    val lastName = "last_name"
    val email = "email"
    val gender = "gender"
    val dob = "dob"
    val password = "password"
    val lang = "lang"
    val avatar = "avatar"
    val gujarat = "Gujarat"
    val gj = "GJ"
    val multipartFormData = "multipart/form-data"
    val label = "label"
    val gps = "gps"
    val network = "network"
    val bearer = "Bearer"
    val hash = "####"
    val hour = "hour"
    val hours = "hours"
    val minutes = "minutes"
    val close = "Close"
    val open = "Open"
    val miles = "miles"
    val menuImgUrl =
        "javascript:(function() { " + "document.querySelector('[role=\"toolbar\"]').remove();})()"
    val deselectAll = "Deselect all"
    val selectAll = "Select all"
    val mapPackage = "com.google.android.apps.maps"
    val drivePath = "com.google.android.apps"
    val position: String = "position"
    val distance: String = "Distance:"
    val driving: String = "driving"
    val walking: String = "walking"
    val transit: String = "transit"
    val user: String = "user"
    val owner: String = "owner"
    val en: String = "en"
    val venues: String = "venues"
    val events: String = "events"
    val venueName: String = "venue_name"
    val city: String = "city"
    val postcode: String = "postcode"
    val addressVerify: String = "address"
    val companyName: String = "company_name"
    val venueId: String = "venue_id"
    val comments: String = "comments"
    val personalDoc: String = "personal_doc"
    val venueDoc: String = "venue_doc"
    val whatson: String = "whatson"
    val venueBooking:Int=1
    val eventBooking:Int=0
    val whatsOnBooking:Int=2

    @SuppressLint("AuthLeak")
    val cloudinaryUrl = "cloudinary://998665658329649:1du65hnx9qyLyeLfywvOtQXOscM@devomvmjs"

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }


    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }

    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection,
                selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}