package com.popiin.req

class CommonReq {
    var limit: String? = null
    var page: String? = null

    constructor() {}
    constructor(limit: String?, page: String?) {
        this.limit = limit
        this.page = page
    }
}