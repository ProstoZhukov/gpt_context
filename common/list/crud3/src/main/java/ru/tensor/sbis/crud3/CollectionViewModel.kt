package ru.tensor.sbis.crud3

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.Observable
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.crud3.domain.PageDirection
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.viewmodel.CollectionViewModelFactory
import ru.tensor.sbis.crud3.view.viewmodel.CollectionViewModelImpl
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.list.view.DataChangedObserver
import ru.tensor.sbis.list.view.calback.ListViewListener

/**
 * Вью модель экрана списка, определяет видимость как самого списка, так и индикаторов прогресса, заглушки, а так же
 * реагирует на пулл-ту-рефреш.
 * Интерфейс выделен только лишь для хранения ссылки на модель представления без указания дженерик-параметров.
 */
interface CollectionViewModel<FILTER, SOURCE_ITEM> : ListViewListener, DataChangedObserver,
    ResetableRefreshable<FILTER> {
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
     * Событие изменения данных коллекции для отображения. Приходит на UI патоке.
     *
     * LiveData здесь не подходит, так как нужно поймать все события и обработать в хронологической
     * последовательности, потому что используется нотификация адаптера. А LiveData может "проглотить" несколько при
     * вызове метода post и отдать только последний.
     */
    val dataChange: Observable<DataChange<SOURCE_ITEM>>

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
     * Требуется ли подскралливать к первому элементу, когда с контроллера пришло событие reset со списком элементов.
     */
    var needScrollToFirstOnReset: Boolean
}


/**
 * Фабрика модели представления списка.
 * [COLLECTION] класс коллекция микросервиса контроллера может выглядеть так ru.tensor.sbis.***.generated.***ListViewModel
 * [COLLECTION_OBSERVER] класс реализация интерфейса наблюдателя для коллекции микросервиса контроллера [COLLECTION],
 * см. [ObserverCallback].
 * [FILTER] класс фильтра используемый для получения коллекции [COLLECTION] из микросервиса контроллера методом 'get':
 * выглядит так ***CollectionProvider.get([FILTER], ***)
 * [PAGINATION_ANCHOR] класс фильтра используемый для получения коллекции [COLLECTION] из микросервиса контроллера методом 'get':
 * выглядит так ***CollectionProvider.get(***, [PAGINATION_ANCHOR]).
 * [ITEM_WITH_INDEX] класс пары, содержащей элемент и индекс, который используется в методах
 * onAdd(p0: ArrayList<[ITEM_WITH_INDEX]>) и
 * onReplace(p0: ArrayList<[ITEM_WITH_INDEX]>) в классе [COLLECTION].
 * [ITEM] класс элемента списка, который отдает контроллер.
 *
 * Модель представления будет жить в пределах скоупа [viewModelStoreOwner].
 * Через [wrapper] осуществляется работа с классами микросервиса.
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
fun <COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM : Any> createCollectionViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    wrapper: Lazy<Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = "",
    initialPageDirection: PageDirection = PageDirection.FORWARD
): CollectionViewModel<FILTER, ITEM> {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        viewModelStoreOwner,
        CollectionViewModelFactory(
            wrapper,
            stubFactory,
            pageSize,
            viewPostSize,
            initialPageDirection
        )
    ).get(
        VIEW_MODEL_KEY_PREFIX + viewModelKey,
        CollectionViewModelImpl::class.java
    ) as CollectionViewModelImpl<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM>
}

private const val VIEW_MODEL_KEY_PREFIX = "CollectionViewModelImpl"