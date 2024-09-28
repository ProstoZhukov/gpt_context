package ru.tensor.sbis.crud3.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.crud3.CollectionViewModel
import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.domain.CollectionPaginator
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.domain.PageDirection
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Фабрика для промежуточной модели представления для компонента crud3, уметт приобразовывать модели от контроллера в
 * любые типы элементов. То же, что и [CollectionViewModelFactory] только с [mapper], который выполняет преобразование
 * моделей элементов списка слоя данных в модели слоя представления.
 */
@AnyThread
internal class ComponentViewModelFactory<FILTER, SOURCE_ITEM, OUTPUT_ITEM : Any>(
    private val mapper: Lazy<ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>>,
    private val creatorCollectionViewModel: () -> CollectionViewModel<FILTER, SOURCE_ITEM>
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
 * Фабрика модель представления для компонента crud3.
 * То же, что и [ComponentViewModelFactory] только учитывает секции и навыходи всегда получает [AnyItem] необходимый
 * тип элементов для работы [ListComponentView].
 */
@AnyThread
internal class ListComponentViewViewModelFactory<FILTER, SOURCE_ITEM>(
    private val creatorComponentViewModel: () -> ComponentViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListComponentViewViewModelImpl(
            innerVm = creatorComponentViewModel()
        ) as T
    }
}

/**
 * Фабрика модель представления для коллекции crud3.
 * Аргументы дженерик см. в [ComponentViewModel].
 * Через [wrapper] осуществляется работа с классами микросервиса.
 * [stubFactory] вызывается для создания заглушек.
 * Параметр [pageSize] задает размер страницы данных, которая должна включать как вмещающиеся на экран количество
 * элементов [viewPostSize], так и запас вне экрана для своевременного запроса следующих страниц для пагинации.
 */
@AnyThread
internal class CollectionViewModelFactory<COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>(
    private val wrapper: Lazy<Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>>,
    private val stubFactory: Lazy<StubFactory>,
    private var pageSize: Int,
    private var viewPostSize: Int,
    private val initialPageDirection: PageDirection = PageDirection.FORWARD
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollectionViewModelImpl(
            wrapper.value,
            CollectionPaginator(
                wrapper.value,
                pageSize,
                viewPostSize,
                initialPageDirection
            ),
            stubFactory.value
        ) as T
    }
}