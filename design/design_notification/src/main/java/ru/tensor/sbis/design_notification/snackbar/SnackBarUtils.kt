@file:JvmName("SnackBarUtils")

package ru.tensor.sbis.design_notification.snackbar

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design_notification.R

/**
 * Скрывает [Snackbar] сразу, без анимации.
 * Применимо, например, при уходе с текущего экрана, если анимация может подтормаживать.
 */
fun Snackbar.hideImmediately() {
    view.isVisible = false
    dismiss()
}

/**
 * Отображает [Snackbar] с содержимым [R.layout.design_notification_snackbar_info_panel] - иконкой, текстом и круглой
 * кнопкой закрытия.
 */
@JvmOverloads
fun showSnackbar(
    parentView: View,
    @ColorInt backgroundColor: Int,
    messageText: String?,
    messageIcon: Drawable?,
    @Px marginBottom: Int = 0,
    ignoreInsetsEvents: Boolean = false
) = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG).apply {
    isGestureInsetBottomIgnored = true
    val layout = view as SnackbarLayout
    hideAllChildren(layout)
    layout.setBackgroundColor(backgroundColor)
    addCustomSnackBarView(layout, messageText, messageIcon, marginBottom)
    if (ignoreInsetsEvents) ignoreInsetsEvents()
    show()
}

/** @SelfDocumented */
fun showSnackbar(
    parentView: View,
    @ColorInt backgroundColor: Int,
    message: String,
    indefinite: Boolean,
    actionClickListener: View.OnClickListener?
) {
    val snackbarBuilder = SnackbarBuilder(parentView)
        .backgroundColor(backgroundColor)
        .message(message)
    if (indefinite) {
        snackbarBuilder.indefiniteDuration()
    }
    if (actionClickListener != null) {
        snackbarBuilder.actionClickListener(actionClickListener)
    }
    snackbarBuilder.show()
}

/** @SelfDocumented */
@JvmOverloads
fun showFailureSnackbar(
    parentView: View,
    messageText: String?,
    marginBottom: Int = 0,
    duration: Int = Snackbar.LENGTH_SHORT
) = showSnackbar(
    parentView,
    messageText,
    R.color.design_notification_snackbar_background_color_failure,
    marginBottom,
    duration
)

/** @SelfDocumented */
@JvmOverloads
fun showSuccessSnackbar(
    parentView: View,
    messageText: String?,
    marginBottom: Int = 0
) = showSnackbar(
    parentView,
    messageText,
    R.color.design_notification_snackbar_background_color_success,
    marginBottom,
    Snackbar.LENGTH_SHORT
)

/** @SelfDocumented */
fun showInfoSnackbar(
    parentView: View,
    @StringRes messageText: Int
) = showInfoSnackbar(parentView, parentView.resources.getString(messageText))

/** @SelfDocumented */
@JvmOverloads
fun showInfoSnackbar(
    parentView: View,
    messageText: String?,
    duration: Int = Snackbar.LENGTH_SHORT
) = showSnackbar(parentView, messageText, R.color.design_notification_snackbar_background_color, 0, duration)

/**
 * Метод для показа [Snackbar]'а.
 *
 * @param parentView         - контейнер
 * @param messageText        - текст
 * @param backgroundColorRes - цвет фона
 * @param marginBottom       - отступ снизу
 * @param duration           - длительность
 */
@JvmOverloads
fun showSnackbar(
    parentView: View,
    messageText: String?,
    @ColorRes backgroundColorRes: Int,
    marginBottom: Int,
    duration: Int,
    ignoreInsetsEvents: Boolean = false
) = getSnackbar(parentView, messageText, backgroundColorRes, marginBottom, duration, ignoreInsetsEvents)
    .show()

/** @SelfDocumented */
internal fun Snackbar.ignoreInsetsEvents() =
    view.setOnApplyWindowInsetsListener { _: View?, insets: WindowInsets? -> insets!! }

// region private
/**
 * Метод для получения объекта [Snackbar].
 *
 * @param parentView         - контейнер
 * @param messageText        - текст
 * @param backgroundColorRes - цвет фона
 * @param marginBottom       - отступ снизу
 * @param duration           - длительность
 * @param ignoreInsetsEvents игнорировать ли системные insets
 */
private fun getSnackbar(
    parentView: View,
    messageText: String?,
    @ColorRes backgroundColorRes: Int,
    marginBottom: Int,
    duration: Int,
    ignoreInsetsEvents: Boolean
): Snackbar {
    val builder = SnackbarBuilder(parentView)
        .backgroundColorRes(backgroundColorRes)
        .customDuration(duration)
        .message(messageText!!)
        .textColorRes(android.R.color.white)
        .actionText(null)
        .setIgnoreInsetsEvents(ignoreInsetsEvents)
    val snackbar = builder.build()
    val snackbarView = snackbar.view
    val params = snackbarView.layoutParams as MarginLayoutParams
    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + marginBottom)
    snackbarView.layoutParams = params
    return snackbar
}

private fun hideAllChildren(viewGroup: ViewGroup) {
    for (i in 0 until viewGroup.childCount) {
        val child = viewGroup.getChildAt(i)
        child.visibility = View.INVISIBLE
    }
}

private fun configureIcon(snackBarView: View, messageIcon: Drawable?) {
    val iconView = snackBarView.findViewById<ImageView>(R.id.design_notification_snackbar_icon_view)
    if (messageIcon != null) {
        iconView.setImageDrawable(messageIcon)
    } else {
        iconView.visibility = View.GONE
    }
}

private fun configureText(snackBarView: View, text: String?) {
    val messageView = snackBarView.findViewById<SbisTextView>(R.id.design_notification_snackbar_text_view)
    messageView.text = text
}

private fun addCustomSnackBarView(parent: ViewGroup, messageText: String?, messageIcon: Drawable?, marginBottom: Int) {
    val snackView = View.inflate(parent.context, R.layout.design_notification_snackbar_info_panel, null)
    configureText(snackView, messageText)
    configureIcon(snackView, messageIcon)
    val params =
        RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.bottomMargin += marginBottom
    parent.addView(snackView, 0, params)
}
// endregion