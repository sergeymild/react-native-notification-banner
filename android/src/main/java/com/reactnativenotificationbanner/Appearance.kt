package com.reactnativenotificationbanner

import android.graphics.Bitmap
import android.graphics.Color

fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
  return (alpha * 255.0f + 0.5f).toInt() shl 24 or
    ((red * 255.0f + 0.5f).toInt() shl 16) or
    ((green * 255.0f + 0.5f).toInt() shl 8) or
    (blue * 255.0f + 0.5f).toInt()
}

@Suppress("EnumEntryName")
enum class BannerStyle {
  error, info, success
}

interface Style {
  var icon: Bitmap?
  var backgroundColor: Int
  var titleColor: Int
  var messageColor: Int

  var titleFont: Font
}

class Font(
  val size: Double,
  val family: String?,
  var textAlign: String
)

class ErrorStyle(
  override var icon: Bitmap?,
  override var backgroundColor: Int,
  override var titleColor: Int,
  override var messageColor: Int,
  override var titleFont: Font
) : Style

class SuccessStyle(
  override var icon: Bitmap?,
  override var backgroundColor: Int,
  override var titleColor: Int,
  override var messageColor: Int,
  override var titleFont: Font
) : Style

class InfoStyle(
  override var icon: Bitmap?,
  override var backgroundColor: Int,
  override var titleColor: Int,
  override var messageColor: Int,
  override var titleFont: Font
) : Style

class DefaultStyle(
  var elevation: Float,
  var cornerRadius: Float,
  var margin: Int,
  var padding: Float,
  var minWidth: Int,
  var maxWidth: Int,


  var success: SuccessStyle,
  var error: ErrorStyle,
  var info: InfoStyle
)

var currentAppearance = DefaultStyle(
  elevation = 4f,
  cornerRadius = 1000f,
  margin = 32,
  padding = 16f,
  minWidth = 150,
  maxWidth = 300,
  success = SuccessStyle(
    backgroundColor = argb(1f, 0.22f, 0.8f, 0.46f),
    titleColor = Color.WHITE,
    messageColor = Color.WHITE,
    titleFont = Font(size = 17.0, family = null, textAlign = "center"),
    icon = null
  ),

  error = ErrorStyle(
    backgroundColor = argb(1f, 0.9f, 0.31f, 0.26f),
    titleColor = Color.WHITE,
    messageColor = Color.WHITE,
    titleFont = Font(size = 17.0, family = null, textAlign = "center"),
    icon = null
  ),

  info = InfoStyle(
    backgroundColor = argb(1f, 0.23f, 0.6f, 0.85f),
    titleColor = Color.WHITE,
    messageColor = Color.WHITE,
    titleFont = Font(size = 17.0, family = null, textAlign = "center"),
    icon = null
  )
)

fun bannerAppearance(type: BannerStyle): Style {
  return when (type) {
    BannerStyle.error -> currentAppearance.error
    BannerStyle.info -> currentAppearance.info
    BannerStyle.success -> currentAppearance.success
  }
}

fun styleFrom(type: String): BannerStyle {
  return when (type) {
    "error" -> BannerStyle.error
    "info" -> BannerStyle.info
    else -> BannerStyle.success
  }
}

fun setNewAppearance(appearance: DefaultStyle) {
  currentAppearance = appearance
}
