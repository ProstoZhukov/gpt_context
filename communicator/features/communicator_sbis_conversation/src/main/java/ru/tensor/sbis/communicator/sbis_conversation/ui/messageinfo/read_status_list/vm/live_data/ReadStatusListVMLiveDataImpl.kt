package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.communicator.common.util.asMutable
import ru.tensor.sbis.communicator.common.util.asSubject
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import java.util.*
import javax.inject.Inject

/**
 * Параметры состояния вью-модели списка статусов прочитанности сообщения
 * @see [ReadStatusListVMLiveData]
 *
 * @author vv.chekurda
 */
internal class ReadStatusListVMLiveDataImpl @Inject constructor() : ReadStatusListVMLiveData {

    override val itemClickObservable: Observable<UUID> = PublishSubject.create()
    override val filterClickObservable: Observable<MessageReadStatus> = PublishSubject.create()
    override val cancelSearchObservable: Observable<Unit> = PublishSubject.create()
    override val hideKeyboardObservable: Observable<Unit> = PublishSubject.create()
    override val searchQuery: LiveData<String> = MutableLiveData(EMPTY)
    override val searchFilter: LiveData<MessageReadStatus> = MutableLiveData(MessageReadStatus.ALL)
    override val filterChangedObservable: Observable<MessageReadStatus> = PublishSubject.create()
    override val searchInputFilter: LiveData<MessageReadStatus> = searchFilter
    override val isGroupConversation: Observable<Boolean> = BehaviorSubject.createDefault(false)
    override val receiversCount: Observable<Int> = BehaviorSubject.createDefault(0)
    override val searchInputVisibility: LiveData<Int> = MutableLiveData(View.VISIBLE)
    override val focusChangedObservable: Observable<Boolean> = PublishSubject.create()
    override val networkErrorObservable: Observable<Unit> = PublishSubject.create()

    override fun setSearchQuery(query: String) {
        searchQuery.asMutable.value = query
    }

    override fun setSearchFilter(filter: MessageReadStatus) {
        searchFilter.asMutable.value = filter
        filterChangedObservable.asSubject.onNext(filter)
    }

    override fun dispatchFilterClick() {
        filterClickObservable.asSubject.onNext(searchInputFilter.value!!)
    }

    override fun dispatchCancelSearch() {
        cancelSearchObservable.asSubject.onNext(Unit)
    }

    override fun dispatchHideKeyboard() {
        hideKeyboardObservable.asSubject.onNext(Unit)
    }

    override fun dispatchOnItemClick(personUuid: UUID) {
        itemClickObservable.asSubject.onNext(personUuid)
    }

    override fun dispatchFocusChanged(isFocused: Boolean) {
        focusChangedObservable.asSubject.onNext(isFocused)
    }

    override fun dispatchNetworkError() {
        networkErrorObservable.asSubject.onNext(Unit)
    }

    override fun setIsGroupConversation(isGroup: Boolean) {
        isGroupConversation.asSubject.onNext(isGroup)
    }

    override fun setMessageReceiversCount(count: Int) {
        receiversCount.asSubject.onNext(count)
    }

    override fun setSearchInputVisibility(isVisible: Boolean) {
        searchInputVisibility.asMutable.value = if (isVisible) View.VISIBLE else View.GONE
    }
}