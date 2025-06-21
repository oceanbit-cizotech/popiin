package com.popiin.exceptions

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No Internet Connection. Please check your network."
}