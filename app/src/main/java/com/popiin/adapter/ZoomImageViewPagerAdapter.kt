package com.popiin.adapter

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.popiin.R
import com.popiin.databinding.AdapterZoomImageViewPagerBinding
import com.popiin.databinding.DialogLoaderBinding
import java.util.*

class ZoomImageViewPagerAdapter(private val list: List<String>) : PagerAdapter() {

    private var progressDialog: Dialog? = null

    override fun getCount(): Int = list.size

    override fun isViewFromObject(@NonNull view: View, @NonNull obj: Any): Boolean {
        return view === obj
    }

    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
        val binding: AdapterZoomImageViewPagerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.adapter_zoom_image_view_pager,
            container,
            false
        )

        val currentUrl = list[position]
        binding.imageUrl = currentUrl

        if (progressDialog == null) {
            progressDialog = initProgress(container)
        }

        if (currentUrl.lowercase(Locale.ROOT).contains("pdf")) {
            loadPdf(binding, currentUrl)
        } else {
            loadImage(binding, currentUrl)
        }

        container.addView(binding.root)
        return binding.root
    }

    private fun loadPdf(binding: AdapterZoomImageViewPagerBinding, url: String) {
        binding.loader.show()
        binding.webView.apply {
            visibility = View.VISIBLE
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    binding.loader.hide()
                    dismissProgressDialog()
                    hideWebViewToolbar(view)
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    showProgressDialog()
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest?,
                    error: WebResourceError?,
                ) {
                    Toast.makeText(view.context, "Error loading PDF", Toast.LENGTH_SHORT).show()
                }
            }
            loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$url")
        }
        binding.myZoomageView.visibility = View.GONE
    }

    private fun loadImage(binding: AdapterZoomImageViewPagerBinding, url: String) {
        dismissProgressDialog()
        binding.loader.hide()
        binding.webView.visibility = View.GONE
        binding.myZoomageView.apply {
            visibility = View.VISIBLE
            Glide.with(context)
                .load(url)
                .into(this)
        }
    }

    private fun hideWebViewToolbar(view: WebView) {
        view.loadUrl("javascript:(function() { document.querySelector('[role=\"toolbar\"]').remove(); })()")
    }

    private fun showProgressDialog() {
        progressDialog?.takeIf { !it.isShowing }?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull obj: Any) {
        container.removeView(obj as View)
        Glide.with(container.context).clear(obj)
    }

    private fun initProgress(container: ViewGroup): Dialog {
        val dialog = Dialog(container.context, R.style.Theme_AppCompat_Dialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(
                DataBindingUtil.inflate<DialogLoaderBinding>(
                    LayoutInflater.from(container.context),
                    R.layout.dialog_loader,
                    null,
                    false
                ).root
            )
        }
        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
            setDimAmount(0.0f)
        }
        return dialog
    }
}
