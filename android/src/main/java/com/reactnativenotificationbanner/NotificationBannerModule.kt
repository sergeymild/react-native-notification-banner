package com.reactnativenotificationbanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.*
import com.tapadoo.alerter.Alerter
import android.util.TypedValue

import android.graphics.Outline
import android.util.Base64

import android.view.ViewOutlineProvider
import android.widget.TextView
import com.facebook.react.uimanager.PixelUtil
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper
import com.tapadoo.alerter.Alert
import com.tapadoo.alerter.OnHideAlertListener
import java.lang.Exception
import java.net.URI
import java.net.URL


fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
  return (alpha * 255.0f + 0.5f).toInt() shl 24 or
    ((red * 255.0f + 0.5f).toInt() shl 16) or
    ((green * 255.0f + 0.5f).toInt() shl 8) or
    (blue * 255.0f + 0.5f).toInt()
}

fun colorFromString(color: String?): Int? {
  if (color == null || !color.startsWith("#")) return null
  return Color.parseColor(color)
}

class DefaultStyle(
  var cornerRadius: Float,
  var errorIcon: Bitmap?,
  var successBackgroundColor: Int?,
  var errorBackgroundColor: Int?,
  var successTitleColor: Int?,
  var successSubtitleColor: Int?,
  var errorTitleColor: Int?,
  var errorSubtitleColor: Int?,
  var elevation: Float
)

fun provideIcon(context: Context, source: String?): Bitmap? {
  source ?: return null
  val resourceId = context.resources.getIdentifier(source, "drawable", context.packageName)

  if (resourceId == 0) {
    // resource is not a local file
    // could be a URL, base64.
    val uri = URI(source)
    val scheme = uri.scheme ?: throw Exception("Invalid URI scheme")
    if (scheme == "data") {
      val parts: Array<String> = source.split(",").toTypedArray()
      val base64Uri = parts[1]
      val decodedString = Base64.decode(base64Uri, Base64.DEFAULT)
      return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
    return BitmapFactory.decodeStream(uri.toURL().openConnection().getInputStream())
  }
  return BitmapFactory.decodeResource(context.resources, resourceId)
}

class NotificationBannerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  var defaultStyle: DefaultStyle? = null

  override fun getName(): String {
    return "NotificationBanner"
  }

  @ReactMethod
  fun configure(params: ReadableMap) {
    currentActivity?.runOnUiThread {
      defaultStyle = DefaultStyle(
        cornerRadius = PixelUtil.toPixelFromDIP(params.getInt("cornerRadius").toFloat()),
        errorIcon = provideIcon(currentActivity!!, params.getString("errorIcon")),
        successBackgroundColor = colorFromString(params.getString("successBackgroundColor")),
        errorBackgroundColor = colorFromString(params.getString("errorBackgroundColor")),
        successTitleColor = colorFromString(params.getString("successTitleColor")),
        successSubtitleColor = colorFromString(params.getString("successSubtitleColor")),
        errorTitleColor = colorFromString(params.getString("errorTitleColor")),
        errorSubtitleColor = colorFromString(params.getString("errorSubtitleColor")),
        elevation = PixelUtil.toPixelFromDIP(params.getInt("elevation").toFloat())
      )
    }
  }

  val colors = mutableMapOf(
    "error" to argb(1.0f, 0.90f, 0.31f, 0.26f),
    "info" to argb(1.0f, 0.23f, 0.60f, 0.85f),
    "success" to argb(1.0f, 0.22f, 0.80f, 0.46f)
  )

  @ReactMethod
  fun show(params: ReadableMap, callBack: Callback?) {
    val listener = OnHideAlertListener {
      currentActivity?.runOnUiThread {
        val style = params.getString("style")!!
        val builder = Alerter.create(currentActivity!!, layoutId = R.layout.alert_default_layout).hideIcon()
        val cornerRadius = defaultStyle?.cornerRadius ?: 0f

        params.getString("title")?.let { builder.setTitle(it) }
        params.getString("subtitle")?.let { builder.setText(it) }



        if (params.hasKey("style")) builder.setBackgroundColorInt(Color.TRANSPARENT)
        if (params.hasKey("duration")) builder.setDuration(params.getInt("duration").toLong())

        builder.setEnterAnimation(R.anim.slide_in_from_top)
        builder.setExitAnimation(R.anim.slide_out_to_top)

        var backgroundColor = colors[style]!!
        var titleColor = Color.WHITE
        var subtitleColor = Color.WHITE

        if (style == "error" && defaultStyle?.errorBackgroundColor != null) {
          backgroundColor = defaultStyle!!.errorBackgroundColor!!
        }

        if (style == "success" && defaultStyle?.successBackgroundColor != null) {
          backgroundColor = defaultStyle!!.successBackgroundColor!!
        }

        if (style == "error" && defaultStyle?.errorTitleColor != null) {
          titleColor = defaultStyle!!.errorTitleColor!!
        }

        if (style == "success" && defaultStyle?.successTitleColor != null) {
          titleColor = defaultStyle!!.successTitleColor!!
        }

        if (style == "error" && defaultStyle?.errorSubtitleColor != null) {
          subtitleColor = defaultStyle!!.errorSubtitleColor!!
        }

        if (style == "success" && defaultStyle?.successSubtitleColor != null) {
          subtitleColor = defaultStyle!!.successSubtitleColor!!
        }

        var showIcon = false
        if (style == "error" && defaultStyle?.errorIcon != null) {
          showIcon = true
          defaultStyle?.errorIcon?.let { builder.setIcon(it) }
          builder.enableIconPulse(false)
          builder.showIcon(true)
        }

        builder.getLayoutContainer()?.let {
          val container = it.findViewById<View>(R.id.banner_container)
          val textContainer = it.findViewById<View>(R.id.text_container)
          val title = it.findViewById<TextView>(R.id.tvTitle)
          val subtitle = it.findViewById<TextView>(R.id.tvText)
          if (showIcon) {
            (textContainer.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0
          }
          title.setTextColor(titleColor)
          subtitle.setTextColor(subtitleColor)
          container.setBackgroundColor(backgroundColor)


          (container.parent as ViewGroup).apply {
            this.setPadding(0, 0, 0, 0)
            this.clipChildren = false
            this.clipToPadding = false
            (this.parent as ViewGroup).apply {
              this.clipChildren = false
              this.clipToPadding = false
            }
          }


          val mViewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
              val left = 0
              val top = 0;
              val right = view.width
              val bottom = view.height
              outline.setRoundRect(left, top, right, bottom, cornerRadius)
            }
          }
          container.outlineProvider = mViewOutlineProvider
          container.clipToOutline = true


          if (defaultStyle?.elevation != null) {
            container.elevation = defaultStyle?.elevation ?: 0f
          }

        }

        builder.enableClickAnimation(false)
        val alert = builder.show()
        alert?.setDismissible(true)

        var isClickHandled = false
        builder.setOnClickListener(View.OnClickListener {
          if (isClickHandled) return@OnClickListener
          isClickHandled = true
          callBack?.invoke()
          val alert = alert ?: return@OnClickListener
          val hideMethod = alert::class.java.getDeclaredMethod("hide")
          hideMethod.isAccessible = true
          hideMethod.invoke(alert)
        })
      }
    }
    if (Alerter.isShowing) Alerter.hide(listener)
    else listener.onHide()
  }
}
