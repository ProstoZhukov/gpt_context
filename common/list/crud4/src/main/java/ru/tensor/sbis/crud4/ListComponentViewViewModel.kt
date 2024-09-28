package ru.tensor.sbis.crud4

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.crud4.data.HierarchyObserverWrapper
import ru.tensor.sbis.crud4.domain.ItemInSectionMapper
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.domain.ItemWithIndex
import ru.tensor.sbis.crud4.domain.ObserverCallback
import ru.tensor.sbis.crud4.domain.ResetWithSection
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.crud4.view.viewmodel.ListComponentViewViewModelFactory
import ru.tensor.sbis.crud4.view.viewmodel.ListComponentViewViewModelImpl
import ru.tensor.sbis.crud4.view.viewmodel.ScrollEvent
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.SectionOptions
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.HierarchyCollectionProtocol
import ru.tensor.sbis.service.PathProtocol

/**
 * Как [createListComponentViewViewModel] без [PAGINATION_ANCHOR], т.к. он не используется
 *
 * [PAGINATION_ANCHOR] класс фильтра используемый для получения коллекции [COLLECTION] из микросервиса контроллера методом 'get':
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Используйте createListComponentViewViewModel без PAGINATION_ANCHOR")
@MainThread
fun <COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    FILTER,
    PAGINATION_ANCHOR,
    ITEM_WITH_INDEX,
    SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>,
    OUTPUT_ITEM : AnyItem,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
    > createListComponentViewViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    itemWithIndexExtractor: Lazy<ItemWithIndex<ITEM_WITH_INDEX, SOURCE_ITEM>>,
    observerWrapper: HierarchyObserverWrapper<COLLECTION_OBSERVER, ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL>>,
    mapper: Lazy<ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = "",
    unit: Unit = Unit // Нужен лишь для разной сигнатуры методов createListComponentViewViewModel)
): ListComponentViewViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> {
    return createListComponentViewViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        itemWithIndexExtractor = itemWithIndexExtractor,
        observerWrapper = observerWrapper,
        mapper = mapper,
        stubFactory = stubFactory,
        pageSize = pageSize,
        viewPostSize = viewPostSize,
        viewModelKey = viewModelKey
    )
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
    OUTPUT_ITEM : AnyItem,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
    > createListComponentViewViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    itemWithIndexExtractor: Lazy<ItemWithIndex<ITEM_WITH_INDEX, SOURCE_ITEM>>,
    observerWrapper: HierarchyObserverWrapper<COLLECTION_OBSERVER, ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL>>,
    mapper: Lazy<ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = ""
): ListComponentViewViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> {
    return ru.tensor.sbis.crud4.createListComponentViewViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        creatorComponentViewModel = {
            createComponentViewModel(
                viewModelStoreOwner = viewModelStoreOwner,
                itemWithIndexExtractor = itemWithIndexExtractor,
                observerWrapper = observerWrapper,
                mapper = lazy { SectionMapperWrapper(mapper.value) },
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
 * Как [createListComponentViewViewModel] с большим числом параметров, но принимает лямбду [creatorComponentViewModel]
 */
@MainThread
fun <COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    FILTER,
    SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
    > createListComponentViewViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    creatorComponentViewModel: () -> ComponentViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    viewModelKey: String = ""
): ListComponentViewViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        viewModelStoreOwner,
        ListComponentViewViewModelFactory { creatorComponentViewModel() }
    )[VIEW_MODEL_KEY_PREFIX + viewModelKey, ListComponentViewViewModelImpl::class.java]
        as ListComponentViewViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>
}

/**
 * Интерфейс втю модели компонента списка. В дополнение к [ComponentViewModel] дает возможность работать с маппером учитывая натсройки секции.
 */
interface ListComponentViewViewModel<COLLECTION, OUTPUT_ITEM : ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> :
    ComponentViewModel<COLLECTION, OUTPUT_ITEM, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER> where  SOURCE_ITEM : DecoratedProtocol<IDENTIFIER> {

    /**
     * Подписка на событие скролла.
     */
    val onScrollEvent: LiveData<ScrollEvent>

    /**
     * Проскроллить к указанной позиции.
     */
    fun scrollToPosition(position: Int)

    /**
     * Событие вызывающееся при попытки скролла к позиции. Необходимо для подписки вью на события из вью модели.
     */
    val scrollToPositionEvent: LiveData<Int>

    /**
     * Событие при ручной отчистке списка.
     */
    val onCleanList: LiveData<Unit>

    /**
     * Метод вызывающийся при прокрутке, необходим для передачи  событий от внутреннего списка к вью модели.
     */
    fun onScroll(recyclerView: RecyclerView, dx: Int, dy: Int)

    /**
     * Установить новое значение фильтра и/или маппера и отобразить список с начальной позиции.
     * Разница с методом [ListComponent.reset] в том что тут используется маппер учитывающий настройки секций.
     */
    fun reset(reset: ResetWithSection<FILTER, SOURCE_ITEM, AnyItem, IDENTIFIER>)

    /**
     * Принудительно очистить список
     */
    fun cleanList()
}

/**
 * Обертка маппера добавляющая в него возможность задать настройки секции для каждого элемента.
 */
class SectionMapperWrapper<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM : AnyItem, IDENTIFIER>(
    private val sectionMapper: ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
) : ItemMapper<SOURCE_ITEM, ItemWithSection<AnyItem>, IDENTIFIER> {
    override fun map(
        item: SOURCE_ITEM,
        actionDelegate: ItemActionDelegate<SOURCE_ITEM, IDENTIFIER>
    ): ItemWithSection<AnyItem> {
        return ItemWithSection(sectionMapper.mapSection(item), sectionMapper.map(item, actionDelegate))
    }
}

/**
 * Модель для добавления настроек секции к элементу списка.
 */
data class ItemWithSection<ITEM : AnyItem>(val sectionOption: SectionOptions, val item: ITEM)

private const val VIEW_MODEL_KEY_PREFIX = "ComponentViewViewModelImpl"