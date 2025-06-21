package com.popiin.util

import android.content.Context
import android.graphics.*
import android.text.Layout
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlin.Throws
import com.google.zxing.WriterException
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.BarcodeFormat
import android.text.TextPaint
import android.text.StaticLayout
import android.text.TextUtils
import androidx.core.content.ContextCompat
import java.util.*

class QrGenerator private constructor(builder: Builder) {
    private val mQrSize: Int
    private val mColor: Int
    private val mMargin: Int
    private val mBgColor: Int
    private val mOverlaySize: Int
    private val mOverlayAlpha: Int
    private val mContent: String?
    private val mFootNote: String?
    private val mOverlay: Bitmap?
    private val mEcl: ErrorCorrectionLevel
    private val mXForMode: PorterDuff.Mode
    @Throws(WriterException::class)
    private fun createQRCode(): Bitmap {
        val hints = Hashtable<EncodeHintType, Any?>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = mEcl
        hints[EncodeHintType.MARGIN] = mMargin
        val matrix = MultiFormatWriter().encode(mContent,
            BarcodeFormat.QR_CODE, mQrSize, mQrSize, hints)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix[x, y]) {
                    pixels[y * width + x] = mColor
                } else {
                    pixels[y * width + x] = mBgColor
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height,
            Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        //draw overlay
        if (mOverlay != null && mOverlaySize > 0) {
            val w = Bitmap.createBitmap(mOverlay)
            val o = w.copy(Bitmap.Config.ARGB_8888, true)
            w.recycle()
            val overlayW = o.width
            val overlayH = o.height
            val scaledH = mOverlaySize * overlayW / overlayH
            val offsetX = (mQrSize - mOverlaySize) / 2
            val offsetY = (mQrSize - scaledH) / 2
            val p = Paint(Paint.FILTER_BITMAP_FLAG)
            p.alpha = mOverlayAlpha
            p.xfermode = PorterDuffXfermode(mXForMode)
            val canvas = Canvas(bitmap)
            val src = Rect(0, 0, overlayW, overlayH)
            val dst = Rect(0, 0, mOverlaySize, scaledH)
            canvas.translate(offsetX.toFloat(), offsetY.toFloat())
            canvas.drawBitmap(o, src, dst, p)
        }

        //draw enlarge the canvas and add footnote
        if (!TextUtils.isEmpty(mFootNote)) {
            val result = Bitmap.createBitmap(mQrSize, mQrSize * 3 / 2, Bitmap.Config.ARGB_8888)
            val textPaint = TextPaint()
            textPaint.color = mColor
            textPaint.textSize = 20f
            textPaint.isAntiAlias = true
            @Suppress("DEPRECATION") val mTextLayout = StaticLayout(
                mFootNote,
                textPaint,
                mQrSize,
                Layout.Alignment.ALIGN_CENTER,
                1.4f, 0.2f, false)
            val canvas = Canvas(result)
            canvas.drawColor(mBgColor)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.translate(0f, (mQrSize * 9 / 8).toFloat())
            mTextLayout.draw(canvas)
            return result
        }
        return bitmap
    }

    class Builder {
        var mColor = Color.BLACK
        var mBgColor = Color.WHITE
        var mMargin = 2
        var mQrSize = 0
        var mOverlaySize = 0
        var mOverlayAlpha = 255
        var mContent: String? = null
        var mFootNote: String? = null
        var mOverlay: Bitmap? = null
        var mEcl = ErrorCorrectionLevel.L
        var mXFerMode = PorterDuff.Mode.SRC_OVER
        @Throws(WriterException::class)
        fun encode(): Bitmap {
            return QrGenerator(this).createQRCode()
        }

        /**
         * @param content qr content
         * @return builder instance
         */
        fun content(content: String?): Builder {
            mContent = content
            return this
        }

        /**
         * @param widthAndHeight qr image size
         * @return builder instance
         */
        fun qrSize(widthAndHeight: Int): Builder {
            mQrSize = widthAndHeight
            return this
        }

        /**
         * Optional
         *
         * @param margin default is 2.See more about [EncodeHintType.MARGIN]
         * @return builder instance
         */
        fun margin(margin: Int): Builder {
            mMargin = margin
            return this
        }

        /**
         * Optional
         *
         * @param color QRCode foreground color,default is [Color.BLACK]
         * @return builder instance
         */
        fun color(color: Int): Builder {
            mColor = color
            return this
        }

        /**
         * Optional
         *
         * @param context context
         * @param color   **@ColorRes**: QRCode foreground color resource,default is [Color.BLACK]
         * @return builder instance
         */
        fun color(context: Context, color: Int): Builder {
            return color(ContextCompat.getColor(context, color))
        }

        /**
         * Optional
         *
         * @param bgColor QRCode background color,default is [Color.WHITE]
         * @return builder instance
         */
        fun bgColor(bgColor: Int): Builder {
            mBgColor = bgColor
            return this
        }

        /**
         * Optional
         *
         * @param ecl ErrorCorrectionLevel,default is [ErrorCorrectionLevel.L](=~7%).
         * @return builder instance
         */
        fun ecc(ecl: ErrorCorrectionLevel): Builder {
            mEcl = ecl
            return this
        }

        /**
         * @param,the overlay on QRCode ,like app icon or something else.
         * @return builder instance
         */
//        private fun overlay(overlay: Bitmap?): Builder {
//            mOverlay = overlay
//            return this
//        }

        /**
         * Optional
         * @return builder instance
         */
//        fun overlay(context: Context, overlay: Int): Builder {
//            return overlay(BitmapFactory.decodeResource(context.resources, overlay))
//        }

//        /**
//         * Optional
//         *
//         * @param overlaySize the overlay icon size
//         * @return build instance
//         */
//        fun overlaySize(overlaySize: Int): Builder {
//            mOverlaySize = overlaySize
//            return this
//        }

//        /**
//         * Optional
//         *
//         * @param the alpha of overlay bitmap, range [0..255],default is 255 (opaque)
//         * @return build instance
//         */
//        fun overlayAlpha(alpha: Int): Builder {
//            mOverlayAlpha = alpha
//            return this
//        }
//
//        /**
//         * Optional
//         *
//         * @param combine overlay with qr code image, default is [android.graphics.PorterDuff.Mode.SRC_OVER]
//         * @return builder instance
//         */
//        fun overlayXForMode(xForMode: PorterDuff.Mode): Builder {
//            mXFerMode = xForMode
//            return this
//        }

//        fun footNote(note: String?): Builder {
//            mFootNote = note
//            return this
//        }
    }

    init {
        mMargin = builder.mMargin
        mQrSize = builder.mQrSize
        mColor = builder.mColor
        mBgColor = builder.mBgColor
        mContent = builder.mContent
        mEcl = builder.mEcl
        mOverlay = builder.mOverlay
        mOverlaySize = builder.mOverlaySize
        mOverlayAlpha = builder.mOverlayAlpha
        mXForMode = builder.mXFerMode
        mFootNote = builder.mFootNote
    }
}