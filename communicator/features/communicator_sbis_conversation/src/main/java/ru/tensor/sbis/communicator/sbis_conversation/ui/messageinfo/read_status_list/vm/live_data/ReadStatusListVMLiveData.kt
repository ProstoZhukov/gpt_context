package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data

import androidx.lifecycle.LiveData
import io.reactivex.Observable
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import java.util.*

/**
 * Интерфейс параметров состояния view списка статусов прочитанности сообщения
 * @see [ReadStatusListItemsLiveData]
 * @see [ReadStatusListSearchInputLiveData]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListViewLiveData :
    ReadStatusListItemsLiveData,
    ReadStatusListSearchInputLiveData

/**
 * Интерфейс параметров состояния вью-модели списка статусов прочитанности сообщения
 * @see [ReadStatusListViewLiveData]
 * @see [ReadStatusListSearchInputLiveData]
 * @see [ReadStatusListFilterLiveData]
 * @see [ReadStatusListSearchInputLiveData]
 * @see [ReadStatusErrorLiveData]
 */
internal interface ReadStatusListVMLiveData :
    ReadStatusListViewLiveData,
    ReadStatusListItemsLiveData,
    ReadStatusMessageInfoLiveData,
    ReadStatusListFilterLiveData,
    ReadStatusListSearchInputLiveData,
    ReadStatusErrorLiveData {

    /**
     * Установить значение поиска
     * @param query значение поиска
     */
    fun setSearchQuery(query: String)

    /**
     * Установить значение фильтра
     * @param filter значение фильтра
     */
    fun setSearchFilter(filter: MessageReadStatus)

    /**
     * Отправить событие клика по фильтру
     */
    fun dispatchFilterClick()

    /**
     * Отправить событие закрытия поиска
     */
    fun dispatchCancelSearch()

    /**
     * Отправить событие закрытия клавиатуры
     */
    fun dispatchHideKeyboard()

    /**
     * Отправить событие клика на элемент списка
     * @param personUuid идентификатор персоны
     */
    fun dispatchOnItemClick(personUuid: UUID)

    /**
     * Отправить событие смены фокуса в поисковой строке
     * @param isFocused true, если строка поиска в фокусе
     */
    fun dispatchFocusChanged(isFocused: Boolean)

    /**
     * Отправить событие об ошибки сети
     */
    fun dispatchNetworkError()

    /**
     * Установить признак групповой переписки
     * @param isGroup true, если переписка групповая
     */
    fun setIsGroupConversation(isGroup: Boolean)

    /**
     * Установить количество получателей сообщения
     * @param count количество
     */
    fun setMessageReceiversCount(count: Int)

    /**
     * Установить видимость поисковой строки
     * @param isVisible true, если поисковая строка должна отображаться
     */
    fun setSearchInputVisibility(isVisible: Boolean)
}

/**
 * Интерфейс параметров состояния элементов
 */
internal interface ReadStatusListItemsLiveData {

    /**
     * [Observable] для подписки на события кликов по элементам списка
     */
    val itemClickObservable: Observable<UUID>
}

/**
 * Интерфейс параметров состояния фильтра для запросов списка
 */
internal interface ReadStatusListFilterLiveData {

    /**
     * Поисковая строка
     */
    val searchQuery: LiveData<String>

    /**
     * Поисковый фильтр
     */
    val searchFilter: LiveData<MessageReadStatus>

    /**
     * [Observable] для подписки на изменения фильтра
     */
    val filterChangedObservable: Observable<MessageReadStatus>
}

/**
 * Интерфейс параметров состояния вью поисковой строки
 */
internal interface ReadStatusListSearchInputLiveData {

    /**
     * [Observable] для обработки кликов на фильтр
     */
    val filterClickObservable: Observable<MessageReadStatus>

    /**
     * [Observable] для обработки закрытия поиска
     */
    val cancelSearchObservable: Observable<Unit>

    /**
     * [Observable] для обработки закрытия клавиатуры
     */
    val hideKeyboardObservable: Observable<Unit>

    /**
     * Текущий фильтр для установки view фильтра в поиковой строке
     */
    val searchInputFilter: LiveData<MessageReadStatus>

    /**
     * Видимость view поисковой строки и фильтра
     */
    val searchInputVisibility: LiveData<Int>

    /**
     * [Observable] для подписки на фокус в строке поиска
     */
    val focusChangedObservable: Observable<Boolean>
}

/**
 * Интерфейс параметров состояния информации о сообщении
 */
internal interface ReadStatusMessageInfoLiveData {

    /**
     * Признак принадлежности групповой переписке
     */
    val isGroupConversation: Observable<Boolean>

    /**
     * Количество получателей сообщения
     */
    val receiversCount: Observable<Int>
}

/**
 * Интерфейс параметров возникающих ошибок
 */
internal interface ReadStatusErrorLiveData {

    /**
     * [Observable] для подписки на получение ошибки сети
     */
    val networkErrorObservable: Observable<Unit>
}