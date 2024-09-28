@file:JvmName("Utils")

package ru.tensor.sbis.design_dialogs.dialogs.container.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.utils.R as RDesignUtils
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * Ограничивает ширину View, представляющего контейнер содержимого диалогового окна, в конфигурации планшета
 *
 * @param view View, ширину которого требуется ограничить
 */
fun restrictDialogContentWidthOnTablet(view: View) {
    view.run {
        layoutParams = layoutParams.apply {
            width = getDesiredDialogContentWidthOnTablet(context).roundToInt()
        }
    }
}

/**
 * Ограничивает ширину View, представляющего контейнер содержимого диалогового окна, в конфигурации планшета.
 * В отличие от [restrictDialogContentWidthOnTablet] делает это посредством задания padding'а
 *
 * @param view View, ширину которого требуется ограничить
 */
fun restrictDialogContentWidthOnTabletByPadding(view: View) {
    view.run {
        val screenWidth = getScreenWidth(context)
        val desiredWidth = getDesiredDialogContentWidthOnTablet(context)

        val paddingHorizontal = Math.round((screenWidth - desiredWidth) / 2)
        setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom)
    }
}

fun Dialog?.slideContentDownAndThenRun(action: Runnable) {
    slideContentDownAndThenRun(null, action)
}

/**
 * @see [slideContentDownAndThenRun]
 */
@JvmOverloads
fun DialogFragment.slideContentDownAndThenDismissAllowingStateLoss(
    dialogName: String? = null,
    dismissAllowingStateLoss: Runnable
) = dialog?.slideContentDownAndThenRun(dialogName) {
    if (isAdded) dismissAllowingStateLoss.run()
}

/**
 * @see [slideContentDownAndThenRun]
 */
@JvmOverloads
fun DialogFragment.slideContentDownAndThenDismiss(
    dialogName: String? = null,
    dismiss: Runnable,
    dismissAllowingStateLoss: Runnable
) = dialog?.slideContentDownAndThenRun(dialogName) {
    dismissSafe(dismiss, dismissAllowingStateLoss)
}

/**
 * Скрывает содержимое диалога с анимацией slide down, по окончании которой выполняет [action].
 * Если скрытие невозможно, то выполняет [action] сразу
 */
fun Dialog?.slideContentDownAndThenRun(dialogName: String?, action: Runnable) {
    val dialogView = (this?.window?.decorView as ViewGroup?)
        ?.getChildAt(0)
        ?: run {
            action.run()
            return
        }

    val slideAnimation = AnimationUtils.loadAnimation(this!!.context, RDesign.anim.slide_out_from_top_animation)
    slideAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) = Unit

        override fun onAnimationEnd(animation: Animation) {
            dialogName?.let { Timber.tag(it).d("On dialog close animation end") }
            Handler(Looper.getMainLooper()).post { action.run() }
        }

        override fun onAnimationRepeat(animation: Animation?) = Unit
    })
    dialogView.startAnimation(slideAnimation)
}

/**
 * Ограничивает ширину шторки в диалоговом окне.
 */
@SuppressLint("RtlHardcoded")
internal fun restrictDialogContentMovablePanelWidthByPadding(view: View, gravity: Int) {
    view.apply {
        val movablePanelSidePadding = resources.getDimensionPixelSize(R.dimen.movable_panel_layout_offset_12)
        when (gravity) {
            Gravity.NO_GRAVITY, Gravity.CENTER, Gravity.CENTER_HORIZONTAL ->
                // подификация не требуется, на всю ширину
                return
            Gravity.LEFT, Gravity.START -> {
                val rightPadding = getScreenWidth(context) / 2
                setPadding(movablePanelSidePadding, paddingTop, rightPadding, paddingBottom)
            }
            Gravity.RIGHT, Gravity.END -> {
                val leftPadding = getScreenWidth(context) / 2
                setPadding(leftPadding, paddingTop, movablePanelSidePadding, paddingBottom)
            }
            else ->
                error("Unsupported gravity $gravity")
        }
    }
}

internal fun getScreenWidth(context: Context) = with(DisplayMetrics()) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(this)
    return@with widthPixels
}

/** @SelfDocumented */
internal fun DialogFragment.dismissSafe(
    dismiss: Runnable = Runnable(::dismiss),
    dismissAllowingStateLoss: Runnable = Runnable(::dismissAllowingStateLoss)
) {
    if (!isAdded) return
    if (isStateSaved) dismissAllowingStateLoss.run() else dismiss.run()
}

private fun getDesiredDialogContentWidthOnTablet(context: Context): Float {
    val desiredWidthPercent = TypedValue().apply {
        context.resources.getValue(RDesignUtils.integer.tablet_selection_window_width_percent, this, true)
    }.float
    return getScreenWidth(context) * desiredWidthPercent
}
