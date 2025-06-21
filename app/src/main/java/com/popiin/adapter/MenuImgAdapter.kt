package com.popiin.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.popiin.R
import com.popiin.databinding.DialogLoaderBinding
import com.popiin.databinding.MenuViewpagerBinding
import com.popiin.util.Constant


class MenuImgAdapter(private var imgList: ArrayList<String>) : PagerAdapter() {
    lateinit var progressDialog: Dialog
    private lateinit var loaderBinding: DialogLoaderBinding
    override fun getCount(): Int {
        return imgList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: MenuViewpagerBinding = DataBindingUtil.inflate(LayoutInflater.from(container.context),
            R.layout.menu_viewpager,
            container, false)
        loaderBinding = DataBindingUtil.inflate(LayoutInflater.from(container.context),
            R.layout.dialog_loader,
            null,
            false)

        progressDialog = initProgress(loaderBinding,container)
        binding.webView.settings.javaScriptEnabled = true

        if (imgList[position].contains(Constant().pdfCaps)) {
            binding.loader.show()

            binding.webView.visibility = View.VISIBLE
            binding.imageview.visibility = View.GONE

            binding.webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if (view.title == "") {
                        view.reload()
                    } else {
                        binding.loader.hide()
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()
                        }
                        binding.webView.loadUrl(Constant().menuImgUrl)
                    }
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressDialog.show()
                    binding.webView.loadUrl(Constant().menuImgUrl)
                }
            }

            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.loadWithOverviewMode = true
            binding.webView.settings.useWideViewPort = true
            binding.webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+imgList[position])
        }else{

            binding.loader.hide()
            binding.webView.visibility = View.GONE
            binding.imageview.visibility = View.VISIBLE

            Glide.with(container.context).load(imgList[position]).into(binding.imageview)
        }


        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun initProgress(
        dialogLoaderBinding: DialogLoaderBinding,
        container: ViewGroup,
    ): Dialog {
        val progressDialog =
            Dialog(container.context, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window!!.setGravity(Gravity.CENTER)
        progressDialog.window!!.setDimAmount(0.0f)
        progressDialog.setContentView(dialogLoaderBinding.root)
        return progressDialog
    }
}