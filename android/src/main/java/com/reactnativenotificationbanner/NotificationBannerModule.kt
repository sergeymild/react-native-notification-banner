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

fun colorFor(context: Context, params: ReadableMap, key: String, default: Int): Int {
  if (!params.hasKey(key)) return default
  return ColorPropConverter.getColor(params.getDouble(key), context)
}

fun px(value: Float): Float {
  return PixelUtil.toPixelFromDIP(value)
}
fun pxFor(params: ReadableMap, key: String, default: Float): Float {
  if (!params.hasKey(key)) return default
  return PixelUtil.toPixelFromDIP(params.getDouble(key))
}

class NotificationBannerModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "NotificationBanner"
  }

  @ReactMethod
  fun configure(params: ReadableMap) {
    val context = currentActivity ?: reactApplicationContext


    var errorStyle = currentAppearance.error
    if (params.hasKey("error")) {
      val currentError = bannerAppearance(BannerStyle.error)
      val error = params.getMap("error")!!
      var font = currentError.titleFont
      if (error.hasKey("titleFont")) {
        val f = error.getMap("titleFont")!!
        font = Font(size = f.getDouble("size"), family = null)
      }

      errorStyle = ErrorStyle(
        icon = provideIcon(context, error.getString("icon")),
        backgroundColor = colorFor(context, error, "backgroundColor", currentError.backgroundColor),
        titleColor = colorFor(context, error, "titleColor", currentError.titleColor),
        messageColor = colorFor(context, error, "messageColor", currentError.messageColor),
        titleFont = font
      )
    }

    var successStyle = currentAppearance.success
    if (params.hasKey("success")) {
      val currentSuccess = bannerAppearance(BannerStyle.success)
      val error = params.getMap("success")!!
      var font = currentSuccess.titleFont
      if (error.hasKey("titleFont")) {
        val f = error.getMap("titleFont")!!
        font = Font(size = f.getDouble("size"), family = null)
      }

      successStyle = SuccessStyle(
        icon = provideIcon(context, error.getString("icon")),
        backgroundColor = colorFor(context, error, "backgroundColor", currentSuccess.backgroundColor),
        titleColor = colorFor(context, error, "titleColor", currentSuccess.titleColor),
        messageColor = colorFor(context, error, "messageColor", currentSuccess.messageColor),
        titleFont = font
      )
    }


    val defaultStyle = DefaultStyle(
      elevation = if (params.hasKey("elevation")) params.getDouble("elevation").toFloat() else currentAppearance.elevation,
      cornerRadius = pxFor(params, "cornerRadius", px(1000f)),
      error = errorStyle,
      success = successStyle,
      info = currentAppearance.info,
      padding = currentAppearance.padding,
      margin = currentAppearance.margin
    )
    setNewAppearance(defaultStyle)
  }

  @ReactMethod
  fun show(params: ReadableMap, callBack: Callback?) {
    val listener = OnHideAlertListener {
      currentActivity?.runOnUiThread {
        val activity = currentActivity ?: return@runOnUiThread
        val style = styleFrom(params.getString("style")!!)
        val builder =
          Alerter.create(activity, layoutId = R.layout.alert_default_layout).hideIcon()

        params.getString("title")?.let { builder.setTitle(it) }
        params.getString("message")?.let { builder.setText(it) }



        if (params.hasKey("style")) builder.setBackgroundColorInt(Color.TRANSPARENT)
        if (params.hasKey("duration")) builder.setDuration(params.getInt("duration").toLong())

        builder.setEnterAnimation(R.anim.slide_in_from_top)
        builder.setExitAnimation(R.anim.slide_out_to_top)


        val bannerStyle = bannerAppearance(style)

        var showIcon = false
        bannerStyle.icon?.let {
          showIcon = true
          builder.setIcon(it)
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
          textContainer.setPadding(
            px(currentAppearance.padding).toInt(),
            px(16f).toInt(),
            px(currentAppearance.padding).toInt(),
            px(16f).toInt()
          )
          title.setTextColor(bannerStyle.titleColor)
          title.setTextSize(TypedValue.COMPLEX_UNIT_SP, bannerStyle.titleFont.size.toFloat())
          subtitle.setTextColor(bannerStyle.messageColor)
          container.setBackgroundColor(bannerStyle.backgroundColor)


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
              outline.setRoundRect(left, top, right, bottom, px(currentAppearance.cornerRadius))
            }
          }
          container.outlineProvider = mViewOutlineProvider
          container.clipToOutline = true

          container.elevation = px(currentAppearance.elevation)
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
