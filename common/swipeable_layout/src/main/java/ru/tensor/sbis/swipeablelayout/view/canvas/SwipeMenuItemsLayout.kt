package ru.tensor.sbis.swipeablelayout.view.canvas

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withSave
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import ru.tensor.sbis.design.BR
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.DEFAULT_SWIPE_MENU_ITEM_LAYOUT
import ru.tensor.sbis.swipeablelayout.DefaultMenuItem
import ru.tensor.sbis.swipeablelayout.MenuItem
import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.ItemSize
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.api.menu.TextItem
import ru.tensor.sbis.swipeablelayout.util.toItemVm
import ru.tensor.sbis.swipeablelayout.view.SwipeMenuItemsContainer
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuDefaultItemViewFactory
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuDividerViewFactory
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuItemViewFactory
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuLayoutItemViewFactory
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool
import ru.tensor.sbis.swipeablelayout.viewpool.tryGetDefaultSwipeMenuItemViewPool
import kotlin.math.max
import ru.tensor.sbis.design.R as RDesign

/**
 * Разметка контейнера для view пунктов и разделителей свайп-меню.
 * Является облегченным canvas аналогом [LinearLayout], который содержит логику создания,
 * размещения и переиспользования view пунктов и разделителей свайп-меню.
 *
 * @author vv.chekurda
 */
internal class SwipeMenuItemsLayout(
    val parent: ViewGroup, attrs: AttributeSet? = null
) : SwipeCanvasLayout, SwipeMenuItemsContainer {

    private val resources: Resources = parent.resources

    private var isVertical: Boolean = false
    private val smallMenuItemWidth = resources.getDimensionPixelSize(R.dimen.swipeable_layout_item_small_width)
    private val largeMenuItemWidth = resources.getDimensionPixelSize(R.dimen.swipeable_layout_item_large_width)
    private val delimiterSize = resources.getDimensionPixelSize(RDesign.dimen.small_delimiter_height)
    private val minVerticalMenuItemWithIconAndTextHeight =
        resources.getDimensionPixelSize(R.dimen.swipeable_layout_item_min_vertical_height_with_icon_and_text)
    private val minVerticalMenuItemWithIconAndMultilineTextHeight =
        resources.getDimensionPixelSize(R.dimen.swipeable_layout_item_min_vertical_height_with_icon_and_text_multiline)
    private val minVerticalMenuItemWithIconHeight =
        resources.getDimensionPixelSize(R.dimen.swipeable_layout_item_min_vertical_height_with_icon)
    private var expectedItemWidth: Int = 0

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val rect = Rect()

    private lateinit var menuItemViewFactory: SwipeMenuItemViewFactory
    private val dividerViewFactory = SwipeMenuDividerViewFactory(0, parent.context)

    private val menuItemViews = mutableListOf<View>()
    private val menuDividerViews = mutableListOf<View>()

    private val containerChildren = mutableListOf<View>()
    private var menuViewPool: SwipeMenuViewPool? = null

    private var measuredWidth = 0
    private var measuredHeight = 0

    private var hasDividers = true
    private var isMenuUniform = true
    private var multiLineItemCount = 0
    private var menuItemCount = 0
    private var isItemLayoutWidthIgnored = true
    private var isCustomMenuLayout = false

    private val items = mutableListOf<SwipeMenuItem>()

    /**
     * Признак наличия пунктов меню в контейнере.
     */
    var hasMenu = false
        private set

    /**
     * Являются ли пункты меню крупноформатными (то есть, с иконкой и текстом).
     */
    var isMenuLarge = true
        private set

    /**
     * Признак наличия опции удаления.
     * @property hasRemoveOption true, если пункт удаления должен присутствовать.
     */
    var hasRemoveOption = false

    override val left: Int
        get() = rect.left
    override val top: Int
        get() = rect.top
    override val right: Int
        get() = rect.right
    override val bottom: Int
        get() = rect.bottom

    override val width: Int
        get() = measuredWidth
    override val height: Int
        get() = measuredHeight

    init {
        parent.context.withStyledAttributes(attrs, R.styleable.SwipeableLayout) {
            hasDividers = getBoolean(R.styleable.SwipeableLayout_SwipeableLayout_hasMenuItemDividers, hasDividers)
        }
    }

    /**
     * Реализация устаревшего способа задания меню
     */
    @Deprecated("Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3")
    fun <ITEM : MenuItem> setMenu(menu: SwipeMenu<ITEM>) {
        val items = menu.items
        hasMenu = items.isNotEmpty()
        this.items.clear()

        if (!hasMenu) return

        hasRemoveOption = menu.hasRemoveOption
        isMenuLarge = menu.isLarge
        multiLineItemCount = items.filterIsInstance<DefaultMenuItem>().count { it.isLongLabel }
        isItemLayoutWidthIgnored = menu.isItemLayoutWidthIgnored
        menuItemCount = items.size
        isCustomMenuLayout = menu.itemLayoutRes != DEFAULT_SWIPE_MENU_ITEM_LAYOUT

        val usedItems = if (isCustomMenuLayout) {
            menu.items
        } else {
            menu.items.map { (it as? DefaultMenuItem)?.toItemVm(parent.context) ?: it }
        }
        setMenuItems(
            SwipeMenuLayoutItemViewFactory.createCustomItemFactory(menu.itemLayoutRes, parent.context),
            menu.itemBindingId,
            usedItems,
            menu.dividerColor
        )
    }

    /** @SelfDocumented */
    fun onAttachedToWindow() {
        initializeDefaultPoolIfNeeded()
        ensureExistingMenuItemViewsAdded()
    }

    /** @SelfDocumented */
    fun onDetachedFromWindow() {
        releaseAllViews()
    }

    override fun <ITEM : SwipeMenuItem> setMenu(items: List<ITEM>) {
        hasMenu = items.isNotEmpty()
        this.items.clear()
        this.items.addAll(items)

        if (!hasMenu) return

        isMenuLarge = items.any { it.size == ItemSize.LARGE }
        isMenuUniform = items.distinctBy { it.size }.size < 2
        multiLineItemCount = items.filterIsInstance<IconWithLabelItem>().count { !it.isLabelSingleLine }
        isItemLayoutWidthIgnored = items.none { it is TextItem }
        menuItemCount = items.size

        setMenuItems(
            SwipeMenuDefaultItemViewFactory(parent.context),
            BR.viewModel,
            items.map { it.toItemVm(parent.context) },
            null
        )
    }

    override fun setMenuItemViewPool(menuViewPool: SwipeMenuViewPool?) {
        this.menuViewPool = menuViewPool
    }

    private fun initializeDefaultPoolIfNeeded() {
        if (hasMenu && menuViewPool == null && !isCustomMenuLayout && isMenuUniform && items.none { it is TextItem }) {
            menuViewPool = tryGetDefaultSwipeMenuItemViewPool(parent)
        }
    }

    private fun ensureExistingMenuItemViewsAdded() {
        if (items.isNotEmpty() && menuItemViews.isEmpty()) {
            setMenu(items.toList())
        }
    }

    /**
     * Установить список пунктов свайп-меню [items] в контейнер.
     * View для нового списка [items] будут переиспользованы с помощью [SwipeMenuViewPool],
     * или будут досоздаваться, если пул пуст.
     */
    private fun <ITEM : MenuItem> setMenuItems(
        itemViewFactory: SwipeMenuItemViewFactory,
        itemBindingId: Int,
        items: List<ITEM>,
        @ColorRes dividerColorRes: Int?
    ) {
        if (menuItemViews.isNotEmpty() && itemViewFactory.javaClass != menuItemViewFactory.javaClass) {
            // если ранее были созданы view с помощью другой фабрики, удаляем все имеющиеся
            removeAllItemViews()
        } else {
            dropUnusedMenuViews(items.count())
        }
        addRequiredMenuViews(items.count(), itemViewFactory, dividerColorRes)
        updateMenuItemViewsSize()
        bindMenuItems(items, itemBindingId)
    }

    private fun addRequiredMenuViews(
        actualItemCount: Int, itemViewFactory: SwipeMenuItemViewFactory, @ColorRes dividerColorRes: Int?
    ) {
        dividerColorRes?.let { dividerViewFactory.dividerColor = it }
        if (dividerColorRes == null) hasDividers = false
        menuItemViewFactory = itemViewFactory
        val currentViewsCount = menuItemViews.size
        for (i in 0 until actualItemCount) {
            if (i >= currentViewsCount) {
                addDividerItemViewIfNeeded()
                addMenuItemView()
            }
            menuItemViews[i].isVisible = true
            menuDividerViews.getOrNull(i)?.isVisible = i > 0
        }
    }

    private fun addDividerItemViewIfNeeded() {
        if (!hasDividers) return
        val divider = (menuViewPool?.takeDividerView() ?: dividerViewFactory.createView()).apply {
                setDividerTag()
                val width = resources.getDimensionPixelSize(RDesign.dimen.small_delimiter_height)
                layoutParams = MarginLayoutParams(width, LayoutParams.MATCH_PARENT)
            }
        menuDividerViews.add(divider)
        addView(divider)
    }

    private fun addMenuItemView() {
        val itemType = items.getOrNull(menuItemViews.size)?.let { it::class }
        val menuItem = (menuViewPool?.takeItemView() ?: menuItemViewFactory.createView(itemType))
        menuItemViews.add(menuItem)
        addView(menuItem)
    }

    private fun dropUnusedMenuViews(actualItemCount: Int) {
        (actualItemCount until menuItemViews.size).map(::getViewWithDividerAt)
            .forEach { (view, divider) ->
                if (!tryRelease(view, divider)) {
                    view.isVisible = false
                    divider?.isVisible = false
                }
            }
    }

    private fun removeAllItemViews() {
        menuItemViews.forEach {
            removeView(it)
        }
        menuItemViews.clear()
    }

    private fun releaseAllViews() {
        (0 until menuItemViews.size).map(::getViewWithDividerAt)
            .forEach { (view, divider) ->
                tryRelease(view, divider)
            }
    }

    private fun tryRelease(view: View, divider: View?): Boolean {
        menuViewPool?.let { pool ->
            pool.releaseItemView(view)
            menuItemViews.remove(view)
            removeView(view)

            divider?.let {
                pool.releaseDividerView(it)
                menuDividerViews.remove(it)
                removeView(it)
            }
            return true
        }
        return false
    }

    private fun getViewWithDividerAt(index: Int) = menuItemViews[index] to menuDividerViews.getOrNull(index)

    private fun updateMenuItemViewsSize() {
        menuItemViews.forEachIndexed { i, it ->
            expectedItemWidth = getMenuItemWidth(it.layoutParams.width, i)
            it.layoutParams = LayoutParams(expectedItemWidth, LayoutParams.MATCH_PARENT)
        }
    }

    private fun <ITEM : MenuItem> bindMenuItems(
        items: List<ITEM>, itemBindingId: Int
    ) {
        for (i in 0 until menuItemViews.size) {
            val itemView = menuItemViews[i]
            items.getOrNull(i)?.let {
                if (it.id != View.NO_ID) itemView.id = it.id
                bind(itemView, it, itemBindingId)
            }
        }
    }

    private fun bind(itemView: View, item: MenuItem, itemBindingId: Int) {
        val binding: ViewDataBinding? = DataBindingUtil.getBinding(itemView) ?: DataBindingUtil.bind(itemView)
        binding?.setVariable(itemBindingId, item)
        itemView.findViewTreeLifecycleOwner()
            ?.let { binding?.lifecycleOwner = it }
        binding?.executePendingBindings()
    }

    private fun getMenuItemWidth(itemLayoutWidth: Int, index: Int) = when {
        !isMenuUniform -> getSpecificItemWidth(itemLayoutWidth, index)
        !isItemLayoutWidthIgnored -> itemLayoutWidth
        isMenuLarge -> largeMenuItemWidth
        else -> smallMenuItemWidth
    }

    private fun getSpecificItemWidth(itemLayoutWidth: Int, index: Int) = when(items.getOrNull(index)?.size) {
        ItemSize.LARGE -> largeMenuItemWidth
        ItemSize.SMALL -> smallMenuItemWidth
        ItemSize.WRAP -> itemLayoutWidth
        null -> itemLayoutWidth
    }

    private fun isMenuVertical(availableHeight: Int): Boolean =
        isMenuUniform && menuItemCount > 1
            && (isMenuWithIconAndTextVertical(availableHeight) || isMenuWithIconVertical(availableHeight))

    private fun isMenuWithIconAndTextVertical(availableHeight: Int): Boolean {
        val minMultiLineItemsHeight = multiLineItemCount * minVerticalMenuItemWithIconAndMultilineTextHeight
        val minSingleLineItemsHeight =
            (menuItemCount - multiLineItemCount) * minVerticalMenuItemWithIconAndTextHeight
        return isMenuLarge && minMultiLineItemsHeight + minSingleLineItemsHeight <= availableHeight
    }

    private fun isMenuWithIconVertical(availableHeight: Int) =
        !isMenuLarge && isItemLayoutWidthIgnored && minVerticalMenuItemWithIconHeight * menuItemCount <= availableHeight

    private fun getItemDividerMargin() = resources.getDimensionPixelSize(
        if (isMenuLarge) R.dimen.swipeable_layout_large_separator_vertical_margin
        else R.dimen.swipeable_layout_small_separator_vertical_margin
    )

    private fun setHorizontalMenuItemDividerSize() {
        setItemDividerLayoutParams(
            delimiterSize, LayoutParams.MATCH_PARENT, marginVertical = getItemDividerMargin()
        )
    }

    private fun setVerticalMenuItemDividerSize() {
        setItemDividerLayoutParams(
            LayoutParams.MATCH_PARENT, delimiterSize, marginHorizontal = getItemDividerMargin()
        )
    }

    private fun setItemDividerLayoutParams(
        width: Int, height: Int, marginVertical: Int = 0, marginHorizontal: Int = 0
    ) {
        for (i in 0 until containerChildren.size) {
            val item = containerChildren[i]
            if (item.isDivider()) {
                (item.layoutParams as MarginLayoutParams).apply {
                    this.width = width
                    this.height = height
                    setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical)
                }
            }
        }
    }

    override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (menuItemCount != 0) {
            val availableHeight = getSize(heightMeasureSpec)
            isVertical = isMenuVertical(availableHeight)
            if (isVertical) {
                setVerticalMenuItemDividerSize()
                measureVertical(availableHeight)
            } else {
                setHorizontalMenuItemDividerSize()
                measureHorizontal(availableHeight)
            }
        } else {
            setMeasuredDimension(0, 0)
        }
    }

    private fun setMeasuredDimension(width: Int, height: Int) {
        measuredWidth = width
        measuredHeight = height
    }

    /**
     * Измерить разметку для вертикального отображения пунктов меню.
     *
     * @param availableHeight доступная высота.
     */
    private fun measureVertical(availableHeight: Int) {
        val maxChildHeight = availableHeight / menuItemCount
        val leftoverSpace = availableHeight % maxChildHeight
        var layoutWidth = 0
        containerChildren.forEachIndexed { i, it ->
            if (it.visibility == View.GONE) return@forEachIndexed
            if (it.isDivider()) {
                it.measure(
                    makeExactlySpec(expectedItemWidth - it.marginLeft - it.marginRight),
                    makeExactlySpec(it.layoutParams.height)
                )
            } else {
                val widthMeasureSpec = if (it.layoutParams.width > 0) makeExactlySpec(it.layoutParams.width)
                else makeUnspecifiedSpec()
                val height = if (i == containerChildren.lastIndex) {
                    maxChildHeight + leftoverSpace
                } else {
                    maxChildHeight
                }
                it.measure(
                    widthMeasureSpec, makeAtMostSpec(height - it.marginTop - it.marginBottom)
                )
                layoutWidth = max(it.measuredWidth + it.marginLeft + it.marginRight, layoutWidth)
            }
        }
        setMeasuredDimension(layoutWidth, availableHeight)
    }

    /**
     * Измерить разметку для горизонтального отображения пунктов меню.
     *
     * @param availableHeight доступная высота.
     */
    private fun measureHorizontal(availableHeight: Int) {
        var layoutWidth = 0
        containerChildren.forEach {
            if (it.visibility == View.GONE) return@forEach
            val widthMeasureSpec = if (it.layoutParams.width > 0) makeExactlySpec(it.layoutParams.width)
            else makeUnspecifiedSpec()
            it.measure(
                widthMeasureSpec, makeExactlySpec(availableHeight - it.marginTop - it.marginBottom)
            )
            layoutWidth += it.measuredWidth + it.marginLeft + it.marginRight
        }
        setMeasuredDimension(layoutWidth, availableHeight)
    }

    override fun layout(left: Int, top: Int) {
        if (menuItemCount == 0) return
        if (isVertical) {
            layoutVertical(left, top)
        } else {
            layoutHorizontal(left, top)
        }
        rect.set(left, top, left + measuredWidth, top + measuredHeight)
    }

    /**
     * Расположить разметку вертикально.
     *
     * @param left позиция левого края, заданная родителем.
     * @param top позиция верхнего края, заданная родителем.
     */
    private fun layoutVertical(left: Int, top: Int) {
        var topPosition = top
        containerChildren.forEach { child ->
            if (child.visibility == View.GONE) return@forEach
            val childLeft = left + child.marginLeft
            val childTop = topPosition + child.marginTop
            val childRight = childLeft + child.measuredWidth
            val childBottom = childTop + child.measuredHeight
            child.layout(childLeft, childTop, childRight, childBottom)
            topPosition = childBottom + child.marginBottom
        }
    }

    /**
     * Расположить разметку горизонтально.
     *
     * @param left позиция левого края, заданная родителем.
     * @param top позиция верхнего края, заданная родителем.
     */
    private fun layoutHorizontal(left: Int, top: Int) {
        var leftPosition = left
        containerChildren.forEach { child ->
            if (child.visibility == View.GONE) return@forEach
            val childLeft = leftPosition + child.marginLeft
            val childTop = top + child.marginTop
            val childRight = childLeft + child.measuredWidth
            val childBottom = childTop + child.measuredHeight
            child.layout(childLeft, childTop, childRight, childBottom)
            leftPosition = childRight + child.marginRight
        }
    }

    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
    }

    /**
     * Нарисовать фон.
     */
    private fun drawBackground(canvas: Canvas) {
        canvas.withSave { drawRect(rect, backgroundPaint) }
    }

    override fun setBackgroundColor(@ColorInt colorInt: Int) {
        backgroundPaint.color = colorInt
        parent.invalidate()
    }

    private fun View.setDividerTag() {
        tag = TAG_DIVIDER
    }

    private fun View.isDivider() = tag == TAG_DIVIDER

    /**
     * Добавить [view] в родительский контейнер.
     */
    private fun addView(view: View) {
        containerChildren.add(view)
        parent.addView(view)
    }

    /**
     * Удалить [view] из родительского контейнера.
     */
    private fun removeView(view: View) {
        containerChildren.remove(view)
        parent.removeView(view)
    }
}

/** Тег для распознавания view разделителя пунктов меню. */
private const val TAG_DIVIDER = "SWIPE_MENU_DIVIDER"