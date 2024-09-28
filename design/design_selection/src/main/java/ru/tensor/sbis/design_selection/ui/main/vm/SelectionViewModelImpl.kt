package ru.tensor.sbis.design_selection.ui.main.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener.SelectedItemClickListener
import ru.tensor.sbis.design_selection.domain.completion.ApplySelection
import ru.tensor.sbis.design_selection.domain.completion.CancelSelection
import ru.tensor.sbis.design_selection.domain.completion.CompleteEvent
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonStrategy
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionLogger
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonDelegate
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonLiveData
import java.util.concurrent.TimeUnit

/**
 * Реализация вью-модели компонента выбора.
 *
 * @property selectedItemsClickListener слушатель кликов по выбранным элементам.
 * @property rulesHelper вспомогательная реализация для определения правил выбора.
 * @param doneButtonDelegate делегат для работы с кнопкой подтверждения выбора.
 * @param headerButtonContract контракт для работы с головной кнопкой.
 * @param uiSchedule планировщик главного потока.
 *
 * @author vv.chekurda
 */
internal class SelectionViewModelImpl<ITEM : SelectionItem>(
    override val selectedItemsClickListener: SelectedItemClickListener<ITEM>,
    private val rulesHelper: SelectionRulesHelper,
    doneButtonDelegate: DoneButtonDelegate<ITEM>,
    headerButtonContract: HeaderButtonContract<ITEM, FragmentActivity>? = null,
    uiSchedule: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel(),
    SelectionViewModel<ITEM>,
    DoneButtonLiveData by doneButtonDelegate {

    private val completeSubject = PublishSubject.create<CompleteEvent<ITEM>>()
    private val selectedDataSubject = BehaviorSubject.createDefault(SelectedData<ITEM>())
    private val errorMessageSubject = PublishSubject.create<String>()

    private var router: SelectionRouter? = null

    private var isCompleted = false

    private val disposer = CompositeDisposable()

    override val selectedDataObservable: Observable<SelectedData<ITEM>> = selectedDataSubject

    private val _selectedDataWatcher = BehaviorSubject.createDefault(SelectedData<SelectionItem>())

    override val selectedItemsWatcher: Observable<SelectedData<SelectionItem>>
        get() = _selectedDataWatcher.observeOn(AndroidSchedulers.mainThread()).share()

    override val searchQuery: MutableStateFlow<String> = MutableStateFlow(StringUtils.EMPTY)

    override val resetScroll = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)

    override val hasSelectableItems: MutableStateFlow<Boolean> = MutableStateFlow(true)

    override val selectedData: SelectedData<ITEM>
        get() = selectedDataSubject.value!!

    override val result: Observable<CompleteEvent<ITEM>> = completeSubject

    override val errorMessage: Observable<String> = errorMessageSubject
        .throttleFirst(LIMIT_EXCEED_THROTTLE_TIME_MS, TimeUnit.MILLISECONDS)
        .observeOn(uiSchedule)

    override val isHeaderButtonVisible = BehaviorSubject.createDefault(headerButtonContract != null)
    override val updateConfig = PublishSubject.create<SelectionConfig>()
    override val onDoneButtonClickedObservable = PublishSubject.create<Unit>()
    override val clearSelectedObservable = PublishSubject.create<Unit>()

    init {
        SelectionLogger.onStartSession(rulesHelper.config)
        selectedDataSubject.skip(1)
            .filter { !it.isUserSelection }
            .firstElement()
            .subscribe(doneButtonDelegate::setInitialData)
            .storeIn(disposer)
        selectedDataSubject.subscribe { selectedData ->
            doneButtonDelegate.setSelectedData(selectedData)
            notifyWatcher(selectedData)
        }.storeIn(disposer)
    }

    override fun onHeaderButtonClicked(strategy: HeaderButtonStrategy) {
        isHeaderButtonVisible.onNext(!strategy.hideButton)
        if (strategy.newConfig != null) {
            router?.closeAllFolders()
            updateConfig.onNext(strategy.newConfig)
        }
    }

    override fun onError(errorMessage: String) {
        errorMessageSubject.onNext(errorMessage)
    }

    override fun onDoneButtonClicked() {
        onDoneButtonClickedObservable.onNext(Unit)
    }

    override fun setSelectedData(selectedData: SelectedData<ITEM>) {
        selectedDataSubject.onNext(selectedData)
    }

    override fun cancel() {
        SelectionLogger.onCancel()
        isCompleted = true
        completeSubject.onNext(CancelSelection)
    }

    override fun complete(result: SelectionComponentResult<ITEM>) {
        SelectionLogger.onComplete(result.items.map { it.id })
        if (rulesHelper.isFinalComplete) {
            isCompleted = true
        }
        completeSubject.onNext(ApplySelection(result))
        notifyWatcher(SelectedData(result.items, isUserSelection = true, isMultiSelection = false))
    }

    override fun setRouter(router: SelectionRouter?) {
        this.router = router
    }

    override fun onBackPressed(): Boolean =
        when {
            isCompleted -> false
            router?.back() == false -> {
                cancel()
                true
            }
            else -> true
        }

    override fun onCleared() {
        SelectionLogger.onEndSession()
        disposer.dispose()
    }

    override fun unselectItem(item: SelectionItem) {
        @Suppress("UNCHECKED_CAST")
        selectedItemsClickListener.onUnselectClicked(item as ITEM, animate = false)
    }

    override fun closeAllFolders() {
        router?.closeAllFolders()
    }

    override fun resetScroll() {
        resetScroll.tryEmit(Unit)
    }

    private fun notifyWatcher(selectedData: SelectedData<ITEM>) {
        @Suppress("UNCHECKED_CAST")
        _selectedDataWatcher.onNext(selectedData as SelectedData<SelectionItem>)
    }
}

private const val LIMIT_EXCEED_THROTTLE_TIME_MS = 300L