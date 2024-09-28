package ru.tensor.sbis.swipeablelayout.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.common.util.exhaustive
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.MenuItem
import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.api.SwipeMenuSide
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeCanvasLayout
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeCanvasLayout.CanvasClickListener
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeDismissMessageLayout
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeMenuItemsLayout
import ru.tensor.sbis.swipeablelayout.view.edit_mode.showPreview
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool

/**
 * Контейнер для отображения содержимого свайп-меню.
 * В содержимое входит: разметка контейнера пунктов меню [SwipeMenuItemsLayout],
 * разметка сообщения об удалении [SwipeDismissMessageLayout].
 *
 * @author vv.chekurda
 */
internal class SwipeContainerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs), SwipeMenuItemsContainer {

    /**
     * Разметка контейнера пунктов меню.
     */
    private val menuItemsContainer = SwipeMenuItemsLayout(this)

    /**
     * Разметка сообщения об удалении.
     */
    private var dismissMessageLayout: SwipeDismissMessageLayout? = null

    private var dismissMessageLayoutSide = SwipeMenuSide.RIGHT

    /**
     * Слушатель кликов по области [dismissMessageLayout],
     * которая перекрывает ячейку после быстрого смахивания.
     */
    private var contentOverlayListener: CanvasClickListener? = null

    private val menuAppearance = MenuAppearance()

    @ColorInt
    private var overlayBackgroundColor = Color.TRANSPARENT

    /**
     * Разметка контейнера пунктов меню.
     */
    val menuItemsLayout: SwipeCanvasLayout
        get() = menuItemsContainer

    /**
     * Признак наличия пунктов меню.
     * @property hasMenu true, если в контейнере присутствуют пункты меню.
     */
    val hasMenu: Boolean
        get() = menuItemsContainer.hasMenu

    /**
     * Признак наличия опции удаления.
     * @property hasRemoveOption true, если пункт удаления должен присутствовать.
     */
    var hasRemoveOption: Boolean
        get() = menuItemsContainer.hasRemoveOption
        set(value) {
            menuItemsContainer.hasRemoveOption = value
        }

    init {
        id = R.id.swipeable_layout_menu_container_view
        setWillNotDraw(false)
        getContext().withStyledAttributes(attrs, R.styleable.SwipeableLayout) {
            overlayBackgroundColor = getColor(
                R.styleable.SwipeableLayout_SwipeableLayout_menuDismissMessageBackground, overlayBackgroundColor
            )
        }
        if (isInEditMode) showPreview(context, attrs)
        setupForAutoTests()
    }

    private fun setupForAutoTests() {
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info?.text = dismissMessageLayout?.messageText?.let { "dismissMessage = $it" }
            }
        }
    }

    /**
     * Реализация устаревшего способа задания меню
     */
    @Deprecated("Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3")
    fun <ITEM : MenuItem> setMenu(menu: SwipeMenu<ITEM>) {
        menuAppearance.updateMenuBackground(menu.backgroundColor)
        menuAppearance.updateOverlayBackground(menu.overlayBackgroundColor)
        menuItemsContainer.setMenu(menu)
        dismissMessageLayout?.hasIcon = menuItemsContainer.isMenuLarge
    }

    override fun <ITEM : SwipeMenuItem> setMenu(items: List<ITEM>) {
        menuItemsContainer.setMenu(items)
        dismissMessageLayout?.hasIcon = menuItemsContainer.isMenuLarge
    }

    override fun setMenuItemViewPool(menuViewPool: SwipeMenuViewPool?) {
        menuItemsContainer.setMenuItemViewPool(menuViewPool)
    }

    /**
     * Присоединить разметку сообщения об удалении [SwipeDismissMessageLayout].
     */
    fun attachDismissMessageLayout(layout: SwipeDismissMessageLayout, side: SwipeMenuSide) {
        if (dismissMessageLayout != null) return
        dismissMessageLayout = layout.also {
            it.attachToParent(this)
            it.setOnClickListener(contentOverlayListener)
        }
        dismissMessageLayoutSide = side
        setDismissMessageBackgroundColor(menuAppearance.overlayBackgroundColorRes)
    }

    /**
     * Отсоединить разметку сообщения об удалении [SwipeDismissMessageLayout].
     */
    fun detachDismissMessageLayout() {
        dismissMessageLayout?.run {
            setOnClickListener(null)
            setBackgroundColor(Color.TRANSPARENT)
            dismissMessageLayout = null
        }
    }

    /**
     * Установить слушателя кликов по области перекрытия контента ячейки.
     */
    fun setContentOverlayClickListener(listener: CanvasClickListener) {
        contentOverlayListener = listener
        dismissMessageLayout?.setOnClickListener(listener)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        dismissMessageLayout?.onTouchEvent(event) ?: super.onTouchEvent(event)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = if (!isInEditMode) getSize(widthMeasureSpec) else EDIT_MODE_PREVIEW_WIDTH
        val availableHeight = if (!isInEditMode) getSize(heightMeasureSpec) else EDIT_MODE_PREVIEW_HEIGHT

        menuItemsContainer.measure(makeUnspecifiedSpec(), makeExactlySpec(availableHeight))
        dismissMessageLayout?.measure(makeExactlySpec(availableWidth), makeExactlySpec(availableHeight))

        val horizontalPadding = paddingLeft + paddingRight
        val childrenWidth = menuItemsContainer.width + (dismissMessageLayout?.width ?: 0)
        val measuredWidth = horizontalPadding + childrenWidth
        setMeasuredDimension(measuredWidth, availableHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val menuItemsContainerLeft = if (dismissMessageLayout == null || dismissMessageLayoutSide == SwipeMenuSide.RIGHT) {
            paddingLeft
        } else {
            paddingLeft + dismissMessageLayout!!.width
        }
        menuItemsContainer.layout(menuItemsContainerLeft, paddingTop)
        dismissMessageLayout?.let {
            val dismissLayoutLeft = when(dismissMessageLayoutSide) {
                SwipeMenuSide.LEFT -> menuItemsContainer.left - it.width
                SwipeMenuSide.RIGHT -> menuItemsContainer.right
            }
            it.layout(dismissLayoutLeft, paddingTop)
        }
    }


    override fun onDraw(canvas: Canvas) {
        menuItemsContainer.draw(canvas)
        dismissMessageLayout?.draw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        menuItemsContainer.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        menuItemsContainer.onDetachedFromWindow()
    }

    override fun hasOverlappingRendering(): Boolean = false

    private inner class MenuAppearance {
        @ColorRes
        var menuBackgroundColorRes: Int = 0

        @ColorRes
        var overlayBackgroundColorRes: Int = 0

        fun updateMenuBackground(@ColorRes color: Int) {
            if (menuBackgroundColorRes != color) {
                menuBackgroundColorRes = color
                setItemsContainerBackgroundColor(menuBackgroundColorRes)
            }
        }

        fun updateOverlayBackground(@ColorRes color: Int) {
            if (overlayBackgroundColorRes != color) {
                overlayBackgroundColorRes = color
                setDismissMessageBackgroundColor(overlayBackgroundColorRes)
            }
        }
    }

    /**
     * Установить цвет фона контейнера пунктов меню [menuItemsContainer].
     *
     * @param color цвет фона.
     */
    private fun setItemsContainerBackgroundColor(@ColorRes color: Int) {
        menuItemsContainer.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    /**
     * Установить цвет фона сообщения об удалении [dismissMessageLayout].
     *
     * @param color цвет фона.
     */
    private fun setDismissMessageBackgroundColor(@ColorRes color: Int) {
        dismissMessageLayout?.setBackgroundColor(
            when {
                overlayBackgroundColor != Color.TRANSPARENT -> overlayBackgroundColor
                color != ResourcesCompat.ID_NULL -> ContextCompat.getColor(context, color)
                else -> Color.MAGENTA
            }
        )
    }
}

/** Ширина [SwipeContainerLayout] для отображения превью в edit mode. */
private const val EDIT_MODE_PREVIEW_HEIGHT = 200

/** Высота [SwipeContainerLayout] для отображения превью в edit mode. */
private const val EDIT_MODE_PREVIEW_WIDTH = 800