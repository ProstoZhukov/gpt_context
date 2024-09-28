package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.databinding.CommunicatorReadStatusListViewBinding
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.ReadStatusListView
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewDependency
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.DaggerReadStatusListViewComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusListFilterSelector
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusMessageReceiversListener
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.R
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import java.util.*

/**
 * Интерфейс вспомогательного класса view списка статусов прочитанности сообщения
 * @see [ReadStatusListView]
 * @see [ReadStatusListStateHelper]
 * @see [ReadStatusListFilterSelector]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListLayoutHelper :
    ReadStatusListStateHelper,
    ReadStatusListFilterSelector,
    ReadStatusMessageReceiversListener,
    LifecycleObserver {

    /**
     * Инициализация вспомогательного класса
     *
     * @param dependency зависимости для инициализации
     */
    fun initHelper(dependency: ReadStatusListViewDependency)
}

/**
 * Вспомогательный класс view списка статусов прочитанности сообщения
 * @see [ReadStatusListLayoutHelper]
 *
 * @property binding          binding вью списка статусов прочитанности сообщения
 * @property keyboardDelegate делегат обработки событий клавиатуры
 */
internal class ReadStatusListLayoutHelperImpl private constructor(
    private var binding: CommunicatorReadStatusListViewBinding?,
    private val keyboardDelegate: ReadStatusListKeyboardHelper
) : ReadStatusListLayoutHelper,
    ReadStatusListStateHelper by keyboardDelegate {

    constructor(
        binding: CommunicatorReadStatusListViewBinding?
    ) : this(binding, ReadStatusListKeyboardDelegate(binding))

    private lateinit var component: ReadStatusListViewComponent

    private var disposer: CompositeDisposable? = CompositeDisposable()

    override fun initHelper(dependency: ReadStatusListViewDependency) {
        initComponent(dependency)
        val viewLifecycleOwner = dependency.fragment.viewLifecycleOwner
        binding!!.also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = component.readStatusListVm.apply {
                initViewModel(it, dependency)
                itemClickObservable
                    .subscribe(this@ReadStatusListLayoutHelperImpl::showPersonCard)
                    .storeIn(requireDisposer())
                filterClickObservable
                    .subscribe(this@ReadStatusListLayoutHelperImpl::showFilterSelection)
                    .storeIn(requireDisposer())
                cancelSearchObservable
                    .subscribe { cancelSearch() }
                    .storeIn(requireDisposer())
                hideKeyboardObservable
                    .subscribe { keyboardDelegate.handleHideKeyboardAction() }
                    .storeIn(requireDisposer())
                focusChangedObservable
                    .subscribe(dependency.focusChangeListener::onFocusChanged)
                    .storeIn(requireDisposer())
            }
        }
        dependency.fragment.lifecycle.addObserver(this)
    }

    override fun selectFilter(filter: MessageReadStatus) {
        binding?.viewModel?.selectFilter(filter)
    }

    override fun onMessageReceiversCountChanged(count: Int) {
        binding?.viewModel?.onMessageReceiversCountChanged(count)
    }

    /**
     * Показать карточку сотрудника
     *
     * @param personUuid идентификатор сотрудника
     */
    private fun showPersonCard(personUuid: UUID) {
        component.router.showProfile(personUuid)
    }

    /**
     * Показать меню выбора фильтра
     *
     * @param currentFilter текущий выбранный фильтр
     */
    private fun showFilterSelection(currentFilter: MessageReadStatus) {
        component.run {
            val anchor = binding!!.communicatorReadStatusSearchInput
            val sbisMenu = SbisMenu(
                children = getOptions().map { getOptionMenuItem(it, it.isEqual(currentFilter)) },
            )
            sbisMenu.showMenuWithLocators(
                fragmentManager = fragment.childFragmentManager,
                verticalLocator = AnchorVerticalLocator(
                    alignment = VerticalAlignment.BOTTOM,
                    force = false,
                    offsetRes = R.dimen.context_menu_anchor_margin,
                ).apply { anchorView = anchor },
                horizontalLocator = AnchorHorizontalLocator(
                    alignment = HorizontalAlignment.RIGHT,
                    force = false,
                    innerPosition = true,
                    offsetRes = R.dimen.context_menu_horizontal_margin,
                ).apply { anchorView = anchor },
                dimType = DimType.SOLID,
            )
        }
    }

    private fun getOptionMenuItem(option: ReadStatusFilterOption, isChecked: Boolean = false) = MenuItem(
        title = component.fragment.getString(option.textRes),
        state = if (isChecked) MenuItemState.ON else MenuItemState.MIXED,
    ) {
        selectFilter(option.toMessageReadStatus())
    }

    /**
     * Закрыть поиск
     */
    private fun cancelSearch() {
        binding?.communicatorReadStatusSearchInput?.setSearchText(StringUtils.EMPTY)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        keyboardDelegate.viewIsResumed()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPaused() {
        keyboardDelegate.viewIsPaused()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        keyboardDelegate.cleanReferences()
        disposer?.dispose()
        disposer = null
        binding = null
    }

    private fun requireDisposer(): CompositeDisposable =
        disposer!!

    /**
     * Инициализация di компонента
     *
     * @param dependency зависимости для инициализации [ReadStatusListViewDependency]
     */
    private fun initComponent(dependency: ReadStatusListViewDependency) {
        with(dependency) {
            component = DaggerReadStatusListViewComponent.factory()
                .create(
                    singletonComponent,
                    fragment,
                    messageUuid,
                    communicatorConversationRouter
                )
        }
    }
}