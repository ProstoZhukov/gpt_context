/**
 * Инструменты для обеспечения привязки данных ко view, в частности, к SwipeableLayout, посредством DataBinding
 *
 * @author us.bessonov
 */
@file:JvmName("SwipeableLayoutBindingUtils")

package ru.tensor.sbis.swipeablelayout.swipeablevm

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.Observable
import androidx.lifecycle.findViewTreeLifecycleOwner
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.ColorfulMenuItemIcon
import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.SwipeableLayout.State.CLOSED
import ru.tensor.sbis.swipeablelayout.SwipeableLayout.State.CLOSING
import ru.tensor.sbis.swipeablelayout.SwipeableLayout.State.DISMISSED
import ru.tensor.sbis.swipeablelayout.SwipeableLayout.State.DRAGGING
import ru.tensor.sbis.swipeablelayout.SwipeableLayout.State.MENU_OPEN
import ru.tensor.sbis.swipeablelayout.SwipeableLayout.State.MENU_OPENING
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.DismissedWithoutMessage
import ru.tensor.sbis.swipeablelayout.api.Dismissing
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import ru.tensor.sbis.swipeablelayout.api.MenuOpening
import ru.tensor.sbis.swipeablelayout.api.SwipeEventListener
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.getBackgroundColor
import ru.tensor.sbis.swipeablelayout.util.getIcon
import ru.tensor.sbis.swipeablelayout.util.getSwipeIconColor
import ru.tensor.sbis.swipeablelayout.view.SwipeMenuItemView

private const val SWIPEABLE_VM_STATE_EVENT_LISTENER = "SWIPEABLE_VM_STATE_EVENT_LISTENER"

/** @SelfDocumented */
@BindingAdapter("swipeableVm")
fun setSwipeableViewModel(layout: SwipeableLayout, viewModel: SwipeableVm) {
    with(viewModel) {
        layout.itemUuid = viewModel.uuid

        layout.itemDismissType = itemDismissType
        layout.isDragLocked = isDragLocked
        layout.shouldSwipeContentToRight = shouldSwipeContentToRight

        dismissMessage?.let {
            layout.setDismissMessage(it)
        }

        eventListeners.forEach {
            layout.addEventListener(null, it)
        }

        swipeMenu?.takeUnless { it.items.isEmpty() }?.let { layout.setMenu(it) } ?: layout.setMenu(menu)

        layout.stateChangeListener = stateChangeListener

        layout.dismissWithoutMessageListener = object : SwipeableLayout.DismissWithoutMessageListener {
            override fun onDismissedWithoutMessage(uuid: String?) {
                onDismissedWithoutMessage.run()
            }
        }

        layout.dismissListener = object : SwipeableLayout.DismissListener {
            override fun onDismissed() {
                onDismissed.run()
            }
        }

        if (state.get()?.state == DISMISSED) {
            state.set(SwipeState(CLOSED, false))
        }

        val onStateChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val targetState = stateValue
                val animated = state.get()!!.animated
                val state = layout.state

                if (targetState == state) {
                    return
                }

                when (targetState) {
                    MENU_OPENING, MENU_OPEN -> layout.openMenu(animated)
                    CLOSING, CLOSED -> layout.close(animated)
                    DISMISSED -> layout.dismiss(animated)
                    DRAGGING -> { /* ignore */
                    }

                    else -> layout.setDismissedWithoutMessage()
                }
            }
        }

        val eventListener: SwipeEventListener = { event.value = it }

        fun onAttachedToWindow() {
            layout.addEventListener(SWIPEABLE_VM_STATE_EVENT_LISTENER, eventListener)
            layout.findViewTreeLifecycleOwner()?.let {
                event.removeObservers(it)
                event.observe(it) { event ->
                    if (layout.lastEvent.javaClass == event?.javaClass) return@observe
                    when (event) {
                        is MenuOpened, is MenuOpening -> layout.openMenu()
                        is Dismissed, is Dismissing -> layout.dismiss()
                        is DismissedWithoutMessage -> layout.setDismissedWithoutMessage()
                        else -> layout.close()
                    }
                }
            }
            if (eventListeners.isEmpty()) {
                state.apply {
                    addOnPropertyChangedCallback(onStateChangedCallback)
                    if (get()?.state != CLOSED) notifyChange()
                }
            }
        }

        layout.doOnAttachedToWindow { onAttachedToWindow() }

        layout.doOnDetachedFromWindow {
            state.removeOnPropertyChangedCallback(onStateChangedCallback)
            layout.removeEventListener(eventListener)
        }

        // нет гарантии, что вызов в doOnAttachedToWindow сработает после onBindViewHolder
        if (layout.isAttachedToWindow) {
            onAttachedToWindow()
        }
    }
}

/** @SelfDocumented */
@BindingAdapter("swipeMenu")
fun setSwipeMenu(layout: SwipeableLayout, menu: SwipeMenu<*>?) {
    menu?.let { layout.setMenu(it) }
}

/** @SelfDocumented */
@BindingAdapter("swipeMenu")
fun setSwipeMenu(layout: SwipeableLayout, menu: List<SwipeMenuItem>?) {
    menu?.let { layout.setMenu(it) }
}

/** @SelfDocumented */
@BindingAdapter("visibilityEqualsGone")
fun isVisibilityOrGone(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

/** @SelfDocumented */
@BindingAdapter("onClick")
fun onClick(view: View, runnable: Runnable?) {
    view.setOnClickListener {
        runnable?.run()
    }
}

/** @SelfDocumented */
@BindingAdapter("textColorRes")
fun setTextColorRes(view: TextView, @ColorRes colorResId: Int) {
    if (colorResId != 0) {
        view.setTextColor(ContextCompat.getColor(view.context, colorResId))
    }
}

/**
 * Задаёт обработчик нажатий на пункт свайп-меню
 *
 * @param isPostponedUntilMenuClosed `true` если клик должен инициировать закрытие меню, а обработчик вызваться лишь
 * по окончании анимации закрытия
 */
@BindingAdapter(
    value = ["swipeMenuItemClickListener", "swipeMenuViewClickListener", "isClickPostponedUntilMenuClosed"],
    requireAll = false
)
fun setSwipeMenuItemClickListener(
    itemView: View,
    listener: Runnable?,
    viewClickListener: ((view: View) -> Unit)? = null,
    isPostponedUntilMenuClosed: Boolean = false
) {
    fun onClick() {
        listener?.run()
        viewClickListener?.invoke(itemView)
    }
    val actualListener = when {
        listener == null && viewClickListener == null -> null
        isPostponedUntilMenuClosed -> View.OnClickListener {
            val swipeableLayout = checkNotNull(findViewParent<SwipeableLayout>(itemView)) {
                "Menu item view is not a descendant of SwipeableLayout"
            }
            swipeableLayout.doAfterNextCloseAnimationEnded {
                onClick()
            }
            swipeableLayout.close()
        }

        else -> View.OnClickListener { onClick() }
    }
    itemView.setOnClickListener(actualListener)
    if (actualListener == null) {
        itemView.isClickable = false
    }
}

/** @SelfDocumented */
@BindingAdapter("swipeMenuItemBackground")
fun setSwipeMenuItemBackground(view: View, icon: ColorfulMenuItemIcon?) {
    icon?.getBackgroundColor(view.context)?.let(view::setBackgroundColor)
}

/**
 * Устанавливает кликабельный фон пункта меню.
 */
@BindingAdapter("enableSwipeMenuItemBackgroundTint")
fun enableSwipeMenuItemBackgroundTint(view: View, enabled: Boolean) {
    with(view) {
        backgroundTintMode = PorterDuff.Mode.SRC_OVER
        backgroundTintList = if (enabled) {
           getTintColorStateList(context)
        } else {
            null
        }
    }
}

/**
 * Устанавливает иконку пункта свайп-меню, указанную в атрибуте из [icon]
 */
@BindingAdapter("swipeMenuItemIcon")
fun setSwipeMenuItemIcon(view: TextView, icon: ColorfulMenuItemIcon?) {
    view.text = icon?.getIcon(view.context)
}

/**
 * Устанавливает цвет иконки пункта свайп-меню из атрибута [R.attr.SwipeableLayout_iconTextColor]. Если атрибут не
 * задан, то используется [customColor]
 */
@BindingAdapter("swipeMenuIconColor")
fun setSwipeMenuIconColor(view: TextView, @ColorRes customColor: Int) {
    view.setTextColor(getSwipeIconColor(customColor, view.context))
}

/**
 * Устанавливает для [SwipeMenuItemView] иконку пункта свайп-меню, указанную в атрибуте из [icon]
 */
@BindingAdapter("swipeMenuItemIcon")
fun setSwipeMenuItemIcon(itemView: SwipeMenuItemView, icon: ColorfulMenuItemIcon?) {
    itemView.iconText = icon?.getIcon(itemView.context)
}

/**
 * Устанавливает для [SwipeMenuItemView] цвет иконки пункта свайп-меню из атрибута
 * [R.attr.SwipeableLayout_iconTextColor]. Если атрибут не задан, то используется [customColor].
 */
@BindingAdapter("swipeMenuIconColor")
fun setSwipeMenuIconColor(itemView: SwipeMenuItemView, @ColorRes customColor: Int) {
    itemView.iconTextColor = getSwipeIconColor(customColor, itemView.context)
}

/** @SelfDocumented */
@BindingAdapter("text")
internal fun TextView.setTextRes(@StringRes text: Int) {
    setText(text)
}

private fun getTintColorStateList(context: Context): ColorStateList {
    val states = arrayOf(
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(android.R.attr.state_focused),
        intArrayOf(android.R.attr.state_hovered),
        intArrayOf()
    )
    val tint = ContextCompat.getColor(context, R.color.swipeable_layout_item_pressed_tint)
    val colors = intArrayOf(
        tint,
        tint,
        tint,
        Color.TRANSPARENT
    )
    return ColorStateList(states, colors)
}