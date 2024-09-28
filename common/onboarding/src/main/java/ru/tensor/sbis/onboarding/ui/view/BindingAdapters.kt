package ru.tensor.sbis.onboarding.ui.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.extentions.getMargins
import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider
import kotlin.math.abs
import kotlin.math.ceil

@BindingAdapter(
    value = ["compressText", "compressState"],
    requireAll = true
)
internal fun SbisTextView.compressText(
    @StringRes titleResId: Int,
    isCompressed: Boolean
) {
    if (titleResId == ID_NULL) {
        return
    }
    val titleText = resources.getString(titleResId)
    setText(titleResId)
    if (isCompressed && titleText.isNotBlank()) {
        val rect = Rect()
        paint.getTextBounds(titleText, 0, titleText.length, rect)
        val height = abs(paint.fontMetrics.ascent)
        if (height > rect.height()) {
            val emptySpace = paint.fontMetrics.run { abs(top - ascent) + (bottom - descent) }
            val newPadding = paddingBottom - ceil(emptySpace.toDouble())
            setPadding(paddingLeft, paddingTop, paddingRight, newPadding.toInt())
        }
    }
}

@BindingAdapter("onClick")
internal fun View.setActionOnClick(action: (() -> Unit)?) {
    setOnClickListener { action?.invoke() }
}

@BindingAdapter("onClickTV")
internal fun View.setTvActionOnClick(action: (() -> Unit)?) {
    val isTV = ThemeProvider.isTV(context)
    if (isTV.not()) {
        return
    }
    setOnKeyListener { _, keyCode, event ->
        val isDown = event.action == KeyEvent.ACTION_DOWN
        if (isDown && tvSelectionEvent.contains(keyCode)) {
            action?.invoke()
            true
        } else false
    }
}

@BindingAdapter("isNotGone")
internal fun View.setNotGone(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("flipProgress")
internal fun ProgressBar.setFlipProgress(progress: Int) {
    if (progress > max) {
        return
    }
    setProgress(progress)
}

private val tvSelectionEvent = listOf(
    KeyEvent.KEYCODE_BUTTON_SELECT,
    KeyEvent.KEYCODE_BUTTON_A,
    KeyEvent.KEYCODE_ENTER,
    KeyEvent.KEYCODE_DPAD_CENTER,
    KeyEvent.KEYCODE_NUMPAD_ENTER,
    KeyEvent.KEYCODE_NUMPAD_ENTER
)

//region imageResourceEfficiently
@BindingAdapter("imageResourceEfficiently")
internal fun ImageView.setImageResourceEfficiently(
    @DrawableRes imageResource: Int
) {
    if (imageResource == ID_NULL || visibility != View.VISIBLE) {
        return
    }
    setImageResourceInSampleSize(imageResource)
}

/** Предотвращает возможный [OutOfMemoryError] */
private fun ImageView.setImageResourceInSampleSize(@DrawableRes imageResource: Int) {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeResource(resources, imageResource, options)
    val (reqHeight: Int, reqWidth: Int) = run {
        val margins = getMargins()
        val metrics = context.getDisplayMetrics()
        metrics.heightPixels - margins.top - margins.bottom to
            metrics.widthPixels - margins.left - margins.right
    }
    options.inSampleSize = calculateInSampleSize(
        options = options,
        requiredWidth = reqWidth,
        requiredHeight = reqHeight
    )
    options.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeResource(resources, imageResource, options)
    setImageBitmap(bitmap)
}

private fun Context.getDisplayMetrics() = DisplayMetrics().also {
    val defaultDisplay = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    defaultDisplay.getMetrics(it)
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    requiredWidth: Int,
    requiredHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > requiredHeight || width > requiredWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while (halfHeight / inSampleSize >= requiredHeight || halfWidth / inSampleSize >= requiredWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
//endregion