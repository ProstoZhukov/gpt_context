package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input

import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.databinding.CommunicatorReadStatusListViewBinding
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload.ReadStatusListUpdateActions
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewDependency
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData

/**
 * Вью-модель поисковой строки списка статусов прочитанности сообщения
 * @see [ReadStatusSearchInputVM]
 *
 * @property liveData      параметры состояния
 * @property updateActions действия для вызовов обновлениея списка
 *
 * @author vv.chekurda
 */
internal class ReadStatusSearchInputVMImpl(
    private val liveData: ReadStatusListVMLiveData,
    private val updateActions: ReadStatusListUpdateActions
) : ViewModel(), ReadStatusSearchInputVM {

    private val searchInputVisibilityRule = { isGroup: Boolean, count: Int -> isGroup && count != 1 }

    private var disposer = CompositeDisposable()

    override fun initViewModel(binding: CommunicatorReadStatusListViewBinding, dependency: ReadStatusListViewDependency) {
        disposer.clear()
        binding.communicatorReadStatusSearchInput.run {
            searchQueryChangedObservable()
                .subscribe(::makeSearchRequest)
                .storeIn(disposer)
            cancelSearchObservable()
                .subscribe { cancelSearch() }
                .storeIn(disposer)
            filterClickObservable()
                .subscribe { onFilterClick() }
                .storeIn(disposer)
            searchFieldEditorActionsObservable()
                .subscribe(::handleEditorAction)
                .storeIn(disposer)
            searchFocusChangeObservable()
                .subscribe(::onFocusChanged)
                .storeIn(disposer)
        }
        liveData.setIsGroupConversation(dependency.isGroupConversation)
        liveData.filterChangedObservable
            .distinctUntilChanged()
            .subscribe { updateActions.reloadList() }
            .storeIn(disposer)
        Observable.combineLatest(
            liveData.isGroupConversation,
            liveData.receiversCount,
            searchInputVisibilityRule)
            .subscribe(liveData::setSearchInputVisibility)
            .storeIn(disposer)
    }

    override fun selectFilter(filter: MessageReadStatus) {
        liveData.setSearchFilter(filter)
    }

    override fun onMessageReceiversCountChanged(count: Int) {
        liveData.setMessageReceiversCount(count)
    }

    /**
     * Сделать поисковый запрос
     * @param query значение поиска
     */
    private fun makeSearchRequest(query: String) {
        if (query != liveData.searchQuery.value) {
            liveData.setSearchQuery(query)
            updateActions.refreshList()
        }
    }

    /**
     * Закрыть поиск
     */
    private fun cancelSearch() {
        makeSearchRequest(StringUtils.EMPTY)
        liveData.dispatchCancelSearch()
    }

    /**
     * Клик по фильтру
     */
    private fun onFilterClick() {
        liveData.dispatchFilterClick()
    }

    /**
     * Обработка действий реадиктирования поисковой строки
     */
    private fun handleEditorAction(actionId: Int) {
        if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH) {
            liveData.dispatchHideKeyboard()
        }
    }

    private fun onFocusChanged(isFocused: Boolean) {
        liveData.dispatchFocusChanged(isFocused)
    }

    override fun onCleared() {
        super.onCleared()
        disposer.dispose()
    }
}