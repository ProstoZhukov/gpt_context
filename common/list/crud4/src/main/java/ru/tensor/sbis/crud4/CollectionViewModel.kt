package ru.tensor.sbis.crud4

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.Observable
import ru.tensor.sbis.crud4.data.HierarchyObserverWrapper
import ru.tensor.sbis.crud4.domain.ItemWithIndex
import ru.tensor.sbis.crud4.domain.ObserverCallback

import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.viewmodel.CollectionViewModelFactory
import ru.tensor.sbis.crud4.view.viewmodel.CollectionViewModelImpl
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.list.view.DataChangedObserver
import ru.tensor.sbis.list.view.calback.ListViewListener
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.HierarchyCollectionObserverProtocol
import ru.tensor.sbis.service.HierarchyCollectionProtocol
import ru.tensor.sbis.service.PathProtocol
import ru.tensor.sbis.service.SelectionDataProtocol
import ru.tensor.sbis.service.generated.DirectionStatus

/**
 * Вью модель экрана списка, определяет видимость как самого списка, так и индикаторов прогресса, заглушки, а так же
 * реагирует на пулл-ту-рефреш.
 * Интерфейс выделен только лишь для хранения ссылки на модель представления без указания дженерик-параметров.
 */
interface CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> : ListViewListener,
    DataChangedObserver,
    ResetableRefreshable<FILTER> where SOURCE_ITEM : DecoratedProtocol<IDENTIFIER> {
    /**
     * Фабрика контента для заглушки.
     */
    val stubFactory: LiveData<StubViewContent>

    /**
     * Является ли текущая загруженная страница первой
     */
    val isZeroPage: Boolean

    /**
     * Видимость индикатор загрузки, расположенного в центре.
     */
    val centralThrobberVisibility: LiveData<Boolean>

    /**
     * Видимость индикатор загрузки, расположенного в тулбаре.
     */
    val toolbarThrobberVisibility: LiveData<Boolean>

    /**
     * Видимость заглушки.
     */
    val stubVisibility: LiveData<Boolean>

    /**
     * Событие с текущим путём до папки.
     */
    val onPath: LiveData<List<PATH_MODEL>>

    /**
     * Событие с количеством выделенных элементов.
     */
    val selectedSizeChange: LiveData<Long?>

    /**
     * Событие окончания изменения коллекции.
     */
    val onEndUpdate: LiveData<DirectionStatus>

    /**
     * Видимость индикатора проблемы сети в тулбаре.
     */
    val toolbarNoNetworkVisibility: LiveData<Boolean>

    /**
     * Доступность подгрузки следующей страницы.
     */
    var loadNextAvailable: LiveData<Boolean>

    /**
     * Доступность подгрузки предыдущей страницы.
     */
    var loadPreviousAvailable: LiveData<Boolean>

    /**
     * Видимость индикатору загрузки следующей страницы.
     */
    var loadNextThrobberIsVisible: LiveData<Boolean>

    /**
     * Видимость индикатору загрузки предыдущей страницы.
     */
    var loadPreviousThrobberIsVisible: LiveData<Boolean>

    /**
     * Доступность pull-to-refresh обновления.
     */
    var refreshIsAvailable: LiveData<Boolean>

    var isRefreshing: LiveData<Boolean>

    /**
     * Событиен об изменении фильтра
     */
    val onChangeFilter: LiveData<FILTER?>

    /**
     * Событие изменения данных коллекции для отображения. Приходит на UI патоке.
     *
     * LiveData здесь не подходит, так как нужно поймать все события и обработать в хронологической
     * последовательности, потому что используется нотификация адаптера. А LiveData может "проглотить" несколько при
     * вызове метода post и отдать только последний.
     */
    val dataChange: Observable<DataChange<SOURCE_ITEM>>

    /**
     * Изменить корневую папку
     */
    fun changeRoot(pathModel: PATH_MODEL?)

    /**
     * Развернуть папку
     */
    fun expand(item: SOURCE_ITEM)

    /**
     * Развернуть папку по индексу элемента.
     */
    fun expand(index: Long)

    /**
     * Свернуть папку
     */
    fun collapse(item: SOURCE_ITEM)

    /**
     * Отметить элемент
     */
    fun mark(item: SOURCE_ITEM)

    /**
     * Выделить элемент
     */
    fun select(item: SOURCE_ITEM)

    /**
     * Получить выделенные пользователем элементы.
     */
    fun getSelected(): SelectionDataProtocol<IDENTIFIER>

    /**
     * Сбросить выделенные пользователем элементы.
     */
    fun resetSelection()

    /**
     * Установить новую коллекцию
     */
    fun setCollection(collection: COLLECTION)

    /**
     * Запросить подгрузки следующей страницы.
     * Запрос будет выполнен только при фактическом наличии следующей страницы. Если запрос уже выполняется, то метод
     * будет проигнорирован, то есть, его можно(но не нужно) вызывать безопасно произвольное количество раз, пока не
     * получены данные.
     */
    override fun loadNext()

    /**
     * Запросить подгрузку предыдущей страницы.
     * Запрос будет выполнен только при фактическом наличии следующей страницы. Если запрос уже выполняется, то метод
     * будет проигнорирован, то есть, его можно(но не нужно) вызывать безопасно произвольное количество раз, пока не
     * получены данные.
     */
    override fun loadPrevious()

    /**
     * Событие необходимости подскролла к указанному элементу.
     */
    val scrollScrollToZeroPosition: LiveData<Int>

    /**
     * Событие необходимости мгновенного подскролла к указанному элементу.
     */
    val scrollScrollToPosition: LiveData<Int>

}


/**
 * Фабрика модели представления списка.
 * [COLLECTION] класс коллекция микросервиса контроллера может выглядеть так ru.tensor.sbis.***.generated.***ListViewModel
 * [COLLECTION_OBSERVER] класс реализация интерфейса наблюдателя для коллекции микросервиса контроллера [COLLECTION],
 * см. [ObserverCallback].
 * [FILTER] класс фильтра используемый для получения коллекции [COLLECTION] из микросервиса контроллера методом 'get':
 * выглядит так ***CollectionProvider.get([FILTER], ***)
 * [ITEM_WITH_INDEX] класс пары, содержащей элемент и индекс, который используется в методах
 * onAdd(p0: ArrayList<[ITEM_WITH_INDEX]>) и
 * onReplace(p0: ArrayList<[ITEM_WITH_INDEX]>) в классе [COLLECTION].
 * [ITEM] класс элемента списка, который отдает контроллер.
 *
 * Модель представления будет жить в пределах скоупа [viewModelStoreOwner].
 * [stubFactory] вызывается для создания заглушек.
 * Параметр [pageSize] задает размер страницы данных, которая должна включать как вмещающиеся на экран количество
 * элементов [viewPostSize], так и запас вне экрана для своевременного запроса следующих страниц для пагинации.
 * По умолчанию установлено значение [pageSize] равно [defaultPageSize] элементов а [viewPostSize] равно [defaultViewPostSize], для большинства случаев
 * этого должно хватать.
 *
 * Если необходимо иметь несколько моделей представления в рамках одного скоупа(фрагмента или активити), то необходимо
 * задать уникальный в пределах скоупа ключ [viewModelKey].
 */
@MainThread
fun <COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    FILTER,
    ITEM_WITH_INDEX,
    ITEM : DecoratedProtocol<IDENTIFIER>,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
> createCollectionViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    itemWithIndexExtractor: Lazy<ItemWithIndex<ITEM_WITH_INDEX, ITEM>>,
    observerWrapper: HierarchyObserverWrapper<COLLECTION_OBSERVER, ObserverCallback<ITEM_WITH_INDEX, ITEM, PATH_MODEL>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = ""
): CollectionViewModel<COLLECTION, FILTER, ITEM, PATH_MODEL, IDENTIFIER> {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        viewModelStoreOwner,
        CollectionViewModelFactory<COLLECTION, COLLECTION_OBSERVER, FILTER, ITEM_WITH_INDEX, ITEM, PATH_MODEL, IDENTIFIER>(
            itemWithIndexExtractor,
            observerWrapper,
            stubFactory,
            pageSize,
            viewPostSize
        )
    )[VIEW_MODEL_KEY_PREFIX + viewModelKey, CollectionViewModelImpl::class.java] as CollectionViewModelImpl<COLLECTION, COLLECTION_OBSERVER, FILTER, ITEM_WITH_INDEX, ITEM, PATH_MODEL, IDENTIFIER>
}

private const val VIEW_MODEL_KEY_PREFIX = "CollectionViewModelImpl"