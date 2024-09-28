package ru.tensor.sbis.crud3

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.crud3.domain.PageDirection
import ru.tensor.sbis.crud3.domain.ResetWithSection
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.viewmodel.ListComponentViewViewModelFactory
import ru.tensor.sbis.crud3.view.viewmodel.ListComponentViewViewModelImpl
import ru.tensor.sbis.crud3.view.viewmodel.ScrollEvent
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.SectionOptions

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
 * [SOURCE_ITEM] класс элемента списка, который отдает контроллер.
 * [OUTPUT_ITEM] класс элемента списка, который используется на UI слое.
 *
 * Модель представления будет жить в пределах скоупа [viewModelStoreOwner].
 * Через [wrapper] осуществляется работа с классами микросервиса.
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
fun <COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM : Any, OUTPUT_ITEM : AnyItem> createListComponentViewViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    wrapper: Lazy<Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>>,
    mapper: Lazy<ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = "",
    initialPageDirection: PageDirection = PageDirection.FORWARD
): ListComponentViewViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM> {
    return createListComponentViewViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        creatorComponentViewModel = {
            createComponentViewModel(
                viewModelStoreOwner = viewModelStoreOwner, wrapper = wrapper,
                mapper = lazy { SectionMapperWrapper(mapper.value) },
                stubFactory = stubFactory,
                pageSize = pageSize,
                viewPostSize = viewPostSize,
                viewModelKey = viewModelKey,
                initialPageDirection = initialPageDirection
            )
        },
        viewModelKey = viewModelKey
    )
}

/**
 * Как [createListComponentViewViewModel] с большим числом параметров, но принимает лямбду [creatorComponentViewModel]
 */
@MainThread
fun <FILTER, SOURCE_ITEM : Any> createListComponentViewViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    creatorComponentViewModel: () -> ComponentViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM>,
    viewModelKey: String = "",
): ListComponentViewViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM> {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        viewModelStoreOwner,
        ListComponentViewViewModelFactory(creatorComponentViewModel)
    )[VIEW_MODEL_KEY_PREFIX + viewModelKey, ListComponentViewViewModelImpl::class.java] as ListComponentViewViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM>
}

/**
 * Интерфейс втю модели компонента списка. В дополнение к [ComponentViewModel] дает возможность работать с маппером учитывая натсройки секции.
 */
interface ListComponentViewViewModel<OUTPUT_ITEM : ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM> :
    ComponentViewModel<OUTPUT_ITEM, FILTER, SOURCE_ITEM>,
    ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM> {

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
     * Метод вызывающийся при прокрутке, необходим для передачи  событий от внутреннего списка к вью модели.
     */
    fun onScroll(recyclerView: RecyclerView, dx: Int, dy: Int)

    /**
     * Установить новое значение фильтра и/или маппера и отобразить список с начальной позиции.
     * Разница с методом [ListComponent.reset] в том что тут используется маппер учитывающий настройки секций.
     */
    fun reset(reset: ResetWithSection<FILTER, SOURCE_ITEM, AnyItem>)
}

/**
 * Обертка маппера добавляющая в него возможность задать настройки секции для каждого элемента.
 */
class SectionMapperWrapper<SOURCE_ITEM, OUTPUT_ITEM : AnyItem>(
    private val sectionMapper: ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM>
) : ItemMapper<SOURCE_ITEM, ItemWithSection<AnyItem>> {
    override fun map(item: SOURCE_ITEM, defaultClickAction: (SOURCE_ITEM) -> Unit): ItemWithSection<AnyItem> {
        return ItemWithSection(sectionMapper.mapSection(item), sectionMapper.map(item, defaultClickAction))
    }
}

/**
 * Модель для добавления настроек секции к элементу списка.
 */
data class ItemWithSection<ITEM : AnyItem>(val sectionOption: SectionOptions, val item: ITEM)

private const val VIEW_MODEL_KEY_PREFIX = "ComponentViewViewModelImpl"