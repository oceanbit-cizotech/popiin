package com.popiin.activity

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import com.popiin.BaseActivity
import com.popiin.R
import android.webkit.WebViewClient
import android.webkit.WebView
import com.popiin.databinding.ActivityWebViewBinding
import com.popiin.util.Constant

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {
    var path: String? = ""
    override fun getLayout(): Int {
        return R.layout.activity_web_view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        path = intent.extras!!.getString(Constant().path)
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.webViewClient = AppWebViewClients()
        binding.webview.loadUrl("https://docs.google.com/viewer?url=$path")
        binding.imgBack.setOnClickListener { finish() }
    }

    inner class AppWebViewClients : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            view!!.loadUrl(request!!.url.toString())
            return true

        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }
}