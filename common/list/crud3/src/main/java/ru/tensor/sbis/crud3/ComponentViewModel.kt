package ru.tensor.sbis.crud3

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.Observable
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.crud3.domain.PageDirection
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.viewmodel.ComponentViewModelFactory
import ru.tensor.sbis.crud3.view.viewmodel.ComponentViewModelImpl

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
fun <COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM : Any, OUTPUT_ITEM : Any> createComponentViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    wrapper: Lazy<Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>>,
    mapper: Lazy<ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>>,
    stubFactory: Lazy<StubFactory>,
    pageSize: Int = defaultViewPostSize * 3,
    viewPostSize: Int = defaultViewPostSize,
    viewModelKey: String = "",
    initialPageDirection: PageDirection = PageDirection.FORWARD
): ComponentViewModel<OUTPUT_ITEM, FILTER, SOURCE_ITEM> {
    return createComponentViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        mapper = mapper,
        creatorCollectionViewModel = {
            createCollectionViewModel(
                viewModelStoreOwner = viewModelStoreOwner,
                wrapper = wrapper,
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
 * Как [createComponentViewModel] с большим числом параметров, но принимает лямбду [creatorCollectionViewModel]
 */
@MainThread
fun <FILTER, SOURCE_ITEM : Any, OUTPUT_ITEM : Any> createComponentViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    mapper: Lazy<ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>>,
    creatorCollectionViewModel: () -> CollectionViewModel<FILTER, SOURCE_ITEM>,
    viewModelKey: String = ""
): ComponentViewModel<OUTPUT_ITEM, FILTER, SOURCE_ITEM> {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        owner = viewModelStoreOwner,
        factory = ComponentViewModelFactory(
            mapper = mapper,
            creatorCollectionViewModel = creatorCollectionViewModel
        )
    )[VIEW_MODEL_KEY_PREFIX + viewModelKey, ComponentViewModelImpl::class.java] as ComponentViewModel<OUTPUT_ITEM, FILTER, SOURCE_ITEM>
}

/**
 * Интерфейс втю модели компонента списка. В дополнение к [CollectionViewModel] дает возможность работать с маппером.
 */
interface ComponentViewModel<OUTPUT_ITEM : Any, FILTER, SOURCE_ITEM> :
    CollectionViewModel<FILTER, SOURCE_ITEM>,
    ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM> {
    /**
     * Подписка на события изменения в компоенте списка, с уже приобразованными моделями
     */
    val dataChangeMapped: Observable<DataChange<OUTPUT_ITEM>>
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