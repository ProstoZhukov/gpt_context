package ru.tensor.sbis.list.base.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.presentation.BackgroundListDataExtractor.ListDataReceiver
import ru.tensor.sbis.list.view.container.ListContainerViewModel
import ru.tensor.sbis.list.view.container.ListContainerViewModelImpl
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain

/**
 * Вью модель экрана, который может отображать список, заглушку, индикатор прогресса в центре экрана, индикатор
 * swipe-to-refresh и индикаторы постраничной подгрузки снизу и сверху списка.
 *
 * @param ENTITY : ListScreenEntity отображаемая бизнес модель экрана списка.
 * @property listContainerViewModel ListContainerViewModel контейнер, инкапсулирующий логику скрытия и показа списка,
 * заглушки и индикатора прогресса в центре экрана.
 * @property loadNextVisibility MutableLiveData<Boolean> отображения индикатора постраничной подгрузки снизу.
 * @property loadPreviousVisibility MutableLiveData<Boolean> отображения индикатора постраничной подгрузки сверху.
 * @property listData ListLiveData данные списка.
 * @property stubContent MutableLiveData<StubContent> данные заглушки.
 * @property loadNextAvailability MutableLiveData<Boolean> можно ли отображать индикатора постраничной подгрузки снизу.
 * см. [loadNext].
 * @property loadPreviousVisibility MutableLiveData<Boolean> можно ли отображать индикатора постраничной подгрузки сверху.
 * см. [loadPrevious].
 * @property swipeRefreshIsVisible MutableLiveData<Boolean> отображения wipe-to-refresh.
 * @property swipeRefreshIsEnabled MutableLiveData<Boolean> доступность wipe-to-refresh.
 * @property fabPadding MutableLiveData<Boolean> добавление отступа для последнего элемента списка перед
 * нижним краем списка.
 */
class PlainListVM<ENTITY : ListScreenEntity>(
    private val progressIsVisible: PublishSubject<Boolean> = PublishSubject.create(),
    private val listContainerViewModel: ListContainerViewModel = ListContainerViewModelImpl(
        progressIsVisible = progressIsVisible
    ),
    private val extractor: BackgroundListDataExtractor = BackgroundListDataExtractor()
) : ListScreenVM, ListContainerViewModel by listContainerViewModel,
    View<ENTITY>, ListDataReceiver {

    private val scrollLiveData = ScrollLiveData()

    /**
     * Состояние vm при полном обновлении данных
     *
     * @see cleanState
     */
    private var reloadState: ReloadState = ReloadState.COMPLETED
    private val _loadNextVisibility = BooleanLiveData(false)
    private val _loadPreviousVisibility = BooleanLiveData(false)
    private val _loadNextAvailability = BooleanLiveData(false)
    private val _loadPreviousAvailability = BooleanLiveData(false)

    override val loadNextVisibility = _loadNextVisibility.distinctUntilChanged()
    override val loadPreviousVisibility = _loadPreviousVisibility.distinctUntilChanged()
    override val loadNextAvailability = _loadNextAvailability.distinctUntilChanged()
    override val loadPreviousAvailability = _loadPreviousAvailability.distinctUntilChanged()
    override val listData = ListLiveData()
    override val swipeRefreshIsVisible = BooleanLiveData(false)
    override val swipeRefreshIsEnabled = BooleanLiveData(true)
    override val fabPadding = BooleanLiveData(false)
    override val needInitialScroll = BooleanLiveData(true)
    override val scrollToPosition: LiveData<Int> = scrollLiveData

    override fun loadPrevious() = Unit

    override fun loadNext() = Unit

    override fun showRefresh() = Unit

    override fun onAdd(items: List<AnyItem>) {
        needInitialScroll.postValue(false)
    }

    override fun cleanState() {
        // модель переведена в чистое состояние. После загрузки данных нужно будет прокрутить список в начало
        reloadState = ReloadState.WAITING_FOR_RELOAD
        needInitialScroll.postValue(true)
        listData.postValue(Plain())
    }

    override fun showData(entity: ENTITY) {
        _loadNextAvailability.postValue(entity.hasNext())
        _loadPreviousAvailability.postValue(entity.hasPrevious())
        swipeRefreshIsEnabled.postValue(!entity.hasPrevious())
        extractor.extract(entity, this)
        // дождались, БМ полностью обновлена
        if (reloadState == ReloadState.WAITING_FOR_RELOAD && entity.isUpToDate()) {
            reloadState = ReloadState.RELOADED
        }
    }

    override fun showStub(stubEntity: StubEntity, immediate: Boolean) {
        listContainerViewModel.showOnlyStub(immediate)
        _loadNextVisibility.postValue(false)
        _loadPreviousVisibility.postValue(false)
        setStubContentFactory(stubEntity.provideStubViewContentFactory())
        swipeRefreshIsVisible.postValue(false)
        swipeRefreshIsEnabled.postValue(true)
    }

    override fun showLoading() {
        listContainerViewModel.showOnlyProgress()
    }

    override fun showLoadNext() {
        _loadNextVisibility.postValue(true)
    }

    override fun showPrevious() {
        _loadPreviousVisibility.postValue(true)
    }

    @MainThread
    override fun receive(data: ListData) {
        listData.value = data
        listContainerViewModel.showOnlyList()
        if (reloadState == ReloadState.RELOADED) {
            reloadState = ReloadState.COMPLETED
            // закончился полный цикл загрузки - можно прокручивать список (сейчас только в начало)
            scrollLiveData.value = 0
        }
        swipeRefreshIsVisible.postValue(false)
        _loadNextVisibility.postValue(false)
        _loadPreviousVisibility.postValue(false)
    }

    fun setHasFabPadding(value: Boolean) {
        fabPadding.postValue(value)
    }
}

private enum class ReloadState {

    /**
     * Ожидается полное обновление данных БМ
     */
    WAITING_FOR_RELOAD,

    /**
     * Модель данных обновлена
     */
    RELOADED,

    /**
     * Завершено обновление модели представления в соответствии с БМ
     */
    COMPLETED
}