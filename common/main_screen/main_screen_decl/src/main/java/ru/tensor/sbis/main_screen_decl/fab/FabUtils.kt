/**
 * Инструменты для управления плавающими кнопками на главном экране
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.main_screen_decl.fab

import android.content.Context
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.main_screen_decl.env.BottomBarProviderExt

/**
 * Показывает заданные кнопки. Все незадействованные кнопки будут скрыты.
 * Если [context] не реализует [BottomBarProviderExt], то выполнится [addButtonFallback].
 * Если [isSplitViewOnTablet] == `true`, то на планшете всегда принудительно вызывается [addButtonFallback].
 * @see [createDefaultMainFabDrawable]
 */
fun addActionButtons(
    context: Context,
    vararg buttons: IconFab,
    isSplitViewOnTablet: Boolean = true,
    addButtonFallback: (() -> Unit)? = null
) = addActionButtons(context, buttons.toList(), isSplitViewOnTablet, addButtonFallback)

/**
 * Показывает заданные кнопки. Все незадействованные кнопки будут скрыты.
 * Если [Fragment.getContext] не реализует [BottomBarProviderExt], то выполнится [addButtonFallback].
 * Если [isSplitViewOnTablet] == `true`, то на планшете всегда принудительно вызывается [addButtonFallback].
 * @see [createDefaultMainFabDrawable
 *
 * @throws [IllegalStateException] если `fragment.context == null`
 */
fun addActionButtons(
    fragment: Fragment,
    vararg buttons: IconFab,
    isSplitViewOnTablet: Boolean = true,
    addButtonFallback: (() -> Unit)? = null
) {
    val context = checkNotNull(fragment.context) {
        "Cannot add button: fragment is not attached"
    }
    addActionButtons(context, buttons.toList(), isSplitViewOnTablet, addButtonFallback)
}

/**
 * @see addActionButtons
 */
fun addActionButtons(
    context: Context,
    buttons: List<IconFab>,
    isSplitViewOnTablet: Boolean = true,
    addButtonFallback: (() -> Unit)? = null
) = withBottomBarProviderOrFallback(context, addButtonFallback, isSplitViewOnTablet) {
    addActionButtons(buttons = buttons.toTypedArray())
}

/**
 * Отображает плавающую кнопку с датой.
 * Если [context] не реализует [BottomBarProviderExt], то выполнится [addButtonFallback].
 * Если [isSplitViewOnTablet] == `true`, то на планшете всегда принудительно вызывается [addButtonFallback].
 */
fun addTodayActionButton(
    context: Context,
    button: TodayFab,
    animated: Boolean = false,
    isSplitViewOnTablet: Boolean = true,
    addButtonFallback: (() -> Unit)? = null
) = withBottomBarProviderOrFallback(context, addButtonFallback, isSplitViewOnTablet) {
    setTodayFabIcon(button.icon)
    setTodayExtraFabClickListener(button.clickListener)
    showTodayExtraFabButton(button.date, button.isWorkDay, animated)
}

/**
 * Отображает плавающую кнопку с датой.
 * Если [Fragment.getContext] не реализует [BottomBarProviderExt], то выполнится [addButtonFallback].
 * Если [isSplitViewOnTablet] == `true`, то на планшете всегда принудительно вызывается [addButtonFallback].
 *
 * @throws [IllegalStateException] если `fragment.context == null`
 */
fun addTodayActionButton(
    fragment: Fragment,
    button: TodayFab,
    animated: Boolean = false,
    isSplitViewOnTablet: Boolean = true,
    addButtonFallback: (() -> Unit)? = null
) {
    val context = checkNotNull(fragment.context) {
        "Cannot add button: fragment is not attached"
    }
    addTodayActionButton(context, button, animated, isSplitViewOnTablet, addButtonFallback)
}

/**
 * Скрывает плавающую кнопку с датой.
 * Если [context] не реализует [BottomBarProviderExt], то выполнится [hideButtonFallback].
 * Если [isSplitViewOnTablet] == `true`, то на планшете всегда принудительно вызывается [hideButtonFallback].
 */
fun hideTodayActionButton(
    context: Context,
    animated: Boolean = false,
    isSplitViewOnTablet: Boolean = true,
    hideButtonFallback: (() -> Unit)? = null
) = withBottomBarProviderOrFallback(context, hideButtonFallback, isSplitViewOnTablet) {
    hideTodayExtraFabButton(animated)
}

/**
 * Скрывает плавающую кнопку с датой.
 * Если [Fragment.getContext] не реализует [BottomBarProviderExt], то выполнится [hideButtonFallback].
 * Если [isSplitViewOnTablet] == `true`, то на планшете всегда принудительно вызывается [hideButtonFallback].
 *
 * @throws [IllegalStateException] если `fragment.context == null`
 */
fun hideTodayActionButton(
    fragment: Fragment,
    animated: Boolean = false,
    isSplitViewOnTablet: Boolean = true,
    hideButtonFallback: (() -> Unit)? = null
) {
    val context = checkNotNull(fragment.context) {
        "Cannot add button: fragment is not attached"
    }
    hideTodayActionButton(context, animated, isSplitViewOnTablet, hideButtonFallback)
}

private fun withBottomBarProviderOrFallback(
    context: Context,
    fallback: (() -> Unit)?,
    isSplitViewOnTablet: Boolean,
    action: BottomBarProviderExt.() -> Unit
) {
    if (isSplitViewOnTablet && DeviceConfigurationUtils.isTablet(context)) {
        fallback?.invoke()
        return
    }
    val bottomBarProvider = context as? BottomBarProviderExt
    if (bottomBarProvider == null && fallback != null) {
        fallback()
        return
    }
    checkNotNull(bottomBarProvider) {
        "Cannot add button: context should implement BottomBarProviderExt"
    }.action()
}