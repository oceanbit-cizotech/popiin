package com.popiin

import com.popiin.req.*
import com.popiin.res.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @POST("api/dologin")
    fun doLogin(@Body loginReq: LoginReq): Call<LoginRes>

    @POST("api/signup")
    fun doRegistration(@Body doSignupReq: SignUpReq): Call<LoginRes>

    @POST("api/forgotpassword")
    fun doForgotPassword(@Body doVerifyEmailReq: ForgotPasswordReq?): Call<CommonRes?>?

    @POST("api/user")
    fun doGetUser(
        @Header("Authorization") token: String?,
    ): Call<LoginRes>

    @POST("api/eventlist")
    fun getEventList(
        @Header("Authorization") token: String?,
        @Body eventReq: EventReq?,
    ): Call<EventListRes?>?

    @POST("api/venuelist")
    fun getVenueList(@Body venuesReq: VenuesReq?): Call<VenueListRes?>?

    @POST("/api/eventdetails")
    fun postEventDetails(
        @Header("Authorization") token: String,
        @Body req: EventDetailsReq,
    ): Call<EventDetailsRes>?

    @POST("/api/venue-details")
    fun postVenueDetails(
        @Header("Authorization") token: String?,
        @Body req: VenueDetailsReq?,
    ): Call<VenueDetailsRes?>?

//    @POST("/api/rate-venue-event")
//    fun postRateVenueEvent(
//        @Header("Authorization") token: String?,
//        @Body req: RateVenueEventReq?,
//    ): Call<CommonRes?>?

    @POST("api/mark-favourite")
    fun marFavourite(
        @Header("Authorization") token: String?,
        @Body eventReq: MarkFavouriteReq?,
    ): Call<CommonRes?>?

    @POST("/api/mybookings")
    fun getMyBookings(
        @Header("Authorization") token: String?,
        @Body req: MyBookingReq?,
    ): Call<MyBookingRes?>?

    @GET("api/myvenuebookings?")
    fun getVenueAttendeesList(
        @Header("Authorization") token: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Call<VenueAttendeesRes?>?

    @POST("/api/eventlist-business")
    fun doGetEventList(
        @Header("Authorization") token: String?,
        @Body req: EventReq?,
    ): Call<EventListRes?>?

    @POST("/api/venuelist-business")
    fun doGetVenuesList(
        @Header("Authorization") token: String?,
        @Body req: EventReq?,
    ): Call<VenueListRes?>?

    @POST("/api/delete-venue")
    fun doDeleteVenues(
        @Header("Authorization") token: String?, @Body req: DeleteVenuesReq?,
    ): Call<CommonRes?>?

    @POST("/api/delete-event")
    fun doDeleteEvent(
        @Header("Authorization") token: String?, @Body req: DeleteEventsReq?,
    ): Call<CommonRes?>?

    @POST("api/update-profile")
    fun doUpdateProfile(
        @Header("Authorization") token: String?,
        @Body multipartBody: MultipartBody?,
    ): Call<LoginRes>

    @Multipart
    @POST("api/eventimage")
    fun getEventImage(
        @Part("Authorization") token: String?,
        @Part image: MultipartBody.Part?,
    ): Call<ImageRes?>?

    @GET("api/notification-list?")
    fun getNotificationsList(
        @Header("Authorization") token: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Call<NotificationsRes?>?

    @POST("api/add-help")
     fun doAddHelp(@Header("Authorization") token: String?,@Body addHelpReq: AddHelpReq) : Call<CommonRes>

    @POST("api/add-partnership")
    fun doAddPartnership(@Header("Authorization") token: String?,@Body addHelpReq: AddHelpReq): Call<CommonRes>

    @POST("/api/createevent")
    fun doCreateEvent(
        @Header("Authorization") token: String?,
        @Body req: CreateEventReq?,
    ): Call<CreateEventRes?>?

    @GET("json")
    fun getLocationDetails(
        @Query("address") location: String?,
        @Query("key") key: String?,
    ): Call<AutoCompateSearchRes?>?

    @POST("/api/update-link")
    fun postUpdateLink(
        @Header("Authorization") token: String?,
        @Body req: UpdateLinkReq?,
    ): Call<CommonRes?>?

    @POST("/api/edit-event")
    fun doEditEvent(
        @Header("Authorization") token: String?,
        @Body req: EditEventReq?,
    ): Call<EditEventRes?>?

    @POST("/api/update-event")
    fun doUpdateEvent(
        @Header("Authorization") token: String?,
        @Body req: EditEventReq?,
    ): Call<EditEventRes?>?

    @POST("/api/createvenue")
    fun doCreateVenue(
        @Header("Authorization") token: String?,
        @Body req: CreateVenuesReq?,
    ): Call<CreateVenueRes?>?

    @POST("/api/update-venue-link")
    fun postUpdateVenueLink(
        @Header("Authorization") token: String?,
        @Body req: UpdateVenueReq?
    ): Call<CommonRes?>?

    @POST("/api/edit-venue")
    fun doEditVenue(
        @Header("Authorization") token: String?,
        @Body req: EditVenueReq?,
    ): Call<EditVenueRes?>?

    @POST("/api/update-venue")
    fun doUpdateVenue(
        @Header("Authorization") token: String?,
        @Body req: EditVenueReq?,
    ): Call<EditVenueRes?>?

    @POST("/api/venue-reservation-enabled")
    fun doVenueReserve(
        @Header("Authorization") token: String?,
        @Body venueReserveReq: VenueReserveReq?,
    ): Call<VenueReserveRes?>?

    @POST("/api/auto-venue-confirmation")
    fun doAutoVenueConfirm(
        @Header("Authorization") token: String?,
        @Body venueReserveReq: VenueReserveReq?,
    ): Call<VenueReserveRes?>?

    @POST("api/get-venue-earnings")
    fun doGetVenueEarnings(
        @Header("Authorization") token: String?,
        @Body earningsReq: EarningsReq,
    ): Call<EarningRes>

    @POST("api/get-whatson-earnings")
    fun doGetWhatsonEarnings(
        @Header("Authorization") token: String?,
        @Body earningsReq: EarningsReq,
    ): Call<WhatsonEarningRes>

    @POST("/api/get-earnings")
    fun doGetEarnings(
        @Header("Authorization") token: String?,
        @Body earningsReq: EarningsReq,
    ): Call<EarningRes>


    @POST("/api/get-paid")
    fun doGetPaid(
        @Header("Authorization") token: String?, @Body req: PaidReq?,
    ): Call<CommonRes?>?

    @POST("/api/get-venue-paid")
    fun doGetVenuePaid(
        @Header("Authorization") token: String?,
        @Body paidVenueReq: PaidVenueReq?,
    ): Call<VenuePaidRes?>?

    @POST("/api/venuewhats-list")
    fun getWhatsOnListFromVenue(
        @Header("Authorization") token: String?,
        @Body req: VenueDetailsReq,
    ): Call<VenueWhatsListRes>

    @POST("/api/venueoffer-list")
    fun getVenueOfferList(
        @Header("Authorization") token: String?,
        @Body req: VenueDetailsReq?,
    ): Call<VenueOfferRes?>?

    @POST("/api/createvenue-offer")
    fun doCreateVenueoffer(
        @Header("Authorization") token: String?,
        @Body req: CreateVenueOfferReq?,
    ): Call<CreateOfferRes?>?

    @POST("/api/venueoffer-update")
    fun doVenueofferUpdate(
        @Header("Authorization") token: String?,
        @Body req: UpdateVenueOfferReq?,
    ): Call<CreateOfferRes?>?

    @POST("/api/venueoffer-delete")
    fun doVenueofferDelete(
        @Header("Authorization") token: String?,
        @Body req: VenueofferDeleteReq?,
    ): Call<CommonRes?>?

    @POST("/api/venueoffer-update-sub")
    fun doVenueOfferUpdateSub(
        @Header("Authorization") token: String?,
        @Body req: VenueOfferUpdateSubReq?,
    ): Call<VenueOfferRes?>?

    @POST("/api/make-venue-offer-live")
    fun doMakeVenueOfferLive(
        @Header("Authorization") token: String?,
        @Body req: MakeVenueOfferLiveReq?,
    ): Call<CommonRes?>?

    @POST("/api/bookevent")
    fun getBookEvent(
        @Header("Authorization") token: String?,
        @Body req: BookEventReq?,
    ): Call<BookEventRes?>?

    @POST("/api/validate-promo")
    fun getValidatePromo(
        @Header("Authorization") token: String?,
        @Body req: ValidatePromoReq?,
    ): Call<ValidatePromoRes?>?

    @POST("/api/event-attendes-business")
    fun getEventBookingAttendees(
        @Header("Authorization") token: String?, @Body req: EventAttendesBusinessReq?,
    ): Call<EventBookingAttendeesRes?>?

    @POST("/api/createvenue-whatson")
    fun doCreateWhatsOn(
        @Header("Authorization") token: String?,
        @Body req: CreateWhatsOnReq?,
    ): Call<CommonRes?>?

    @POST("/api/venuewhats-update")
    fun doUpdateWhatsOn(
        @Header("Authorization") token: String?,
        @Body req: CreateWhatsOnReq?,
    ): Call<CommonRes?>?

    @POST("/api/venuewhats-delete")
    fun getVenuewhatsDelete(
        @Header("Authorization") token: String?,
        @Body req: VenueWhatsDeleteReq?,
    ): Call<CommonRes?>?

    @POST("/api/event-venues")
    fun doGetEventVenues(
        @Header("Authorization") token: String?,
    ): Call<GetEventVenueRes?>?

    @POST("/api/venuewhats-list-business")
    fun doGetVenueWhatsListBusiness(
        @Header("Authorization") token: String?,
    ): Call<WhatsOnListForSalesReportRes?>?

    @POST("/api/sales-report")
    fun doGetSalesReport(
        @Header("Authorization") token: String?,
        @Body req: SalesReportReq?,
    ): Call<SalesReportRes?>?

    @GET("api/venue-attendes-business?")
    fun getVenueBookAttendeesList(
        @Header("Authorization") token: String?,
        @Query("venue_id") venue_id: Int,
    ): Call<VenueBookingAttendeeRes?>?

    @GET("api/venuewhats-attendes-business")
    fun getWhatsOnBookAttendeesList(
        @Header("Authorization") token: String?,
        @Query("venue_id") venue_id: Int,
    ): Call<WhatsOnBookingAttendeesRes?>?

    @POST("/api/add-venue-tickets")
    fun doCreateVenueTickets(
        @Header("Authorization") token: String?,
        @Body venueTicketsReq: CreateVenueTicketsReq?,
    ): Call<CreateVenueTicketsRes?>?

    @POST("/api/update-venue-tickets")
    fun doUpdateVenueTickets(
        @Header("Authorization") token: String?,
        @Body venueTicketsReq: CreateVenueTicketsReq?,
    ): Call<UpdateVenueTicketRes?>?

    @POST("/api/favourites")
    fun getFavouriteEvents(
        @Header("Authorization") token: String?,
        @Body req: CommonReq?,
    ): Call<FavouriteEventsRes?>?

    @POST("/api/favourite-venues")
    fun getFavouriteVenue(
        @Header("Authorization") token: String?,
        @Body req: FavouriteVenueReq?,
    ): Call<VenueListRes?>?

    @POST("/api/bookvenue")
    fun getBookVenue(
        @Header("Authorization") token: String?,
        @Body req: BookVenueReq?,
    ): Call<VenueBookRes?>?

    @POST("/api/make-special-request")
    fun getMakeSpecialRequest(
        @Header("Authorization") token: String?,
        @Body req: MakeSpecialRequestReq?,
    ): Call<CommonRes?>?

    @POST("/api/business-payment")
    fun doBusinessPayment(
        @Header("Authorization") token: String?, @Body req: BusinessPaymentReq?,
    ): Call<CommonRes?>?

    @POST("/api/delete-account")
    fun doDeleteAccount(
        @Header("Authorization") token: String?,
    ): Call<CommonRes?>?

    @POST("/api/scancode")
    fun postScanCodeEvent(
        @Header("Authorization") token: String?,
        @Body req: ScanCodeEventReq?,
    ): Call<CommonRes?>?

    @POST("/api/scan-offer-code")
    fun doScanOfferCode(
        @Header("Authorization") token: String?,
        @Body req: ScanOfferCodeReq?,
    ): Call<CommonRes?>?

    @POST("api/delete-notification")
    fun doDeleteNotification(
        @Header("Authorization") token: String?,
        @Body deleteNotificationReq: DeleteNotificationReq?,
    ): Call<DeleteNotificationRes?>?

    @POST("api/venuewhats-ticket-save")
    fun createWhatsOnReservation(
        @Header("Authorization") token: String?,
        @Body createWhatsonReserveReq: CreateWhatsOnReserveReq?,
    ): Call<CreateWhatsonReserveRes?>?

    @POST("api/venuewhats-ticket-update")
    fun updateWhatsOnReservation(
        @Header("Authorization") token: String?,
        @Body createWhatsonReserveReq: CreateWhatsOnReserveReq?,
    ): Call<CreateWhatsonReserveRes?>?

    @POST("api/venuewhats-ticket-delete?")
    fun deleteWhatsOnReservation(
        @Header("Authorization") token: String?,
        @Query("ticket_id") ticket_id: Int,
        @Query("venue_id") venue_id: Int,
    ): Call<CommonRes?>?

    @POST("api/mywhatsonbooking")
    fun myWhatsOnBooking(
        @Header("Authorization") token: String?,
        @Body myBookingReq: MyBookingReq,
    ): Call<MyWhatsonBookRes?>?

    @POST("api/venuewhats-ticket-book")
    fun doWhatsTicketBook(
        @Header("Authorization") token: String?,
        @Body myWhatsOnReq: BookWhatsOnReq,
    ): Call<WhatsOnBookRes?>?

    @POST("api/venuewhats-ticket-list?")
    fun getWhatsOnTicketList(
        @Header("Authorization") token: String?,
        @Query("whatson_id") whatson_id: Int,
    ): Call<WhatsOnReserveListRes?>?

    @POST("api/venue-story?")
    fun getStoryList(
        @Header("Authorization") token: String?,
        @Query("venue_id") venue_id: Int,
    ): Call<VenueStoryListRes?>?

    @POST("api/venue-list-story?")
    fun getVenueStoryList(
        @Header("Authorization") token: String?,
        @Query("lat") lat: Double,
        @Query("longi") longi: Double,
        @Query("distance") distance: Int,
    ): Call<VenueStoryListRes?>?

    @POST("api/venue-add-story")
    fun addVenueStory(
        @Header("Authorization") token: String?,
        @Body addStoryReq: AddStoryReq,
    ): Call<AddVenueStoryRes?>?

    @POST("api/venue-delete-story?")
    fun deleteVenueStory(
        @Header("Authorization") token: String?,
        @Query("id") id: Int,
    ): Call<CommonRes?>?

    @GET("json")
    fun doSearch(
        @Query("query") search: String?,
        @Query("key") key: String?,
    ): Call<PlaceSearchRes?>?

    @GET("json?")
    fun doAddressSearch(
        @Query("query") search: String?,
        @Query("location") location: String?,
        @Query("key") key: String?,
    ): Call<AddressSearchRes?>?

    @GET("json?")
    fun doAddressDetails(
        @Query("placeid") search: String?,
        @Query("key") key: String?,
    ): Call<SearchAddressDetailRes?>?

    @GET("json?")
    fun getDirections(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("mode") mode: String?,
        @Query("key") key: String?,
    ): Call<GoogleDirectionRes?>?

    @POST("api/cancel-whatson-booking-user")
    fun cancelBooking(
        @Header("Authorization") token: String?,
        @Body cancelBookingReq: CancelBookingReq,
    ): Call<CancelBookingRes?>?

    @POST("api/cancel-venue-booking-user")
    fun cancelVenueBooking(
        @Header("Authorization") token: String?,
        @Body cancelBookingReq: CancelBookingReq,
    ): Call<CancelBookingRes?>?

    @POST("api/confirm-venue-booking")
    fun confirmVenueBooking(
        @Header("Authorization") token: String?,
        @Body confirmVenueBookReq: ConfirmVenueBookReq,
    ): Call<ConfirmVenueBookRes?>?

    @POST("api/sendverificationemail")
    fun sendVerificationEmail(
        @Header("Authorization") token: String?,
        @Body resendEmailReq: ResendEmailReq,
    ): Call<CommonRes?>?

    @POST("api/verifyaccount")
    fun verifyAccount(
        @Header("Authorization") token: String?,
        @Body verifyAccountReq: VerifyAccountReq,
    ): Call<CommonRes?>?

    @POST("api/submit-document")
    fun submitDocument(
        @Header("Authorization") token: String?,
        @Body multipartBody: MultipartBody,
    ): Call<SubmitDocRes?>?

    @GET("api/create-stripe-link")
    fun createStripeLink(
        @Header("Authorization") token: String?,
    ): Call<CreateStripeLinkRes?>?

    @POST("api/list-venue-trend")
    fun getListVenueTrend(
        @Header("Authorization") token: String?,
        @Body req: VenueDetailsReq?,
        ): Call<VenueTrendRes>

    @GET("api/list-trend")
    fun getListOfTrend(
        @Header("Authorization") token: String?,
    ): Call<TrendRes>

    @POST("api/create-trend")
    fun doCreateTrend(
        @Header("Authorization") token: String?,
        @Body req:CreateTrendReq
    ): Call<CreateTrendRes>

    @POST("api/delete-trend")
    fun doDeleteTrend(
        @Header("Authorization") token: String?,
        @Body req:DeleteTrendReq
    ): Call<CreateTrendRes>

    @POST("api/update-trend")
    fun doUpdateTrend(
        @Header("Authorization") token: String?,
        @Body req:CreateTrendReq
    ): Call<CreateTrendRes>

    @GET("api/config")
    fun getConfig(): Call<ConfigRes>
}