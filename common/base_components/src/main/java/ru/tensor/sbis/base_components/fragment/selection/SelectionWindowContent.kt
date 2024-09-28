package ru.tensor.sbis.base_components.fragment.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.R
import ru.tensor.sbis.base_components.databinding.BaseComponentsSelectionWindowHeaderBinding
import ru.tensor.sbis.base_components.fragment.selection.shadow.SelectionWindowShadowBehavior
import ru.tensor.sbis.base_components.fragment.selection.shadow.ShadowVisibilityDispatcher
import ru.tensor.sbis.common_filters.FilterWindowHeaderItem
import ru.tensor.sbis.design.utils.mergeAttrsWithCurrentTheme
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.containerAs
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.containerIs

/**
 * Окно выбора
 * http://axure.tensor.ru/MobileStandart8/#p=%D0%BE%D0%BA%D0%BD%D0%BE_%D0%B2%D1%8B%D0%B1%D0%BE%D1%80%D0%B0__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_3_&g=1
 */
abstract class SelectionWindowContent : BaseFragment(), Content, ViewDependencyProvider {

    /**
     * Корневой view.
     */
    private lateinit var mWindowContainer: SelectionWindowLayout

    /**
     * Верняя часть окна с кнопкой "Закрыть".
     */
    private lateinit var mCloseHood: View

    /**
     * Контейнер шапки окна.
     */
    private lateinit var mHeaderContainer: FrameLayout

    /**
     * Тень шапки окна.
     */
    private lateinit var mHeaderShadow: View

    /**
     * Контейнер контента.
     */
    private lateinit var mContentContainer: FrameLayout

    /**
     * Кнопка "Применить" в шапке
     */
    private lateinit var mAcceptButton: View

    private var defaultHeaderBinding: BaseComponentsSelectionWindowHeaderBinding? = null

    /**
     * Задаёт вьюмодель для конфигурации содержимого стандартного заголовка
     *
     * @throws IllegalStateException при попытке вызова, если метод создания заголовка ещё не вызван из [onCreateView],
     * либо переопределён
     */
    fun setHeaderViewModel(vm: FilterWindowHeaderItem) {
        val binding = checkNotNull(defaultHeaderBinding) {
            "Cannot set viewModel. Make sure header view is created and you are not using custom header"
        }
        binding.viewModel = vm.run { copy(hasButton = hasButton) }
        if (binding.root.isAttachedToWindow) {
            binding.executePendingBindings()
        }
    }

    /**
     * Обновляет вьюмодель стандартного заголовка
     *
     * ```
     * val content: SelectionWindowContent
     * //...
     * content.updateHeaderViewModel {
     *     copy(hasBackArrow = false)
     * }
     * ```
     *
     * @param update лямбда, в которой доступна текущая вьюмодель для формирования обновлённой на её основе
     */
    fun updateHeaderViewModel(update: FilterWindowHeaderItem.() -> FilterWindowHeaderItem) {
        val viewModel = defaultHeaderBinding?.viewModel
            ?: FilterWindowHeaderItem()
        setHeaderViewModel(
            viewModel.update().run { copy(hasButton = hasButton) }
        )
    }

    /**
     * Включить/отключить скроллирование шапки.
     */
    protected fun setHoodScrollingEnabled(enable: Boolean) {
        mWindowContainer.isHoodScrollingEnabled = enable
    }

    /**
     * Добавляет в контейнер [View] заголовка.
     * Переопределять метод следует только в случае крайней необходимости, если вид заголовка принципиально отличается
     * от описанного в спецификации (http://axure.tensor.ru/MobileStandart8/#p=окно_выбора__версия_3_&g=1).
     * В реализации по умолчанию используется стандартный заголовок, для конфигурации которого доступен метод
     * [setHeaderViewModel]
     */
    protected open fun inflateHeaderView(inflater: LayoutInflater, container: ViewGroup) {
        container.addView(createDefaultHeader(inflater, container))
    }

    /**
     * Наполнить контент.
     */
    protected abstract fun inflateContentView(inflater: LayoutInflater, container: ViewGroup)

    /**
     * Инициализируем компоненты экрана.
     */
    @CallSuper
    protected open fun initViews(root: View, savedInstanceState: Bundle?) {
        mWindowContainer = root.findViewById(R.id.base_components_window_container)
        mCloseHood = root.findViewById(R.id.base_components_hood_container)
        mHeaderContainer = root.findViewById(R.id.base_components_header_container)
        mHeaderShadow = root.findViewById(R.id.base_components_app_bar_shadow)
        mContentContainer = root.findViewById(R.id.base_components_content_container)
        mAcceptButton = root.findViewById(R.id.base_components_header_button_accept)
        val searchViewLayoutRes = getSearchViewLayoutRes()
        if (searchViewLayoutRes != 0) {
            // Отображаем разделитель
            root.findViewById<View>(R.id.base_components_search_view_divider).visibility = View.VISIBLE
            // Добавляем панель поиска
            val searchStub = root.findViewById<ViewStub>(R.id.base_components_search_view_stub)
            searchStub.layoutResource = searchViewLayoutRes
            val searchView = searchStub.inflate()
            onSearchViewInflated(searchView)
        }

        mAcceptButton.visibility = if (isAcceptButtonVisible()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        mCloseHood.visibility = if (hasCloseHood() && isCloseHoodVisible()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (!hasHeader()) setHeaderContainerVisible(false)

        if (!hasHeaderDivider()) {
            root.findViewById<View>(R.id.base_components_header_divider).visibility = View.GONE
        }
    }

    /**
     * Действие на нажатие кнопки-галочки
     */
    @CallSuper
    protected open fun onApplyClick() = requestCloseContainer()

    /**
     * Действие на нажатие кнопки-крестика
     */
    @CallSuper
    protected open fun onCloseClick() = requestCloseContainer()

    /**
     * Инициализируем слушатели.
     */
    @CallSuper
    protected open fun initListeners(savedInstanceState: Bundle?) {
        mAcceptButton.setOnClickListener { onApplyClick() }
        mCloseHood.setOnClickListener { onCloseClick() }
    }


    /**
     * Возвращает диспетчер, отвечающий за видимость тени под заголовом.
     */
    protected open fun getShadowVisibilityDispatcher(): ShadowVisibilityDispatcher? = null

    /**
     * Получить идентификатор макета для панели поиска. Для того, чтобы
     * избежать создания панели поиска верните значение 0 из данного метода.
     * По умолчанию панель поиска отсутствует.
     */
    @LayoutRes
    protected open fun getSearchViewLayoutRes(): Int {
        return 0
    }

    /**
     * Обработать событие создания панели поиска. Переопределите этот метод,
     * если вам нужно сконфигурировать панель поиска.
     */
    protected open fun onSearchViewInflated(view: View) = Unit
    // endregion

    // region Request Container methods

    /**
     * Запросить у контейнера закрытие.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun requestCloseContainer() {
        containerAs<Container.Closeable>()?.closeContainer()
    }

    // endregion
    /** @SelfDocumented */
    protected open fun isAcceptButtonVisible(): Boolean = false

    /** @SelfDocumented */
    protected open fun isCloseHoodVisible(): Boolean = true

    /** @SelfDocumented */
    protected open fun hasHeader(): Boolean = true

    /** @SelfDocumented */
    protected open fun hasHeaderDivider(): Boolean = true

    private fun hasCloseHood(): Boolean {
        return !containerIs<TabletContainerDialogFragment>() &&
            containerAs<Content>()?.containerIs<TabletContainerDialogFragment>() != true
    }

    /**
     * Задать видимость кнопки подтверждения в шапке
     */
    protected fun setAcceptButtonVisible(visible: Boolean) {
        mAcceptButton.visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * Задать видимость кнопки закрытия
     */
    protected fun setCloseHoodVisible(isVisible: Boolean) {
        mCloseHood.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    /**
     * Задаёт видимость контейнера заголовка. Видимость по-умолчанию определяет метод [hasHeader].
     * @param isVisible true - видимый, false - скрытый.
     */
    protected fun setHeaderContainerVisible(isVisible: Boolean) {
        val newVisibility = if (isVisible) View.VISIBLE else View.GONE
        mHeaderContainer.visibility = newVisibility
        mHeaderShadow.visibility = newVisibility
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().mergeAttrsWithCurrentTheme(
            R.attr.base_components_selection_window_content_theme,
            R.style.SelectionWindowContentTheme
        )
        val root = inflater.inflate(R.layout.base_components_content_selection_window, container, false)
        // Инициализируем компоненты экрана
        initViews(root, savedInstanceState)
        // Наполняем шапку
        inflateHeaderView(inflater, mHeaderContainer)
        // Наполняем контентную область
        inflateContentView(inflater, mContentContainer)
        // Задаем диспетчер видимости тени
        initShadowVisibilityDispatcher()
        // Инициализируем слушатели
        initListeners(savedInstanceState)
        return root
    }

    override fun onDestroyView() {
        defaultHeaderBinding = null
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        onCloseClick()
        return true
    }

    private fun createDefaultHeader(inflater: LayoutInflater, container: ViewGroup): View {
        return BaseComponentsSelectionWindowHeaderBinding.inflate(inflater, container, false)
            .apply { viewModel = FilterWindowHeaderItem() }
            .also { defaultHeaderBinding = it }
            .root
    }

    /**
     * Инициализируем диспетчер видимости тени.
     */
    private fun initShadowVisibilityDispatcher() {
        val dispatcher = getShadowVisibilityDispatcher()
        val params = mHeaderShadow.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            val behavior = params.behavior
            if (behavior is SelectionWindowShadowBehavior) {
                behavior.setDispatcher(dispatcher)
                behavior.setupDependency(this)
            }
        }
    }
}