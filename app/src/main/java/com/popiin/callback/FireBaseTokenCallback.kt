package com.popiin.callback

abstract class FireBaseTokenCallback {
    open fun onTokenGenerateSuccess(firebaseToken:String) {}
}