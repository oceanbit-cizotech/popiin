package com.popiin.res

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateVenueTicketsRes {
    @Expose
    @SerializedName("data")
    var data: List<VenueListRes.Tickets>? = null

    @Expose
    @SerializedName("message")
    var message: String? = null

    //    public static class Data {
    @Expose
    @SerializedName("success")
    var isSuccess = false
    //        @Expose
    //        @SerializedName("created_at")
    //        private String created_at;
    //        @Expose
    //        @SerializedName("status")
    //        private int status;
    //        @Expose
    //        @SerializedName("is_deposite_required")
    //        private int is_deposite_required;
    //        @Expose
    //        @SerializedName("available_quantity")
    //        private int available_quantity;
    //        @Expose
    //        @SerializedName("quantity")
    //        private int quantity;
    //        @Expose
    //        @SerializedName("end_time")
    //        private String end_time;
    //        @Expose
    //        @SerializedName("start_time")
    //        private String start_time;
    //        @Expose
    //        @SerializedName("name")
    //        private String name;
    //        @Expose
    //        @SerializedName("price")
    //        private int price;
    //        @Expose
    //        @SerializedName("venue_id")
    //        private int venue_id;
    //
    //        public String getCreated_at() {
    //            return created_at;
    //        }
    //
    //        public int getStatus() {
    //            return status;
    //        }
    //
    //        public int getIs_deposite_required() {
    //            return is_deposite_required;
    //        }
    //
    //        public int getAvailable_quantity() {
    //            return available_quantity;
    //        }
    //
    //        public int getQuantity() {
    //            return quantity;
    //        }
    //
    //        public String getEnd_time() {
    //            return end_time;
    //        }
    //
    //        public String getStart_time() {
    //            return start_time;
    //        }
    //
    //        public String getName() {
    //            return name;
    //        }
    //
    //        public int getPrice() {
    //            return price;
    //        }
    //
    //        public int getVenue_id() {
    //            return venue_id;
    //        }
    //    }
}