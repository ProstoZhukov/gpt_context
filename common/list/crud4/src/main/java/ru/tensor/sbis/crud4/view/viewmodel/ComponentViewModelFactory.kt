package ru.tensor.sbis.crud4.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.crud4.CollectionViewModel
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.ItemWithSection
import ru.tensor.sbis.crud4.data.HierarchyObserverWrapper
import ru.tensor.sbis.crud4.domain.CollectionPaginator
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.domain.ItemWithIndex
import ru.tensor.sbis.crud4.domain.ObserverCallback
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.HierarchyCollectionObserverProtocol
import ru.tensor.sbis.service.HierarchyCollectionProtocol
import ru.tensor.sbis.service.PathProtocol

/**
 * Фабрика для промежуточной модели представления для компонента crud4, умеет преобразовывать модели от контроллера в
 * любые типы элементов. То же, что и [CollectionViewModelFactory] только с [mapper], который выполняет преобразование
 * моделей элементов списка слоя данных в модели слоя представления.
 */
@AnyThread
internal class ComponentViewModelFactory<COLLECTION, FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM : Any, PATH_MODEL, IDENTIFIER>(
    private val mapper: Lazy<ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>>,
    private val creatorCollectionViewModel: () -> CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ComponentViewModelImpl(
            mapper.value,
            innerVm = creatorCollectionViewModel()
        ) as T
    }
}

/**
 * Фабрика модель представления для компонента crud4.
 * То же, что и [ComponentViewModelFactory] только учитывает секции и на выходе всегда получает [AnyItem] необходимый
 * тип элементов для работы [ListComponentView].
 */
@AnyThread
internal class ListComponentViewViewModelFactory<COLLECTION, FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, PATH_MODEL, IDENTIFIER>(
    private val creatorComponentViewModel: () -> ComponentViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListComponentViewViewModelImpl(
            innerVm = creatorComponentViewModel()
        ) as T
    }
}

/**
 * Фабрика модель представления для коллекции crud4.
 * Аргументы дженерик см. в [ComponentViewModel].
 * [stubFactory] вызывается для создания заглушек.
 * Параметр [pageSize] задает размер страницы данных, которая должна включать как вмещающиеся на экран количество
 * элементов [viewPostSize], так и запас вне экрана для своевременного запроса следующих страниц для пагинации.
 */
@AnyThread
internal class CollectionViewModelFactory<
    COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    FILTER,
    ITEM_WITH_INDEX,
    SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
>(
    private val itemWithIndexExtractor: Lazy<ItemWithIndex<ITEM_WITH_INDEX, SOURCE_ITEM>>,
    private val observerWrapper: HierarchyObserverWrapper<COLLECTION_OBSERVER, ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL>>,
    private val stubFactory: Lazy<StubFactory>,
    private var pageSize: Int,
    private var viewPostSize: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollectionViewModelImpl<COLLECTION, COLLECTION_OBSERVER, FILTER, ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>(
            itemWithIndexExtractor.value,
            CollectionPaginator(
                observerWrapper,
                viewPostSize
            ),
            stubFactory.value
        ) as T
    }
}