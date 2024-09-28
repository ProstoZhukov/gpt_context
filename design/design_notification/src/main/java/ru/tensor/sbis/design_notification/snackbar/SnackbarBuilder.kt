package ru.tensor.sbis.design_notification.snackbar

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import ru.tensor.sbis.design_notification.R
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design.R as DesignR

/**
 * Предназначен для создания [Snackbar].
 * Перед использованием убедитесь, что нужен именно [Snackbar], а не панель-информер ([SbisPopupNotification]).
 *
 * @author us.bessonov
 */
class SnackbarBuilder(private val parentView: View) {
    private var message = ""
    private var duration = Snackbar.LENGTH_LONG
    private var customDuration = 0
    private var actionText: String? = parentView.context.getString(DesignR.string.design_close)
    private var actionClickListener: View.OnClickListener? = null
    private var callback: Snackbar.Callback? = null

    @ColorInt
    private var backgroundColor = ContextCompat.getColor(
        parentView.context, R.color.design_notification_snackbar_background_color
    )

    @ColorInt
    private var textColor: Int = ContextCompat.getColor(parentView.context, android.R.color.white)
    private var ignoreInsetsEvents = false

    fun message(@StringRes messageResId: Int) = apply {
        message = parentView.context.getString(messageResId)
    }

    fun message(message: String) = apply {
        this.message = message
    }

    fun shortDuration() = apply {
        duration = Snackbar.LENGTH_SHORT
    }

    fun longDuration() = apply {
        duration = Snackbar.LENGTH_LONG
    }

    fun customDuration(duration: Int) = apply {
        customDuration = duration
    }

    fun indefiniteDuration() = apply {
        duration = Snackbar.LENGTH_INDEFINITE
    }

    fun actionText(@StringRes actionTextResId: Int) = apply {
        actionText = parentView.context.getString(actionTextResId)
    }

    fun actionText(actionText: String?) = apply {
        this.actionText = actionText
    }

    fun withoutAction() = apply {
        actionText(null)
    }

    fun actionClickListener(actionClickListener: View.OnClickListener) = apply {
        this.actionClickListener = actionClickListener
    }

    fun callback(callback: Snackbar.Callback) = apply {
        this.callback = callback
    }

    fun backgroundColorRes(@ColorRes backgroundColorResId: Int) = apply {
        backgroundColor = ContextCompat.getColor(parentView.context, backgroundColorResId)
    }

    fun backgroundColor(@ColorInt backgroundColor: Int) = apply {
        this.backgroundColor = backgroundColor
    }

    fun textColorRes(@ColorRes textColorResId: Int) = apply {
        textColor = ContextCompat.getColor(parentView.context, textColorResId)
    }

    fun setIgnoreInsetsEvents(ignoreInsetsEvents: Boolean) = apply {
        this.ignoreInsetsEvents = ignoreInsetsEvents
    }

    fun textColor(@ColorInt textColor: Int) = apply {
        this.textColor = textColor
    }

    fun build(): Snackbar {
        return Snackbar.make(parentView, message, duration).apply {
            isGestureInsetBottomIgnored = true
            if (customDuration != 0) {
                duration = customDuration
            }
            actionText?.let { configureAction(it) }
            callback?.let(::addCallback)
            val layout = view as SnackbarLayout
            layout.setBackgroundColor(backgroundColor)
            configureMessage(layout)
            configureActionButton(layout)
            if (ignoreInsetsEvents) ignoreInsetsEvents()
        }
    }

    fun show() = build().show()

    private fun Snackbar.configureAction(actionText: String) {
        setAction(actionText) { v: View? ->
            actionClickListener?.onClick(v)
            dismiss()
            setAction(null, null)
        }
    }

    private fun configureMessage(snackBarLayout: ViewGroup) {
        snackBarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
            setTextColor(textColor)
            maxLines = SNACKBAR_MAX_LINES
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                snackBarLayout.resources.getDimension(R.dimen.design_notification_snackbar_text_size)
            )
        }
    }

    private fun configureActionButton(snackBarLayout: ViewGroup) {
        snackBarLayout.findViewById<Button>(com.google.android.material.R.id.snackbar_action).apply {
            setTextColor(textColor)
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                snackBarLayout.resources.getDimension(R.dimen.design_notification_snackbar_action_size)
            )
            setTypeface(typeface, Typeface.BOLD)
        }
    }
}

private const val SNACKBAR_MAX_LINES = 4