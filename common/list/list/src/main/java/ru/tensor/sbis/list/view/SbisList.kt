package ru.tensor.sbis.list.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.view.children
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.view.adapter.NO_ITEM_TO_HIGHLIGHT
import ru.tensor.sbis.list.view.adapter.SbisAdapter
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.calback.ItemMoveCallback
import ru.tensor.sbis.list.view.calback.ListViewListener
import ru.tensor.sbis.list.view.decorator.DecoratorHolder
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.DataInfo
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.DataInsertListener
import ru.tensor.sbis.list.view.utils.HeaderItemDataProvider
import ru.tensor.sbis.list.view.utils.HeaderItemMediator
import ru.tensor.sbis.list.view.utils.InitialDataAddListener
import ru.tensor.sbis.list.view.utils.ItemTouchHelperAttacher
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.NeedLoadMoreNotifierCallbackHandler
import ru.tensor.sbis.list.view.utils.ScrollerToPosition
import ru.tensor.sbis.list.view.utils.TopLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager
import ru.tensor.sbis.list.view.utils.setupAnimations

/**
 * Реализация [RecyclerView] для которой поддержаны требования стандарта(http://axure.tensor.ru/MobileStandart8/#p=%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_02_&g=1)
 */
class SbisList internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val listDataHolder: ListDataHolder = ListDataHolder(),
    colorProvider: ColorProvider = ColorProvider(context),
    private val adapter: SbisAdapter = SbisAdapter(),
    private val decoratorHolder: DecoratorHolder = DecoratorHolder(),
    private val bottomLoadMoreProgressHelper: BottomLoadMoreProgressHelper = BottomLoadMoreProgressHelper(),
    private val topLoadMoreProgressHelper: TopLoadMoreProgressHelper = TopLoadMoreProgressHelper(),
    private val layoutManager: SbisGridLayoutManager = SbisGridLayoutManager(
        context,
        listDataHolder,
        bottomLoadMoreProgressHelper,
        topLoadMoreProgressHelper
    ),
    private val needLoadMoreNotifierCallbackHandler: NeedLoadMoreNotifierCallbackHandler = NeedLoadMoreNotifierCallbackHandler(
        layoutManager
    ),
    private val helperAttacher: ItemTouchHelperAttacher = ItemTouchHelperAttacher(listDataHolder),
    private val scrollerToPosition: ScrollerToPosition = ScrollerToPosition()
) : RecyclerView(
    context,
    attrs,
    defStyle
), SelectionManager {
    private val bottomLoadMoreDisposable: Disposable
    private val itemVisibilityPositionProvider = object : DataChangedObserver.ItemVisibilityPositionProvider {
        override fun findFirstCompletelyVisibleItemPosition() = layoutManager.findFirstCompletelyVisibleItemPosition()
        override fun findLastCompletelyVisibleItemPosition() = layoutManager.findLastCompletelyVisibleItemPosition()
    }
    var needInitialScroll: Boolean = true

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
    ) : this(
        context,
        attrs,
        defStyle,
        listDataHolder = ListDataHolder()
    )

    init {
        scrollerToPosition.setListAndLayoutManager(this, layoutManager)
        adapter.registerAdapterDataObserver(scrollerToPosition)
        adapter.registerSelectionObserver(this)

        setAdapter(adapter)
        setLayoutManager(layoutManager)
        updateMeasurementCacheEnabled()
        decoratorHolder.addDecorators(
            this,
            layoutManager,
            listDataHolder,
            adapter,
            resources.getDimensionPixelSize(R.dimen.list_divider_space_size),
            colorProvider
        )
        helperAttacher.attach(this)
        bottomLoadMoreDisposable = bottomLoadMoreProgressHelper.attach(
            this,
            layoutManager,
            adapter,
            decoratorHolder
        )
        topLoadMoreProgressHelper.setAdapter(adapter)
        itemAnimator!!.setupAnimations()
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                setBackgroundColor(listDataHolder.getBackgroundResId(context))
                scrollerToPosition.rememberFirstVisibleItemPosition()
                updateMeasurementCacheEnabled()
            }
        })

        val horizontal = with(context.obtainStyledAttributes(attrs, R.styleable.SbisList, defStyle, 0)) {
            getBoolean(R.styleable.SbisList_SbisList_isHorizontal, false)
        }

        isHorizontal(horizontal)
    }

    /**
     * Установить слушателя для события изменения данных в адаптере.
     */
    fun setDataChangedObserver(observer: DataChangedObserver) {
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                observer.onItemRangeInserted(positionStart, itemCount, itemVisibilityPositionProvider)
            }
        })
    }

    /**
     * Должен ли список быть горизонтальным
     */
    fun isHorizontal(orientation: Boolean) {
        layoutManager.orientation = if (orientation) HORIZONTAL else VERTICAL
        bottomLoadMoreProgressHelper.isHorizontal = orientation
        topLoadMoreProgressHelper.isHorizontal = orientation
    }

    /** @SelfDocumented */
    fun setScrollEnabled(enabled: Boolean) {
        layoutManager.setScrollEnabled(enabled)
    }

    /**@SelfDocumented*/
    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    fun notifyItemChanged(position: Int, data: DataInfo) {
        listDataHolder.setData(data)
        adapter.notifyItemChanged(position, data.getItems())
    }

    fun notifyItemInserted(position: Int, data: DataInfo) {
        listDataHolder.setData(data)
        adapter.notifyItemInserted(position, data.getItems())
        updateDecoration(position)
    }

    fun notifyItemRemoved(position: Int, data: DataInfo) {
        listDataHolder.setData(data)
        adapter.notifyItemRemoved(position, data.getItems())
        updateDecoration(position)
    }

    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int, data: DataInfo) {
        listDataHolder.setData(data)
        adapter.notifyItemRangeChanged(positionStart, itemCount, data.getItems())
    }

    fun notifyItemRangeInserted(positionStart: Int, itemCount: Int, data: DataInfo) {
        listDataHolder.setData(data)
        var additionalItems = 0
        if (topLoadMoreProgressHelper.isAdded()) {
            additionalItems = 1
        }
        adapter.notifyItemRangeInserted(positionStart, itemCount - additionalItems, data.getItems())
        updateDecoration(positionStart)
    }

    fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int, data: DataInfo) {
        listDataHolder.setData(data)
        adapter.notifyItemRangeRemoved(positionStart, itemCount, data.getItems())
        updateDecoration(positionStart)
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().also {
            it.putParcelable(SUPER_STATE, super.onSaveInstanceState())
            adapter.saveState(it)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE))
            adapter.getRestore(state)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSizeChanged(width: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, h, oldw, oldh)
        if (width > 0) {
            layoutManager.setViewWidth(width)
            updateMeasurementCacheEnabled()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()!!.lifecycle.addObserver(lifecycleObserver)
    }

    /**
     * Установить колбек для результата успешного окончания перетаскивания элемента текущего списка в пределах
     * этого же списка.
     * @param itemMoveCallback ItemMoveCallback
     */
    fun setItemMoveCallback(itemMoveCallback: ItemMoveCallback) {
        helperAttacher.setItemMoveCallback(itemMoveCallback)
    }

    /**
     * Нужно ли прокручивать список к первому из добавленных. Требуется в Задачах.
     */
    fun setShouldMoveToAdded(should: Boolean) {
        scrollerToPosition.moveToAdded = should
    }

    /**
     * Установить колбек для сигнала о необходимости подгрузки элементов в начало или конец списка или обновления.
     * @param listViewListener LoadMoreCallback
     */
    fun setLoadMoreCallback(listViewListener: ListViewListener) {
        needLoadMoreNotifierCallbackHandler.handle(this, listViewListener)
    }

    @Deprecated("Устаревший метод работал не правильно теперь есть раздельные методы для навигации и FAB")
    fun fabPadding(has: Boolean) {
        bottomLoadMoreProgressHelper.navMenuPadding(has)
    }

    /**
     * Нужен ли отступ от последнего элемента до края списка для размещения FAB.
     * @param has Boolean
     */
    fun floatingPanelPadding(has: Boolean) {
        bottomLoadMoreProgressHelper.fabPadding(has)
    }

    /**
     * Нужен ли отступ от последнего элемента до края списка для размещения ННП.
     * @param has Boolean
     */
    fun navMenuPadding(has: Boolean) {
        bottomLoadMoreProgressHelper.navMenuPadding(has)
    }

    /**
     * Показывать ли индикатор внизу списка.
     * @param value Boolean
     */
    fun loadNextProgressIsVisible(value: Boolean) {
        bottomLoadMoreProgressHelper.setShowProgress(value)
    }

    /**
     * Показывать ли индикатор внизу списка.
     * @param value Boolean
     */
    fun loadPreviousProgressIsVisible(value: Boolean) {
        topLoadMoreProgressHelper.hasLoadMore(value)
    }

    /**
     * Флаг [value] определяет должен ли компонент вызывать колбек, оповещающий о том, что необходима подгрузка
     * следующей страницы данных.
     */
    fun loadNextAvailability(value: Boolean) {
        if (value) needLoadMoreNotifierCallbackHandler.shouldNotifyNext(value)
    }

    /**
     * Флаг [value] определяет должен ли компонент вызывать колбек, оповещающий о том, что необходима подгрузка
     * предыдущей страницы данных.
     */
    fun loadPreviousAvailability(value: Boolean) {
        if (value) needLoadMoreNotifierCallbackHandler.shouldNotifyPrevious(value)
    }

    /**
     * Установить список данных для немедленного отображения.
     * @param data ListData
     */
    fun setListData(data: ListData) {
        listDataHolder.setData(data)
        val lastItemCount = adapter.itemCount
        adapter.setItems(data)
        scrollIfNeeded(data, lastItemCount)
        invalidateItemDecorations()
    }

    /**
     * Снять выделение с элемента списка.
     */
    override fun cleanSelection() {
        decoratorHolder.cleanSelection()
        adapter.notifyItemChanged(adapter.lastHighlightedItemPosition)
        adapter.lastHighlightedItemPosition = NO_ITEM_TO_HIGHLIGHT
    }

    /**
     * Установить режим, при котором нажатый элемент будет оставаться выделенным и отобразится индикатор выделения
     * с края элемента. Доступность нажатия на элемент определяется опциями
     * элемента см. [ru.tensor.sbis.list.view.item.Options]
     */
    override fun highlightSelection() {
        adapter.highlightSelection = true
    }

    /**
     * Подсветить конкретный элемент в списке
     * @param position Int
     */
    override fun highlightItem(position: Int) {
        decoratorHolder.highlightItem(position)
        adapter.notifyItemChanged(position)
        adapter.lastHighlightedItemPosition = position
    }

    /**
     * Подсветить элемент в списке, первый соответствующий положительному результату сравнения [predicate]
     */
    override fun highlightItem(predicate: (AnyItem) -> Boolean) {
        cleanSelection()
        adapter.doWithItemPosition(predicate) { position: Int ->
            highlightItem(position)
        }
    }

    /**
     * Установить слушателя получения "первичных" данных[initialDataAddListener], который будет оповещать когда до
     * обновления данных список был пуст, а после уже что-то содержит.
     */
    // TODO: Удалить после решиня задачи, по задаче добавить опции для скрола к первой позиции не только при первой загрузке
    fun registerInitialDataAddListener(initialDataAddListener: InitialDataAddListener) {
        adapter.registerAdapterDataObserver(
            object : AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    if (adapter.itemCount > 0) initialDataAddListener.onAdd(listDataHolder.getItems())
                }
            }
        )
    }

    /**
     * Установить слушателя изменения данных[dataInsertListener], который будет оповещать
     * когда данные в списке изменились
     */
    fun registerDataInsertListener(dataInsertListener: DataInsertListener) {
        adapter.registerAdapterDataObserver(
            object : AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    dataInsertListener.onInsert(listDataHolder.getItems())
                }
            }
        )
    }

    /**
     * Задать необходимость предотвращения слишком быстрых последовательных нажатий на каждый элемент по отдельности,
     * что делает доступным одновременное нажатие на несколько элементов списка.
     * По умолчанию false - нажатия со слишком маленьким временным интервалом между ними не допускаются как по одному
     * элементу, так и по различным
     */
    fun setShouldThrottleItemClicksSeparately(throttleSeparately: Boolean) {
        adapter.setShouldThrottleItemClicksSeparately(throttleSeparately)
    }

    /**
     * Задать интервал допустимый между повторным нажатием на элемент. По умолчанию интервал равен [ITEM_CLICK_INTERVAL]
     * @param interval время в течении которого клики будут игнорироваться, если передать 0 клики будут обрабатываться
     * мгновенно.
     */
    fun setItemClicksThrottleInterval(interval: Long) {
        adapter.setItemClicksThrottleInterval(interval)
    }

    /**
     * Выполнить скрол до позиции [positionToInitialScroll], если нужен первичный скрол.
     */
    fun initialScrollToPosition(positionToInitialScroll: Int) {
        if (needInitialScroll) scrollToPosition(positionToInitialScroll)
    }

    /**
     * Установить медиатор, который будет получать данные от самого верхнего элемента списка
     */
    fun setHedItemMediator(headItemMediator: HeaderItemMediator) {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topChild: View = getChildAt(0)
                getHeadItemMediator(topChild)?.let {
                    headItemMediator.updateWithData(it, canScrollVertically(-1))
                }
            }
        })
    }

    /**
     * Установить медиатор, который будет получать данные от верхнего элемента списка с учетом сдвига сверху
     */
    fun setHeadItemMediatorWithOffset(headItemMediator: HeaderItemMediator, getTopOffset: () -> Int) {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topOffset = getTopOffset()
                val topChild: View = children.firstOrNull { view -> view.bottom > topOffset } ?: return
                getHeadItemMediator(topChild)?.let {
                    headItemMediator.updateWithData(it, canScrollVertically(-1))
                }
            }
        })
    }

    /**
     * Проскролить элемент из [position] с отступом от верха списка [offset].
     */
    fun scrollWithTopOffset(position: Int, offset: Int) {
        layoutManager.scrollToPositionWithOffset(position, offset)
    }

    /**
     * Поменять элементы списка [AnyItem] местами.
     */
    internal fun reorder(
        draggedPosition: Int,
        targetPosition: Int
    ) {
        val reorderedDataInfo = listDataHolder.reorder(draggedPosition, targetPosition)
        reorderedDataInfo?.let {
            listDataHolder.setData(it)
            adapter.swap(draggedPosition, targetPosition)
        }
    }

    private fun getHeadItemMediator(topChild: View): HeaderItemDataProvider? {
        val topChildPosition = getChildAdapterPosition(topChild)
        if (topChildPosition == NO_POSITION || adapter.itemCount <= topChildPosition) return null
        val topItem = adapter.getItem(topChildPosition)
        return topItem as? HeaderItemDataProvider
    }

    private fun updateDecoration(positionStart: Int) {
        //todo https://online.sbis.ru/opendoc.html?guid=7db07642-7e87-49d7-b30b-39e47c90cd7a&client=3
        /**
         * Для обновления декорации оставшихся элементов. Конкретно, для отступа после последнего элемента.
         */
        if (positionStart > 0 && adapter.itemCount > 0) adapter.notifyItemChanged(positionStart - 1)
    }

    /**
     * Проверяем, нужно ли отключить measurement cache для корректного измерения высоты сворачиваемых ячеек.
     * @see <a href="https://issuetracker.google.com/issues/204709350">Ошибка в IssueTracker</a>
     */
    private fun updateMeasurementCacheEnabled() {
        with(layoutManager) {
            // сначала проверяем spanCount, чтобы избежать потенциально ненужного поиска в списке элементов.
            isMeasurementCacheEnabled = spanCount < 2 || !listDataHolder.hasCollapsibleItems()
        }
    }

    private fun scrollIfNeeded(data: ListData, lastItemCount: Int) {
        if (lastItemCount == 0 && !data.isEmpty()) {
            initialScrollToPosition(data.positionToInitialScroll)
        } else if (data.forceScrollToInitialPosition) {
            scrollToPosition(data.positionToInitialScroll)
        }
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            adapter.destroyDataBindingViewHolders()
            bottomLoadMoreDisposable.dispose()
        }
    }
}

private const val SUPER_STATE = "SUPER_STATE"