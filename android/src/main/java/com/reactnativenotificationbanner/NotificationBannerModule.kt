package com.reactnativenotificationbanner

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.*
import com.tapadoo.alerter.Alerter
import android.util.TypedValue

import android.graphics.Outline

import android.view.ViewOutlineProvider
import com.facebook.react.uimanager.PixelUtil
import com.tapadoo.alerter.Alert
import com.tapadoo.alerter.OnHideAlertListener


fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
  return (alpha * 255.0f + 0.5f).toInt() shl 24 or
    ((red * 255.0f + 0.5f).toInt() shl 16) or
    ((green * 255.0f + 0.5f).toInt() shl 8) or
    (blue * 255.0f + 0.5f).toInt()
}


class NotificationBannerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "NotificationBanner"
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Int, b: Int, promise: Promise) {

    promise.resolve(a * b)

  }

  val colors = mutableMapOf(
    "error" to argb(1.0f, 0.90f, 0.31f, 0.26f),
    "info" to argb(1.0f, 0.23f, 0.60f, 0.85f),
    "success" to argb(1.0f, 0.22f, 0.80f, 0.46f)
  )

  @ReactMethod
  fun show(params: ReadableMap) {
    Alerter.hide(OnHideAlertListener {
      currentActivity?.runOnUiThread {
        val builder = Alerter.create(currentActivity!!, layoutId = R.layout.alert_default_layout).hideIcon()
        var cornerRadius = PixelUtil.toPixelFromDIP(10f)
      if (params.hasKey("borderRadius")) {
        cornerRadius = PixelUtil.toPixelFromDIP(params.getInt("borderRadius").toFloat())
      }if (params.hasKey("title")) {
          builder.setTitle(params.getString("title")!!)
        }

        if (params.hasKey("subtitle")) {
          builder.setText(params.getString("subtitle")!!)
        }

        if (params.hasKey("style")) {
          builder.setBackgroundColorInt(Color.TRANSPARENT)
        }

        if (params.hasKey("duration")) {
          builder.setDuration(params.getInt("duration").toLong())
        }

        builder.setEnterAnimation(R.anim.slide_in_from_top)
        builder.setExitAnimation(R.anim.slide_out_to_top)

        builder.getLayoutContainer()?.let {
          val container = it.findViewById<View>(R.id.banner_container)
          container.setBackgroundColor(colors[params.getString("style")!!]!!)


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
                val  left = 0
                val top = 0;
                val right = view.width
                val bottom = view.height
                outline.setRoundRect(left, top, right, bottom, cornerRadius)
              }
            }
            container.outlineProvider = mViewOutlineProvider
            container.clipToOutline = true


          if (params.hasKey("elevation")) {
            container.elevation = params.getInt("elevation").toFloat()
          }

        }

        builder.enableClickAnimation(false)
        builder.show()
      }
    })
  }
}
