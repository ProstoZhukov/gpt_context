package ru.tensor.sbis.crud3

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.crud3.domain.PageDirection
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.crud3.view.DefaultStubViewContentFactory
import ru.tensor.sbis.crud3.view.FirstItemFactory
import ru.tensor.sbis.crud3.view.Refreshable
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.progress.SbisPullToRefresh
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.ForceBackgroundColor
import ru.tensor.sbis.list.view.utils.DataInsertListener
import ru.tensor.sbis.list.view.utils.HeaderItemMediator
import timber.log.Timber

/**
 *
 * Списочный компонент для отображения CRUD3 коллекции.
 * См. README.md
 */
class ListComponentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), ListComponentViewConfig, ListLoadingIndicatorConfig {
    private var disposable = CompositeDisposable()
    private lateinit var viewModel: ListComponentViewViewModel<ItemWithSection<AnyItem>, *, *>
    private val firstItemFactories: MutableList<FirstItemFactory<*>> = mutableListOf()
    private var refreshLayout: SbisPullToRefresh? = null
    private var forceBackgroundColor: ForceBackgroundColor = ForceBackgroundColor.NONE
    private var priorityBackgroundColor: Int? = null
    private var forceScrollToInitialPosition: Boolean = false

    /**
     * Контейнер для списка, индикатора прогресса и заглушки.
     */
    private val listContainer = FrameLayout(getContext(), attrs).apply { id = R.id.crud3_list_container_id }
    private val progressBar = SbisLoadingIndicator(getContext(), attrs).apply { id = R.id.crud3_progress_bar_id }
    private val stub = StubView(getContext(), attrs).apply { id = R.id.crud3_stub_id }
    private var attached = false
    val list = SbisList(getContext(), attrs).apply {
        id = R.id.crud3_list_id
        // TODO Удалить после решения https://online.sbis.ru/opendoc.html?guid=bc81d45c-39cc-4a30-8b70-b7cb4429eaf5&client=3
        needInitialScroll = false
    }

    /**
     * Подавить отображение индикатора загрузки всего списка.
     */
    override var suppressCenterLoadIndicator: Boolean = false

    /**
     * Подавить отображение заглушки.
     */
    override var suppressStubs: Boolean = false

    /**
     * Подавить отображение индикатора загрузки следующей страницы.
     */
    override var suppressLoadNextIndicator: Boolean = false

    /**
     * Подавить отображение индикатора загрузки предыдущей страницы.
     */
    override var suppressLoadPrevIndicator: Boolean = false

    /**
     * Возвращает view заглушки, для кастомизации внешнего вида.
     */
    val stubView: View = stub

    fun registerDataInsertListener(dataInsetsListener: DataInsertListener) {
        list.registerDataInsertListener(dataInsetsListener)
    }

    /**
     * Установить медиатор, который будет получать данные от самого верхнего элемента списка
     */
    fun setHeadItemMediator(headItemMediator: HeaderItemMediator) {
        list.setHedItemMediator(headItemMediator)
    }

    private val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            viewModel.onScroll(recyclerView, dx, dy)
        }
    }

    init {
        listContainer.addView(list, MATCH_PARENT, MATCH_PARENT)
        listContainer.addView(stub, MATCH_PARENT, MATCH_PARENT)
        listContainer.addView(progressBar, LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        })

        val horizontal: Boolean
        val isUsedSwipeRefreshLayout: Boolean

        with(context.obtainStyledAttributes(attrs, R.styleable.ListComponentView, 0, 0)) {
            forceBackgroundColor = ForceBackgroundColor.values()[
                getInt(R.styleable.ListComponentView_ListComponentView_forceBackgroundColor, 0)
            ]
            forceScrollToInitialPosition =
                getBoolean(R.styleable.ListComponentView_ListComponentView_forceScrollToInitialPosition, false)
            horizontal = getBoolean(R.styleable.ListComponentView_ListComponentView_isHorizontal, false)
            isUsedSwipeRefreshLayout =
                getBoolean(R.styleable.ListComponentView_ListComponentView_isUsedSwipeRefreshLayout, true)
        }

        if (isUsedSwipeRefreshLayout) {
            refreshLayout = SbisPullToRefresh(getContext(), attrs).apply {
                id = R.id.crud3_refresh_layout_id
                addView(listContainer, MATCH_PARENT, MATCH_PARENT)
            }
            addView(refreshLayout)
        } else {
            addView(listContainer)
        }

        isHorizontal(horizontal)
    }

    private val viewTreeLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            disposable.dispose()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()!!.lifecycle.addObserver(viewTreeLifecycleObserver)
        bindViewModel()
        children.forEach {
            if (it != refreshLayout && it != listContainer) {
                this.removeView(it)
                listContainer.addView(it)
            }
        }
        attached = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attached = false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        (refreshLayout ?: listContainer).layout(0, 0, right, bottom)
    }

    /**
     * Инициализировать компонент и получить интерфейс внешней манипуляции. Полученную ссылку можно удерживать без
     * ограничений в пределах ЖЦ [viewModelStoreOwner].
     * Ссылки на lazy лямбды для [wrapper], [mapper] и [stubFactory] будут освобождены сразу, по окончанию работы
     * метода.
     * Параметр [pageSize] задает размер страницы данных, которая должна включать как вмещающиеся на экран количество
     * элементов [viewPostSize], так и запас вне экрана для своевременного запроса следующих страниц для пагинации.
     * По умолчанию установлено значение [pageSize] равно [defaultPageSize] элементов а [viewPostSize] равно [defaultPageSize] / 3, для большинства случаев,
     * когда высота ячейки больше 2 строк, этого должно хватать, если ячейка узкая по высоте, стоит взять значение
     * больше 100.
     * @param firstItemFactory фабрика кастомного элемента который будет помещен в начало списка.
     * @param viewModelKey ключ для сохранения вью модели. Если на экране несколько списков, необходимо задавать разные ключи
     */
    @MainThread
    fun <COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM : Any> inject(
        viewModelStoreOwner: ViewModelStoreOwner,
        wrapper: Lazy<Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM>>,
        mapper: Lazy<ItemInSectionMapper<ITEM, AnyItem>>,
        stubFactory: Lazy<StubFactory> = lazy { DefaultStubViewContentFactory() },
        firstItemFactory: Lazy<FirstItemFactory<*>>? = null,
        pageSize: Int = defaultViewPostSize * 3,
        viewPostSize: Int = defaultViewPostSize,
        viewModelKey: String = "",
        initialPageDirection: PageDirection = PageDirection.FORWARD
    ): ListComponent<FILTER, ITEM, AnyItem> {

        bindViewModel(
            createListComponentViewViewModel(
                viewModelStoreOwner,
                wrapper,
                mapper,
                stubFactory,
                pageSize,
                viewPostSize,
                viewModelKey,
                initialPageDirection
            ),
            firstItemFactory?.value
        )

        @Suppress("UNCHECKED_CAST")
        return viewModel as ListComponent<FILTER, ITEM, AnyItem>
    }

    /**
     * Инициализировать компонент созданной через [createComponentViewModel] вьюмоделью [ComponentViewModel].
     */
    fun bindViewModel(
        componentViewModel: ListComponentViewViewModel<ItemWithSection<AnyItem>, *, *>,
        firstItemFactory: FirstItemFactory<*>? = null
    ) {
        bindViewModel(
            componentViewModel = componentViewModel,
            firstItemFactories = firstItemFactory?.let { listOf(it) } ?: emptyList()
        )
    }

    /**
     * Инициализировать компонент созданной через [createComponentViewModel] вьюмоделью [ComponentViewModel].
     */
    fun bindViewModel(
        componentViewModel: ListComponentViewViewModel<ItemWithSection<AnyItem>, *, *>,
        firstItemFactories: List<FirstItemFactory<*>>
    ) {
        viewModel = componentViewModel
        this.firstItemFactories.clear()
        this.firstItemFactories.addAll(firstItemFactories)
        if (attached) bindViewModel()
    }

    private fun bindViewModel() {
        if (!this::viewModel.isInitialized) return

        viewModel.scrollToPositionEvent.observe {
            list.scrollToPosition(it)
        }
        list.addOnScrollListener(listener)

        val firstItemsSections: List<ItemWithSection<*>> =
            if (viewModel.isZeroPage) {
                firstItemFactories.map { it.create() }
            } else {
                emptyList()
            }
        refreshLayout?.setOnRefreshListener {
            viewModel.refresh()
            firstItemsSections.forEach { (it.item as? Refreshable)?.refresh() }
        }
        viewModel.refreshIsAvailable.observe {
            Timber.d("refreshIsAvailable - $it")
            refreshLayout?.isEnabled = it
        }
        viewModel.isRefreshing.observe {
            Timber.d("isRefreshing - $it")
            refreshLayout?.isRefreshing = it
        }
        viewModel.centralThrobberVisibility.observe {
            if (suppressCenterLoadIndicator) {
                progressBar.visibility = GONE
                return@observe
            }
            Timber.d("centralThrobberVisibility - $it")
            progressBar.visibility = it.toVisibility()
        }
        viewModel.stubVisibility.observe {
            if (suppressStubs) {
                stub.visibility = GONE
                return@observe
            }
            Timber.d("stubVisibility - $it")
            stub.visibility = it.toVisibility()
            if (it && firstItemFactories.isNotEmpty() && list.childCount > 0) {
                stub.updateLayoutParams<LayoutParams> {
                    setMargins(stub.marginLeft, list.children.first().height, stub.marginRight, stub.marginBottom)
                }
            }
        }
        viewModel.stubFactory.observe { it.let { stub.setContent(it) } }
        list.bindWithViewModel(viewModel, this)
        disposable.add(
            list.subscribeToDataChange(
                viewModel.dataChangeMapped,
                firstItemsSections,
                forceBackgroundColor,
                forceScrollToInitialPosition = forceScrollToInitialPosition,
                priorityBackgroundColor = priorityBackgroundColor
            )
        )
    }

    /**
     * Должен ли список быть горизонтальным
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun isHorizontal(orientation: Boolean) {
        list.isHorizontal(orientation)
    }

    /**
     * Проскролить элемент из [position] с отступом от верха списка [offset].
     */
    fun scrollWithTopOffset(position: Int, offset: Int) {
        list.scrollWithTopOffset(position, offset)
    }

    /**
     * Установить медиатор, который будет получать данные от верхнего элемента списка с учетом сдвига сверху
     */
    fun setHeadItemMediatorWithOffset(headItemMediator: HeaderItemMediator, getTopOffset: () -> Int) {
        list.setHeadItemMediatorWithOffset(headItemMediator, getTopOffset)
    }

    /**
     * TODO: временное решение, будет сделано по https://online.sbis.ru/opendoc.html?guid=35b15db1-7537-4bfa-b9c6-9bf75523d4a0&client=3
     *
     * Установить приоритетный цвет фона списка
     */
    @Deprecated("Временное решение")
    fun setPriorityBackgroundColor(color: Int) {
        priorityBackgroundColor = color
    }

    private fun Boolean.toVisibility() = if (this) VISIBLE else GONE
    private fun <T> LiveData<T>.observe(observer: Observer<T>) {
        observe(findViewTreeLifecycleOwner()!!, observer)
    }
}
