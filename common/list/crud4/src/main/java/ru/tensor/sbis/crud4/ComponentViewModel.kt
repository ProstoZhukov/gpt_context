package ru.tensor.sbis.crud4

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.Observable
import ru.tensor.sbis.crud4.data.HierarchyObserverWrapper
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.domain.ItemWithIndex
import ru.tensor.sbis.crud4.domain.ObserverCallback
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.viewmodel.ComponentViewModelFactory
import ru.tensor.sbis.crud4.view.viewmodel.ComponentViewModelImpl
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.HierarchyCollectionProtocol
import ru.tensor.sbis.service.PathProtocol

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
 * [SOURCE_ITEM] класс элемента списка, который отдает контроллер.
 * [OUTPUT_ITEM] класс элемента списка, который используется на UI слое.
 *
 * Модель представления будет жить в пределах скоупа [viewModelStoreOwner].
 * С помощью [mapper] выполняется преобразование моделей элементов списка слоя данных в модели слоя представления.
 * [stubFactory] вызывается для создания заглушек.
 * Параметр [pageSize] задает размер страницы данных, которая должна включать как вмещающиеся на экран количество
 * элементов [viewPostSize], так и запас вне экрана для своевременного запроса следующих страниц для пагинации.
 * По умолчанию установлено значение [pageSize] равно [defaultPageSize] элементов, а [viewPostSize] равно [defaultViewPostSize], для большинства случаев
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
    SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>,
    OUTPUT_ITEM : Any,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
    > createComponentViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    itemWithIndexExtractor: Lazy<ItemWithIndex<ITEM_WITH_INDEX, SOURCE_ITEM>>,
    observerWrapper: HierarchyObserverWrapper<COLLECTION_OBSERVER, ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL>>,
    mapper: Lazy<ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = ""
): ComponentViewModel<COLLECTION, OUTPUT_ITEM, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> {
    return createComponentViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        mapper = mapper,
        creatorCollectionViewModel = {
            createCollectionViewModel(
                viewModelStoreOwner = viewModelStoreOwner,
                itemWithIndexExtractor = itemWithIndexExtractor,
                observerWrapper = observerWrapper,
                stubFactory = stubFactory,
                pageSize = pageSize,
                viewPostSize = viewPostSize,
                viewModelKey = viewModelKey
            )
        },
        viewModelKey = viewModelKey
    )
}

/**
 * Как [createComponentViewModel] с большим числом параметров, но принимает лямбду [creatorCollectionViewModel]
 */
@MainThread
fun <COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    FILTER,
    SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>,
    OUTPUT_ITEM : Any,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
    > createComponentViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    mapper: Lazy<ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>>,
    creatorCollectionViewModel: () -> CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    viewModelKey: String = ""
): ComponentViewModel<COLLECTION, OUTPUT_ITEM, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        owner = viewModelStoreOwner,
        factory = ComponentViewModelFactory(
            mapper = mapper,
            creatorCollectionViewModel = creatorCollectionViewModel
        )
    )[VIEW_MODEL_KEY_PREFIX + viewModelKey, ComponentViewModelImpl::class.java]
        as ComponentViewModel<COLLECTION, OUTPUT_ITEM, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>
}

/**
 * Интерфейс вью модели компонента списка. В дополнение к [CollectionViewModel] дает возможность работать с маппером.
 */
interface ComponentViewModel<COLLECTION, OUTPUT_ITEM : Any, FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, PATH_MODEL, IDENTIFIER> :
    CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER> {
    /**
     * Подписка на события изменения в компоненте списка, с уже преобразованными моделями.
     */
    val dataChangeMapped: Observable<DataChange<OUTPUT_ITEM>>

    /**
     * Оповещение об открытии переходи в папку через хлебную крошку.
     */
    val onMove: LiveData<PATH_MODEL?>

    /**
     * Оповещение об открытии папки.
     */
    val onOpenFolder: LiveData<Pair<IDENTIFIER, IDENTIFIER?>>

    /**
     * Оповещение о переходе в родительскую папку.
     */
    val onGoBackFolder: LiveData<Unit>

    /**
     * Открыть папку.
     */
    fun openFolder(pathModel: PATH_MODEL)

    /**
     * Открыть папку через хлебную крошку.
     */
    fun openFolder(view: IDENTIFIER, folder: IDENTIFIER)

    /**
     * Вернуться в предыдущую папку.
     */
    fun goBackFolder()

}

/**
 * Размер видимой части страницы по умолчанию.
 */
const val defaultViewPostSize = 30

/**
 * Размер страницы для запроса данных по умолчанию.
 */
const val defaultPageSize: Int = defaultViewPostSize * 3

private const val VIEW_MODEL_KEY_PREFIX = "ComponentViewModelImpl"
