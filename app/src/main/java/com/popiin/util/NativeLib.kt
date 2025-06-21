package com.popiin.util

object NativeLib {
    init {
        System.loadLibrary("native-lib") // Ensure this matches your C++ library
    }

    external fun getBaseUrl(isDebug: Boolean): String
}