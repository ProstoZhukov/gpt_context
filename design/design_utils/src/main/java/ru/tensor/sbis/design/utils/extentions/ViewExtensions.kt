/**
 * Копия из https://github.com/android/android-ktx. Этот файл можно удалить после перехода на android-X
 *
 * TODO: 12/21/2020 устаревает по поручению https://online.sbis.ru/opendoc.html?guid=bfcf2d5a-ad9a-489c-b334-62bcf931a888
 */
@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.design.utils.extentions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.utils.STANDART_CLICK_DELAY
import ru.tensor.sbis.design.utils.preventDoubleClick

/**
 * Performs the given action when this view is next laid out.
 *
 * @see doOnLayout
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun View.doOnNextLayout(crossinline action: (view: View) -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    })
}

/**
 * Performs the given action when this view is laid out. If the view has been laid out and it
 * has not requested a layout, the action will be performed straight away, otherwise the
 * action will be performed after the view is next laid out.
 *
 * @see doOnNextLayout
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun View.doOnLayout(crossinline action: (view: View) -> Unit) {
    if (ViewCompat.isLaidOut(this) && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout {
            action(it)
        }
    }
}

/**
 * Выполняет [action] при первом вызове onGlobalLayout, при котором [skipWhile] вернёт false (по умолчанию просто при
 * первом вызове)
 *
 * @return установленный [ViewTreeObserver.OnGlobalLayoutListener]
 */
inline fun View.doOnNextGlobalLayout(
    crossinline skipWhile: () -> Boolean = { false },
    crossinline action: () -> Unit
): ViewTreeObserver.OnGlobalLayoutListener {
    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (skipWhile()) return
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action()
        }
    }
    return listener.also { viewTreeObserver.addOnGlobalLayoutListener(it) }
}

/**
 * Performs the given action when the view tree is about to be drawn.
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun <T : View> T.doOnPreDraw(crossinline action: (view: T) -> Unit) {
    val vto = viewTreeObserver
    vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            action(this@doOnPreDraw)
            when {
                vto.isAlive -> vto.removeOnPreDrawListener(this)
                else -> viewTreeObserver.removeOnPreDrawListener(this)
            }
            return true
        }
    })
}

/**
 * Выполняет [action] при отсоединении [View] от окна
 */
@Suppress("unused")
inline fun View.doOnDetachedFromWindow(crossinline action: (view: View) -> Unit) {
    addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) = Unit

            override fun onViewDetachedFromWindow(v: View) {
                removeOnAttachStateChangeListener(this)
                action(this@doOnDetachedFromWindow)
            }
        }
    )
}

/**
 * Выполняет [action] при присоединении [View] к окну
 */
@Suppress("unused")
inline fun <T : View> T.doOnAttachToWindow(crossinline action: (view: T) -> Unit) {
    addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                action(this@doOnAttachToWindow)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        }
    )
}

/**
 * Sends [AccessibilityEvent] of type [AccessibilityEvent.TYPE_ANNOUNCEMENT].
 *
 * @see View.announceForAccessibility
 */
@Suppress("unused")
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun View.announceForAccessibility(@StringRes resource: Int) {
    val announcement = resources.getString(resource)
    announceForAccessibility(announcement)
}

/**
 * Класс, содержащий внутренние отступы для view.
 */
data class ViewPaddings(val left: Int, val top: Int, val right: Int, val bottom: Int)

/**
 * Класс, содержащий отступы для view.
 */
data class ViewMargins(val left: Int, val start: Int, val top: Int, val right: Int, val end: Int, val bottom: Int)

/**
 * Возвращает внутренние отступы для view в виде объекта [ViewPaddings]
 */
fun View.getPaddings(): ViewPaddings = ViewPaddings(paddingLeft, paddingTop, paddingRight, paddingBottom)

/**
 * Возвращает отступы для view в виде объекта [ViewMargins]
 */
fun View.getMargins(): ViewMargins =
    (layoutParams as ViewGroup.MarginLayoutParams).let { marginLayoutParams ->
        ViewMargins(
            marginLayoutParams.leftMargin,
            marginLayoutParams.marginStart,
            marginLayoutParams.topMargin,
            marginLayoutParams.rightMargin,
            marginLayoutParams.marginEnd,
            marginLayoutParams.bottomMargin
        )
    }

/**
 * Updates this view's padding. This version of the method allows using named parameters
 * to just set one or more axes.
 *
 * @see View.setPadding
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun View.updatePadding(
    @Px left: Int = paddingLeft,
    @Px top: Int = paddingTop,
    @Px right: Int = paddingRight,
    @Px bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

/**
 * Sets the view's padding. This version of the method sets all axes to the provided size.
 *
 * @see View.setPadding
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}

/**
 * Returns true when this view's visibility is [View.VISIBLE], false otherwise.
 *
 * ```
 * if (view.isVisible) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.VISIBLE], false to [View.GONE].
 *
 * ```
 * view.isVisible = true
 * ```
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

/**
 * Returns true when this view's visibility is [View.INVISIBLE], false otherwise.
 *
 * ```
 * if (view.isInvisible) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.INVISIBLE], false to [View.VISIBLE].
 *
 * ```
 * view.isInvisible = true
 * ```
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

/**
 * Returns true when this view's visibility is [View.GONE], false otherwise.
 *
 * ```
 * if (view.isGone) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.GONE], false to [View.VISIBLE].
 *
 * ```
 * view.isGone = true
 * ```
 */
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

/**
 * Executes [block] with the View's layoutParams and reassigns the layoutParams with the
 * updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    updateLayoutParams<ViewGroup.LayoutParams>(block)
}

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
@JvmName("updateLayoutParamsTyped")
@Deprecated(message = "Используйте ktx extensions в View.kt")
inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(block: T.() -> Unit) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}

/** @SelfDocumented */
@JvmName("doOnApplyWindowInsetsPaddings")
inline fun View.doOnApplyWindowInsets(
    crossinline applyWindowInsets: (View, WindowInsets, ViewPaddings) -> Unit
) {
    val initialPaddings = getPaddings()
    setOnApplyWindowInsetsListener { view, insets ->
        applyWindowInsets(view, insets, initialPaddings)
        insets
    }
    requestApplyInsetsWhenAttached()
}

/** @SelfDocumented */
@JvmName("doOnApplyWindowInsetsMargins")
inline fun View.doOnApplyWindowInsets(
    crossinline applyWindowInsets: (View, WindowInsets, ViewMargins) -> Unit
) {
    val initialMargins = getMargins()
    setOnApplyWindowInsetsListener { view, insets ->
        applyWindowInsets(view, insets, initialMargins)
        insets
    }
    requestApplyInsetsWhenAttached()
}

/** @SelfDocumented */
fun View.applyWindowInsets(windowInsets: WindowInsets, viewPaddings: ViewPaddings) {
    setPadding(
        viewPaddings.left + windowInsets.systemWindowInsetLeft,
        viewPaddings.top + windowInsets.systemWindowInsetTop,
        viewPaddings.right + windowInsets.systemWindowInsetRight,
        viewPaddings.bottom + windowInsets.systemWindowInsetBottom
    )
}

/** @SelfDocumented */
fun View.applyWindowInsets(windowInsets: WindowInsets, viewMargins: ViewMargins) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = viewMargins.left + windowInsets.systemWindowInsetLeft
        marginStart = viewMargins.start + windowInsets.systemWindowInsetLeft
        topMargin = viewMargins.top + windowInsets.systemWindowInsetTop
        rightMargin = viewMargins.right + windowInsets.systemWindowInsetRight
        marginEnd = viewMargins.end + windowInsets.systemWindowInsetRight
        bottomMargin = viewMargins.bottom + windowInsets.systemWindowInsetBottom
    }
}

/** @SelfDocumented */
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    view.removeOnAttachStateChangeListener(this)
                    view.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            }
        )
    }
}

/** @SelfDocumented */
@Suppress("unused")
fun View.applyWidth(width: Int) {
    layoutParams = layoutParams.apply { this.width = width }
}

/** @SelfDocumented */
fun View.applyHeight(height: Int) {
    layoutParams = layoutParams.apply { this.height = height }
}

/** @SelfDocumented */
@Suppress("unused")
fun View.setHorizontalPadding(horizontalPadding: Int) {
    setPadding(
        horizontalPadding,
        paddingTop,
        horizontalPadding,
        paddingBottom
    )
}

/**
 * Получение Activity в любой View
 */
tailrec fun View.getActivity(viewContext: Context = context): Activity =
    when (viewContext) {
        is Activity -> viewContext
        is ContextWrapper -> getActivity(viewContext.baseContext)
        else -> throw IllegalArgumentException("Can not find Activity by viewContext")
    }

/**
 * Выполняет указанный блок кода, если view видима.
 */
inline fun <T : View> T.runOnVisible(block: T.() -> Unit) {
    if (visibility != View.GONE) {
        block()
    }
}

/**
 * Возвращает ширину view с учётом отступов.
 */
fun View.getFullMeasuredWidth(): Int {
    runOnVisible {
        val layoutParams = (layoutParams as ViewGroup.MarginLayoutParams)
        return this.measuredWidth + layoutParams.marginStart + layoutParams.marginEnd
    }
    return 0
}

/**
 * Возвращает высоту view с учётом отступов.
 */
fun View.getFullMeasuredHeight(): Int {
    runOnVisible {
        val layoutParams = (layoutParams as ViewGroup.MarginLayoutParams)
        return this.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
    }
    return 0
}

/**
 * Возвращает ширину view с учётом отступов.
 */
@get:JvmName("getFullMeasureWidthNullable")
val View?.fullMeasuredWidth
    get() = this?.getFullMeasuredWidth() ?: 0
/**
 * Обновляет отступ сверху
 *
 * @return true если значение изменилось и выполнен [View.requestLayout]
 */
fun View.updateTopMargin(@Px margin: Int): Boolean =
    with(layoutParams as ViewGroup.MarginLayoutParams) {
        if (topMargin != margin) {
            topMargin = margin
            requestLayout()
            true
        } else {
            false
        }
    }

/**
 * Обновляет отступ снизу
 *
 * @return true если значение изменилось и выполнен [View.requestLayout]
 */
fun View.updateBottomMargin(@Px margin: Int): Boolean =
    with(layoutParams as ViewGroup.MarginLayoutParams) {
        if (bottomMargin != margin) {
            bottomMargin = margin
            requestLayout()
            true
        } else {
            false
        }
    }

/**
 * Обновляет отступ слева
 *
 * @return true если значение изменилось и выполнен [View.requestLayout]
 */
fun View.updateLeftMargin(@Px margin: Int): Boolean =
    with(layoutParams as ViewGroup.MarginLayoutParams) {
        if (leftMargin != margin) {
            leftMargin = margin
            requestLayout()
            true
        } else {
            false
        }
    }

/**
 * Обновляет отступ справа
 *
 * @return true если значение изменилось и выполнен [View.requestLayout]
 */
fun View.updateRightMargin(@Px margin: Int): Boolean =
    with(layoutParams as ViewGroup.MarginLayoutParams) {
        if (rightMargin != margin) {
            rightMargin = margin
            requestLayout()
            true
        } else {
            false
        }
    }

//region Padding
/**@SelfDocumented*/
fun View.setVerticalPadding(top: Int, bottom: Int = top) {
    setPadding(paddingLeft, top, paddingRight, bottom)
}

/**@SelfDocumented*/
fun View.setTopPadding(padding: Int) {
    setVerticalPadding(padding, paddingBottom)
}

/**@SelfDocumented*/
fun View.setBottomPadding(padding: Int) {
    setVerticalPadding(paddingTop, padding)
}

/**@SelfDocumented*/
fun View.setHorizontalPadding(left: Int, right: Int = left) {
    setPadding(left, paddingTop, right, paddingBottom)
}

/**@SelfDocumented*/
fun View.setLeftPadding(padding: Int) {
    setHorizontalPadding(padding, paddingRight)
}

/**@SelfDocumented*/
fun View.setRightPadding(padding: Int) {
    setHorizontalPadding(paddingLeft, padding)
}
//endregion

//region Margin
/**@SelfDocumented*/
fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    marginParams().setMargins(left, top, right, bottom)
}

/**@SelfDocumented*/
fun View.setVerticalMargin(top: Int, bottom: Int) {
    with(marginParams()) {
        setMargins(leftMargin, top, rightMargin, bottom)
        layoutParams = this
    }
}

/**@SelfDocumented*/
fun View.setTopMargin(margin: Int) {
    setVerticalMargin(margin, marginParams().bottomMargin)
}

/**@SelfDocumented*/
fun View.setBottomMargin(margin: Int) {
    setVerticalMargin(marginParams().topMargin, margin)
}

/**@SelfDocumented*/
fun View.setHorizontalMargin(left: Int, right: Int) {
    with(marginParams()) {
        setMargins(left, topMargin, right, bottomMargin)
        layoutParams = this
    }
}

/**@SelfDocumented*/
fun View.setLeftMargin(margin: Int) {
    setHorizontalMargin(margin, marginParams().rightMargin)
}

/**@SelfDocumented*/
fun View.setRightMargin(margin: Int) {
    setHorizontalMargin(marginParams().leftMargin, margin)
}

/**@SelfDocumented*/
internal fun View.marginParams() = layoutParams as ViewGroup.MarginLayoutParams
//endregion

/**
 * Показывает view плавно
 * [duration] - длительность (по-умолчанию 150 мс)
 */
fun View.show(duration: Long = 150) =
    animateVisibility(true, duration)

/**
 * Скрывает view плавно
 * [duration] - длительность (по-умолчанию 150 мс)
 */
fun View.hide(duration: Long = 150) =
    animateVisibility(false, duration)

private fun View.animateVisibility(toShow: Boolean, duration: Long) {
    toShow && visibility == View.VISIBLE && return
    !toShow && visibility != View.VISIBLE && return
    val toVisibility = if (toShow) View.VISIBLE else View.GONE
    alpha = if (toShow) 0f else 1f
    visibility = toVisibility
    animate()
        .alpha(if (toShow) 1f else 0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                visibility = toVisibility
            }
        })
}

/**
 * Вешает кликлистенер с задержкой на view
 */
fun View.preventDoubleClickListener(delay: Long = STANDART_CLICK_DELAY, action: (view: View) -> Unit) {
    setOnClickListener(preventDoubleClick(delay) { action.invoke(this) })
}

/**
 * Выполняет [action] после полного завершения layout фазы у View.
 */
inline fun View.postOnLayout(crossinline action: (view: View) -> Unit) {
    doOnLayout {
        if (it.isInLayout) {
            it.post {
                action(it)
            }
        } else {
            action(it)
        }
    }
}
