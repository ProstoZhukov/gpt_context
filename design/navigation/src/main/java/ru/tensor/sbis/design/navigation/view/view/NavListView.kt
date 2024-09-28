package ru.tensor.sbis.design.navigation.view.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.design.buttons.SbisCutButton
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonMode
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonType
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.view.adapter.NavigationViewHelper
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.delegateNotEqual
import kotlin.math.ceil

/**
 * @author ma.kolpakov
 */
@SuppressLint("ViewConstructor")
internal class NavListView(
    context: Context,
    private val viewHelper: NavigationViewHelper,
    navMenuSharedResources: NavViewSharedStyle? = null,
) : ViewGroup(context), NavListAPI {
    private val foldButtonTopMargin = Offset.S.getDimenPx(context)
    private val sectionStartMargin = Offset.X2S.getDimenPx(context)
    private val verticalListMinWidth = context.resources.getDimensionPixelSize(R.dimen.navigation_menu_width)
    private var verticalParentHeight = 0
    private var itemsInSection = 0
    private var itemsMap: MutableMap<NavigationItem, NavigationViewModel> = LinkedHashMap()
    private var hidedItems = mutableSetOf<NavigationItem>()
    private val disposable = CompositeDisposable()

    // хранит отношение модели и вью внутри родителя
    private val viewsMap = mutableMapOf<NavigationItem, View>()

    private var isExpand = false

    private val foldButton = SbisCutButton(context).apply {
        id = R.id.bottom_navigation_fold_button
        type = SbisCutButtonType.ACCENTED
        mode = SbisCutButtonMode.ARROW_DOWN
        isVisible = false
        setOnClickListener {
            isExpand = !isExpand
            mode = if (isExpand) {
                SbisCutButtonMode.ARROW_UP
            } else {
                SbisCutButtonMode.ARROW_DOWN
            }
            safeRequestLayout()
        }
    }

    private var isNeedFoldButton by delegateNotEqual(false) { newValue ->
        foldButton.isVisible = newValue
    }

    private var itemHeight = navMenuSharedResources?.itemHeight ?: 0

    /** @SelfDocumented **/
    var listener: () -> Unit = {}

    /** @SelfDocumented **/
    var orientation = Orientation.HORIZONTAL

    /** Ширина элемента ННП **/
    var itemWidth = 0

    override var configuration by delegateNotEqual(NavViewConfiguration.SCROLL) { _ ->
        requestLayout()
    }

    init {
        addView(foldButton)
    }

    override fun getDisposable() = disposable

    override fun getVisibleItemCount() = children.filterNotFoldButton { it.visibility == View.VISIBLE }.count()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        if (orientation == Orientation.HORIZONTAL) {
            var width = 0
            children.filterNotFoldButton().forEach {
                measureChild(
                    it,
                    if (itemWidth != 0)
                        MeasureSpecUtils.makeAtMostSpec(itemWidth)
                    else
                        MeasureSpecUtils.makeUnspecifiedSpec(),
                    heightMeasureSpec
                )
                if (it.visibility != GONE) {
                    width += it.measuredWidth
                }
            }
            widthSpec = MeasureSpecUtils.makeExactlySpec(width)
            super.onMeasure(widthSpec, heightMeasureSpec)
        } else {
            verticalParentHeight = MeasureSpec.getSize(heightMeasureSpec)
            when (configuration) {
                NavViewConfiguration.SCROLL -> measureScrollConfiguration(widthSpec) {
                    super.onMeasure(widthSpec, it)
                }

                NavViewConfiguration.SECTION -> {
                    measureChild(
                        foldButton,
                        MeasureSpecUtils.makeExactlySpec(verticalListMinWidth),
                        MeasureSpecUtils.makeUnspecifiedSpec()
                    )

                    itemsInSection = getSectionItemsCount()

                    if (itemsInSection >= children.filterVisibleNavItems().count()) {
                        measureScrollConfiguration(widthSpec) {
                            super.onMeasure(MeasureSpecUtils.makeExactlySpec(verticalListMinWidth), it)
                        }
                    } else {
                        measureSectionConfiguration {
                            super.onMeasure(
                                MeasureSpecUtils.makeExactlySpec(
                                    if (isExpand) getSectionsWidth()
                                    else verticalListMinWidth
                                ),
                                it
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var offset = 0
        if (orientation == Orientation.HORIZONTAL) {
            children.filterNotFoldButton().forEach {
                if (it.visibility != GONE) {
                    it.layout(offset, 0)
                    offset = it.right
                }
            }
        } else {
            when (configuration) {
                NavViewConfiguration.SCROLL -> layoutScrollConfiguration()
                NavViewConfiguration.SECTION -> {
                    if (itemsInSection >= children.filterVisibleNavItems().count()) {
                        layoutScrollConfiguration()
                    } else {
                        layoutSectionConfiguration()
                    }
                }
            }

        }
    }

    override fun setAdapter(map: Map<out NavigationItem, NavigationViewModel>) {
        removeAllViews()
        addView(foldButton)
        itemsMap.clear()
        itemsMap.putAll(map)
        viewsMap.clear()
        itemsMap.entries.forEach {
            addItem(it.key, it.value)
        }
    }

    override fun remove(item: NavigationItem) {
        viewsMap[item]?.let {
            removeView(it)
        }
        viewsMap.remove(item)
        itemsMap.remove(item)
    }

    override fun insert(item: NavigationItem, viewModel: NavigationViewModel) {
        addItem(item, viewModel)
    }

    override fun reorder(items: List<NavigationItem>) = with(itemsMap) {
        if (items == keys.toList()) return
        val itemsSet = items.toSet()
        // Если пересечение существующих и упорядоченных элементов уже в правильном порядке, то ничего делать не нужно.
        if (itemsMap.keys.isIntersectionInTheSameOrder(itemsSet)) return
        // Сохраняем элементы, которых нет в упорядоченном наборе, чтобы по возможности разместить их на прежних местах.
        val missing = entries.mapIndexedNotNull { i, entry ->
            if (!itemsSet.contains(entry.key)) {
                i to (entry.key to entry.value)
            } else {
                null
            }
        }
        val reordered = items
            .mapNotNull { item -> remove(item)?.let { item to it } }
            .toMutableList()
        missing.forEach { (oldPos, entry) ->
            reordered.add((oldPos + 1).coerceAtMost(reordered.size), entry)
        }
        clear()
        putAll(reordered)
        removeAllViews()
        addView(foldButton)
        forEach { (item, _) ->
            addView(viewsMap[item])
        }
    }

    override fun changeItemIcon(item: NavigationItem, icon: ControllerNavIcon) {
        itemsMap[item]?.controllerIcon?.onNext(icon)
    }

    override fun changeItemLabel(item: NavigationItem, label: NavigationItemLabel) {
        itemsMap[item]?.updateLabel(label)
    }

    override fun hide(item: NavigationItem) {
        hidedItems.add(item)
        viewsMap[item]?.let {
            it.visibility = GONE
        }
    }

    override fun show(item: NavigationItem) {
        viewsMap[item]?.let {
            it.visibility = VISIBLE
        }
        hidedItems.remove(item)
        showItemListener?.invoke(item)
    }

    override fun getItemPosition(item: NavigationItem): Int {
        return indexOfChild(viewsMap[item])
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        listener.invoke()
    }

    override fun getItemView(navigationItem: NavigationItem): View? {
        val viewIndex = getItemPosition(navigationItem)
        return if (viewIndex != -1) {
            getChildAt(viewIndex)
        } else {
            null
        }
    }

    override var showItemListener: ((NavigationItem) -> Unit)? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewsMap.clear()
        itemsMap.clear()
        hidedItems.clear()
    }

    private fun getSectionItemsCount() =
        if (MeasureSpec.getMode(verticalListMinWidth) == MeasureSpec.UNSPECIFIED && SDK_INT <= 22) {
            childCount
        } else {
            (verticalParentHeight - (foldButton.measuredHeight + foldButtonTopMargin)) / itemHeight
        }

    private fun getSectionsWidth(): Int = children
        .filterVisibleNavItems()
        .chunked(itemsInSection)
        .mapIndexed { index, chunk -> chunk.maxOf { it.measuredWidth } + if (index != 0) sectionStartMargin else 0 }
        .sum()

    private fun getAdditionalSectionsCount(): Int =
        (ceil(children.filterVisibleNavItems().count() / itemsInSection.toFloat()).toInt() - 1)
            .coerceAtLeast(0)

    private fun measureSectionConfiguration(measureParentHeight: (Int) -> Unit) {
        var height = 0
        children.filterNotFoldButton().forEach {
            measureChild(
                it,
                MeasureSpecUtils.makeExactlySpec(verticalListMinWidth),
                MeasureSpecUtils.makeUnspecifiedSpec()
            )
            if (it.visibility != GONE) {
                height = it.measuredHeight
            }
        }
        isNeedFoldButton = true
        height = (height * itemsInSection) + foldButton.measuredHeight + foldButtonTopMargin
        measureParentHeight(MeasureSpecUtils.makeExactlySpec(height))
    }

    private fun measureScrollConfiguration(parentWidthSpec: Int, measureParentHeight: (Int) -> Unit) {
        var height = 0
        children.filterNotFoldButton().forEach {
            measureChild(
                it,
                MeasureSpecUtils.makeExactlySpec(parentWidthSpec),
                MeasureSpecUtils.makeUnspecifiedSpec()
            )
            if (it.visibility != GONE) {
                height += it.measuredHeight
            }
        }
        isNeedFoldButton = false
        measureParentHeight(MeasureSpecUtils.makeExactlySpec(height))
    }

    private fun layoutSectionConfiguration() {
        var dX = 0
        if (isExpand) {
            children
                .filterVisibleNavItems()
                .chunked(itemsInSection)
                .forEach { chunk ->
                    var maxItemWidth = 0
                    var dY = 0
                    chunk.forEach { item ->
                        maxItemWidth = maxOf(maxItemWidth, item.measuredWidth)
                        item.layout(dX, dY)
                        dY = item.bottom
                    }
                    dX += maxItemWidth + sectionStartMargin
                }
        } else {
            var dY = 0
            children
                .filterVisibleNavItems()
                .take(itemsInSection)
                .forEach { item ->
                    item.layout(0, dY)
                    dY = item.bottom
                }
        }
        layoutFoldButton()
    }

    private fun layoutFoldButton() {
        val firstSectionDx = (verticalListMinWidth - foldButton.measuredHeight) / 2
        val itemHeight = (children.filterNotFoldButton().firstOrNull()?.measuredHeight ?: 0)

        when (foldButton.mode) {
            SbisCutButtonMode.ARROW_DOWN -> {
                foldButton.layout(firstSectionDx, (itemsInSection * itemHeight) + foldButtonTopMargin)
            }

            SbisCutButtonMode.ARROW_UP -> {
                val itemsInLastSection = children.filterVisibleNavItems().chunked(itemsInSection).last().count()
                foldButton.layout(
                    firstSectionDx + getAdditionalSectionsCount() * verticalListMinWidth,
                    itemsInLastSection * itemHeight + foldButtonTopMargin,
                )
            }
            // Такого кейса не может быть
            SbisCutButtonMode.MORE -> Unit
        }
    }

    private fun layoutScrollConfiguration() {
        var offset = 0
        children.filterVisibleNavItems().forEach {
            it.layout(0, offset)
            offset = it.bottom
        }
    }

    private fun addItem(item: NavigationItem, viewModel: NavigationViewModel) {
        itemsMap[item] = viewModel
        val newItem = viewHelper.createView(viewModel)
        viewsMap[item] = newItem.first
        if (hidedItems.contains(item)) {
            newItem.first.visibility = GONE
        }
        addView(newItem.first, itemsMap.keys.indexOf(item))
        disposable.add(newItem.second)
    }

    private fun Set<NavigationItem>.isIntersectionInTheSameOrder(other: Set<NavigationItem>): Boolean {
        return intersect(other).toList() == other.intersect(this).toList()
    }

    private fun <T : View> Sequence<T>.filterVisibleNavItems(): Sequence<T> =
        this.filterNotFoldButton { it.visibility != GONE }

    private fun <T> Sequence<T>.filterNotFoldButton(predicate: (T) -> Boolean): Sequence<T> =
        this.filter { it !is SbisCutButton && predicate(it) }

    private fun <T> Sequence<T>.filterNotFoldButton(): Sequence<T> =
        this.filter { it !is SbisCutButton }

    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }
}
