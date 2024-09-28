package ru.tensor.sbis.business.common.ui.base.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.base_components.adapter.ItemTypeSpecificDecoration
import ru.tensor.sbis.base_components.adapter.vmadapter.ItemChecker
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.databinding.FragmentBaseCrudListBinding
import ru.tensor.sbis.business.common.ui.base.adapter.BaseListAdapter
import ru.tensor.sbis.business.common.ui.base.contract.ScreenContract
import ru.tensor.sbis.business.common.ui.base.state_vm.InformationVM
import ru.tensor.sbis.business.common.ui.fragment.resetToSummaryScreen
import ru.tensor.sbis.business.common.ui.utils.*
import ru.tensor.sbis.business.common.ui.viewmodel.BaseViewModel
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.CommonUtils.SimpleAnimationListener
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.design.navigation.util.ActiveTabOnClickListener
import ru.tensor.sbis.design.navigation.util.scrollToTop
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.progress.SbisPullToRefresh
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import javax.inject.Inject
import ru.tensor.sbis.design.R as RDesign

/**
 * Базовый фрагмент для визуализации списочных данных в МП Бизнес
 * Используется в разделах Продажи и Деньги
 *
 * @author as.chadov
 *
 * Содержит обработчики:
 * - отложенного создания и отображения фрагмента после окончания анимации (используется опционально)
 * - стандартный сценарий обработки события клавиатуры [AdjustResizeHelper.KeyboardEventListener]
 * - управления отображением клавиатуры и ННП
 * - обработка скролла в самый верх
 * - добавления опционального контента в [ViewStub] макета [FragmentBaseCrudListBinding] вне [RecyclerView]
 *
 * @param VIEW_MODEL тип вью-модели фрагмента унаследованной от базовой [BaseViewModel]
 *
 * @property scrollHelper хэлпер, публикующий события скролла для подписчиков
 * @property rxBus шина для прослушивания событий
 * @property screenReceiverId идентификатор экрана (для получения событий). По-умолчанию именовние класса
 * @property isKeyboardOpen true, если клавиатура видна, иначе false
 * @property shouldShowKeyboardOnViewId id вью для которой необходимо отобразить клавиатуру после смены ориентации
 * @property shouldShowNavigationPanel true, если отображаем ННП при скрытии клавиатуры
 */
@SuppressLint("ShowToast")
@Deprecated("Устарел, не наследовать новые реализаций")
abstract class BaseFragment<VIEW_MODEL> :
    VMFragment<VIEW_MODEL, FragmentBaseCrudListBinding>(),
    AdjustResizeHelper.KeyboardEventListener,
    ActiveTabOnClickListener,
    TransitionAnimator
        where VIEW_MODEL : BaseViewModel,
              VIEW_MODEL : ScreenContract {

    @Inject
    lateinit var scrollHelper: ScrollHelper

    @Inject
    lateinit var rxBus: RxBus

    /** region View refs */
    /**@SelfDocumented */
    protected var refreshView: SbisPullToRefresh? = null

    /**@SelfDocumented */
    protected var recyclerView: RecyclerView? = null

    /** Ссылка на панель поиска [SearchInput] вне [RecyclerView] */
    protected var outerSearchView: SearchInput? = null
    protected var viewModelAdapter: BaseListAdapter? = null
        private set

    /** endregion View refs */

    private var isKeyboardOpen: Boolean = false
    private var isResizedViewOnKeyboardOpen = false
    private var disableExitTransitionAnimation = false

    @IdRes
    private var shouldShowKeyboardOnViewId = 0
    private var searchPanelHeight = 0
    private var bottomNavigationHeight = 0

    override var layoutId = R.layout.fragment_base_crud_list
    open val screenReceiverId: String = this::class.java.canonicalName.orEmpty()
    open val shouldShowNavigationPanel: Boolean = true

    /** Регистрируем пункты списка в адаптере [ViewModelAdapter] */
    protected open fun registerCells() = Unit

    /** Установить пул переиспользуемых вью списка */
    protected open fun provideRecycledViewPool(): RecyclerView.RecycledViewPool? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNavigationHeight = resources.getDimensionPixelSize(RDesign.dimen.bottom_navigation_height)
        registerKeyboardOnScrollEvents()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState).applyTestId(this)
        initViewOutOfList()
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    /** Переопределенное создание и запуск анимации появления фрагмента для выполения действия после окончания анимации */
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val onFinishAction = onEnterAnimationFinished()
        if (enter && nextAnim != 0x0 && onFinishAction != null) {
            val animation = AnimationUtils.loadAnimation(activity, nextAnim)
            animation.setAnimationListener(object : SimpleAnimationListener() {
                override fun onAnimationEnd(animation: Animation) {
                    super.onAnimationEnd(animation)
                    val delay = if (animation.hasEnded()) 0 else DELAY_TO_ENSURE_ANIMATION_END
                    Handler().postDelayed(onFinishAction, delay)
                }
            })
            return animation
        }
        if (!enter && disableExitTransitionAnimation && nextAnim != 0x0) {
            disableExitTransitionAnimation = false
            return object : Animation() {}.apply { duration = 0 }
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    /** Действие после окончания анимации появления фрагмента */
    protected open fun onEnterAnimationFinished(): Runnable? = null

    override fun disableExitTransitionAnimation() {
        disableExitTransitionAnimation = true
    }

    @CallSuper
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            refreshView = refresh
            recyclerView = listContainer.list
        }.also {
            initRefreshView()

            /**
             * При пересоздании фрагмента сохраняем позицию скролла, при первом создании
             * или после вытеснения из памяти скроллим вверх.
             **/
            val scrollToTop = viewModel.list.get()?.isEmpty() ?: true
            initList(scrollToTop)
        }
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        changeViewEnable(true)
        adjustKeyboardAndNavigationPanel()
    }

    override fun onPause() {
        super.onPause()
        changeViewEnable(false)
        if (isKeyboardOpen) {
            findFocusView()?.let { shouldShowKeyboardOnViewId = it.id }
            hideKeyboard()
        }
    }

    override fun onDestroyView() {
        refreshView = null
        recyclerView = null
        outerSearchView = null
        viewModelAdapter?.listUpdateCallback = null
        viewModelAdapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRemoving && shouldShowNavigationPanelOnBack()) {
            scrollHelper.showNavigationPanel()
        }
    }

    /** Метод переопределения базового [LayoutManager] если его возможностей недостаточно */
    protected open fun initLayoutManager(): LinearLayoutManager =
        BaseLinearLayoutManager(requireContext())

    /** Метод описания декораторов, разделителей для использования в [ViewModelAdapter] */
    protected open fun getDecorationData(): RecyclerView.ItemDecoration = ItemTypeSpecificDecoration(listOf())

    /** Возвращает необходимость отобржения ННП при выходе с экрана */
    protected open fun shouldShowNavigationPanelOnBack() = true

    /** region Register cell */
    /** Добавить в адаптер списка вью-модель с типом [ITEM_TYPE] для макета [layoutId] */
    protected inline fun <reified ITEM_TYPE : Any> registerCell(@LayoutRes layoutId: Int) {
        viewModelAdapter?.cell<ITEM_TYPE>(layoutId)
    }

    /** Добавить в адаптер списка вью-модель с типом [ITEM_TYPE] для макета [layoutId] */
    protected inline fun <reified ITEM_TYPE : Any> registerCell(
        @LayoutRes layoutId: Int,
        noinline areItemsTheSame: (ITEM_TYPE, ITEM_TYPE) -> Boolean,
        noinline areContentsTheSame: (ITEM_TYPE, ITEM_TYPE) -> Boolean = { a: ITEM_TYPE, b: ITEM_TYPE -> a == b },
    ) {
        viewModelAdapter?.cell(
            layoutId = layoutId,
            areItemsTheSame = areItemsTheSame,
            areContentsTheSame = areContentsTheSame
        )
    }

    protected inline fun <reified ITEM_TYPE : Any> registerCell(
        @LayoutRes layoutId: Int,
        itemChecker: ItemChecker<ITEM_TYPE>,
    ) {
        viewModelAdapter?.cell(layoutId = layoutId, itemChecker = itemChecker)
    }
    /** endregion Register cell */

    /** region [AdjustResizeHelper.KeyboardEventListener] */
    /**
     * Показ клавиатуры с соответствующей подстройкой разметки.
     * Возвращаемый результат влияет на отработку [ViewHeightResizer] для изменения высоты вью,
     * что требуется для исправления скрытия части заглушки [StubView] под клавиатурой.
     */
    @CallSuper
    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        if (hasNoOverlay(true)) {
            isKeyboardOpen = true
        }
        scrollHelper.hideNavigationPanel()
        isResizedViewOnKeyboardOpen = resizeViewHeightOnKeyboard()
        return !isResizedViewOnKeyboardOpen
    }

    /** Закрытие клавиатуры, изменения в разметке, вызванные клавиатурой, отменяются */
    @CallSuper
    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        val closeOnBackPress = !isKeyboardOpen
        isKeyboardOpen = false
        if (shouldShowNavigationPanel) {
            scrollHelper.showNavigationPanel()
        }
        if (isResizedViewOnKeyboardOpen || closeOnBackPress) {
            return false
        }
        return !resizeViewHeightOnKeyboard()
    }

    override fun onActiveTabClicked(item: NavigationItem) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (firstVisibleItemPosition == 0) {
                resetToSummaryScreen()
            } else {
                recyclerView?.scrollToTop()
            }
        }
    }

    /**
     * Необходима ли отработка [ViewHeightResizer] для изменения высоты вью, что требуется для исправления скрытия
     * части заглушки [StubView] под клавиатурой. Для планшета отработка [ViewHeightResizer]
     * ненужна поскольку вызывает сайд эффекты при смене LayoutParams [FrameLayout] в [ConstraintLayout] и тд.
     *
     * @return true если требуется доп. обработка
     */
    @CallSuper
    protected fun resizeViewHeightOnKeyboard(): Boolean =
        DeviceConfigurationUtils.isTablet(requireContext()).not() && viewModel.list.toList.filterIsInstance<InformationVM>()
            .firstOrNull()?.showIcon?.isTrue ?: false
    /** endregion [AdjustResizeHelper.KeyboardEventListener] */

    //region Keyboard
    /**
     * Скрыть клавиатуру
     * @param force принудительное скрытие даже если фокус не на текущем экране
     * Например для скрытия клавиатуры с "прошлого" экрана при проваливании на новый
     */
    protected fun hideKeyboard(force: Boolean = false) {
        val screenFocus = findFocusView()
        if (screenFocus != null) {
            if (screenFocus is SearchInput) {
                screenFocus.hideKeyboard()
            } else {
                KeyboardUtils.hideKeyboard(screenFocus)
            }
        } else if (force) {
            val view = findFocusView(false)
            view?.clearFocus()
            view?.let(KeyboardUtils::hideKeyboard)
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    protected open fun findFocusView(onScreenOnly: Boolean = true): View? {
        if (binding == null) return null
        return if (onScreenOnly) {
            binding.root.findFocus()
        } else {
            binding.root.rootView.findFocus()
        }.takeIf { it != null && it.parent != null }
    }

    /** Подписаться на событие скролла для изменения видимости клавиатуры и ННП */
    private fun registerKeyboardOnScrollEvents() = addViewDisposable {
        scrollHelper.scrollEventObservable.subscribe { scrollEvent ->
            if (isKeyboardOpen && scrollEvent == ScrollEvent.SCROLL_DOWN) {
                hideKeyboard()
            }
        }
    }

    /** Настроить видимость компановки ННП-клавиатура */
    private fun adjustKeyboardAndNavigationPanel() {
        if (shouldShowKeyboardOnViewId != 0) {
            showKeyboard(shouldShowKeyboardOnViewId)
            shouldShowKeyboardOnViewId = 0
        } else {
            if (hasNoOverlay()) {
                hideKeyboard(true)
                scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE_SOFT)
            }
        }
    }

    /**
     * Отображается ли фрагмент на переднем плане или имеется фрагмент перекрывающий его
     *
     * @param takeChild учитывая дочерние. Например если имеется панель выбра над текущим экраном
     */
    private fun hasNoOverlay(takeChild: Boolean = false): Boolean {
        val hasOverlayFragment = (activity as? OverlayFragmentHolder)?.hasFragment() ?: false
        if (hasOverlayFragment) {
            return false
        }
        return parentFragmentManager.fragments.lastOrNull() == this &&
                takeIf { takeChild }?.childFragmentManager?.fragments?.isEmpty() ?: true
    }


    /** Показать клавиатуру и соответсвенно скрыть ННП */
    private fun showKeyboard(@IdRes viewId: Int = 0) {
        val action = fun(view: View) {
            scrollHelper.hideNavigationPanel()
            val searchInput = view as? SearchInput ?: view.parent as? SearchInput
            if (searchInput != null) {
                searchInput.showKeyboard()
                searchInput.showCursorInSearch()
            } else {
                requireContext()
                KeyboardUtils.showKeyboard(view)
            }
        }
        val focusView = outerSearchView ?: findFocusView()
        if (focusView != null) {
            action(focusView)
        } else {
            recyclerView?.post { recyclerView?.findViewById<View>(viewId)?.let { action(it) } }
        }
    }
    //endregion Keyboard

    /** region Добавление опциональной вью вне списка */
    /**
     * Получить идентификатор макета для опциональной области над реестром.
     * Например используется для строки поиска вне [RecyclerView]
     */
    @LayoutRes
    protected open fun getOptionalViewLayoutRes(): Int = ID_NULL

    /** @SelfDocumented */
    protected open fun onInflateOptionalViewListener(stub: ViewStub, inflated: View) = Unit

    /** Инициализируем опциональный компонент экрана расположенный вне [RecyclerView] */
    private fun initViewOutOfList() {
        val optionalLayoutRes = getOptionalViewLayoutRes()
        if (optionalLayoutRes != 0 && binding.optionalContent.isInflated.not()) {
            binding.optionalContent.viewStub?.apply {
                layoutResource = optionalLayoutRes
                setOnInflateListener { stub, inflated ->
                    onInflateOptionalViewListener(
                        stub = stub,
                        inflated = inflated
                    )
                }
                inflate()
            }
        }
    }
    /** endregion Добавление опциональной вью */

    /** region Инициализация вью */
    private fun initRefreshView() = refreshView?.run {
        setProgressViewOffset(
            true,
            0,
            resources.getDimensionPixelSize(R.dimen.business_refresh_indicator_padding_top)
        )
        setColorSchemeResources(RDesign.color.color_accent)
    }

    private fun initList(needScrollToTop: Boolean) {
        viewModelAdapter = BaseListAdapter().apply {
            listUpdateCallback = object : ViewModelAdapter.ListUpdateCallback {
                override fun onItemsInsertedOnTop(
                    insertPosition: Int,
                    itemCount: Int,
                ) {
                    if (insertPosition == 0 && needScrollToTop) {
                        recyclerView?.scrollToPosition(0)
                    }
                }
            }
        }
        registerCells()
        recyclerView?.apply {
            setHasFixedSize(true)
            provideRecycledViewPool()?.let(::setRecycledViewPool)
            layoutManager = initLayoutManager()
            addOnScrollListener(ScrollListenerForScrollHelper(scrollHelper))
            itemAnimator?.apply {
                moveDuration = 0
                changeDuration = 0
                if (this is DefaultItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
            swapAdapter(viewModelAdapter, true)
            isMotionEventSplittingEnabled = false
            addItemDecoration(getDecorationData())
        }
    }

    /**
     * Обновление состояния вью [View.isEnabled] (необходимо для автотестирования экранов с одинаковой иерархией).
     * Изменение по согласованию с Федянин Н.С. [https://online.sbis.ru/department/54ee0732-30bd-420c-a008-4f322cb97819/]
     *
     * @param isEnabled false если вью для данного экрана игнорируются автотестами
     */
    private fun changeViewEnable(isEnabled: Boolean) {
        binding.root.isEnabled = isEnabled
    }

    /** Обработка отображения ННП после завершения полного расчета макета. */
    @CallSuper
    protected open fun onListLayoutCompleted(layoutManager: LinearLayoutManager) {
        val list = recyclerView ?: return
        if (!isResumed || !hasNoOverlay()) return
        val areAllItemsCompletelyVisible = list.isContentNotCoveredByNavigation()
        if (areAllItemsCompletelyVisible && !isKeyboardOpen) {
            scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE_SOFT)
        }
    }

    /** @true true если содержимое списка не перекрывается ННП */
    private fun RecyclerView.isContentNotCoveredByNavigation(): Boolean {
        val itemCount = adapter?.itemCount ?: return true
        if (childCount < itemCount) return false

        val firstItemTop = getChildAt(0)?.top ?: return true
        val lastItemBottom = getChildAt(itemCount - 1)?.bottom ?: return true
        val height = Rect()
            .apply { getGlobalVisibleRect(this) }
            .height()

        return lastItemBottom - firstItemTop <= height - bottomNavigationHeight
    }

    /** endregion Инициализация вью */

    private inner class BaseLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

        override fun onLayoutCompleted(state: RecyclerView.State?) {
            super.onLayoutCompleted(state)
            onListLayoutCompleted(this)
        }

        /**
         * Disable predictive animations. There is a bug in RecyclerView which causes views that
         * are being reloaded to pull invalid ViewHolders resetNavigation the internal recycler stack if the
         * adapter size has decreased since the ViewHolder was recycled.
         * See: https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
         */
        override fun supportsPredictiveItemAnimations() = false
    }

    companion object {
        /**@SelfDocumented */
        private const val DELAY_TO_ENSURE_ANIMATION_END: Long = 50

        /**
         * Максимальный размер пула ViewHolders для списка по умолчанию.
         * Увеличение значения позволяет не пересоздавать view holders при изменении данных, и
         * следовательно, экономить время на отрисовку
         */
        const val DEFAULT_RECYCLED_VIEW_POOL = 60
    }
}

/**
 * Лямбда сравнения двух объектов, всегда определяющая их как одни и те же.
 * Используется, в частности, при проверке наличия элемента в списке, в котором он всегда единственный
 */
val alwaysTheSame: (Any, Any) -> Boolean = { _, _ -> true }