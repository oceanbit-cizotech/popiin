package com.popiin.req

class EventReq {
    var limit: Int
    var search: String
    var page: Int
    var filter: String? = null
    var from_date: String? = null
    var to_date: String? = null

    constructor(limit: Int, search: String, page: Int) {
        this.limit = limit
        this.search = search
        this.page = page
    }

    constructor(limit: Int, search: String, page: Int, filter: String?) {
        this.limit = limit
        this.search = search
        this.page = page
        this.filter = filter
    }

    constructor(
        limit: Int,
        search: String,
        page: Int,
        filter: String?,
        from_date: String?,
        to_date: String?
    ) {
        this.limit = limit
        this.search = search
        this.page = page
        this.filter = filter
        this.from_date = from_date
        this.to_date = to_date
    }
}