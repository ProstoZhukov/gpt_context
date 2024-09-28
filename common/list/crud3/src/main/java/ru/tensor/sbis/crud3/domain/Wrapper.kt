package ru.tensor.sbis.crud3.domain

import androidx.annotation.AnyThread
import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.data.Crud3CollectionWrapper

/**
 * Объединение интерфейсов.
 * Описание аргументов см в [ComponentViewModel].
 */
@AnyThread
interface Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM>
    : Crud3CollectionWrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM>,
    ItemWithIndex<ITEM_WITH_INDEX, ITEM>