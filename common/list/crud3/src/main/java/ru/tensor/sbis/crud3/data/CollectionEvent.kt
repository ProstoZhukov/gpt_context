package ru.tensor.sbis.crud3.data

import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Событие коллекции микросервиса. Соответствуют методам кобека коллекции микросервиса, ими же и производятся.
 * [SOURCE_ITEM] класс элемента списка, который отдает микросервис.
 * [ITEM_WITH_INDEX] класс пары, содержащей элемент и индекс, который используется в методах микросервиса.
 */
internal sealed interface CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>
internal class OnReset<SOURCE_ITEM, ITEM_WITH_INDEX>(val p0: List<SOURCE_ITEM>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnRemove<SOURCE_ITEM, ITEM_WITH_INDEX>(val p0: List<Long>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnMove<SOURCE_ITEM, ITEM_WITH_INDEX>(val p0: List<Pair<Long, Long>>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnAdd<SOURCE_ITEM, ITEM_WITH_INDEX>(val p0: List<ITEM_WITH_INDEX>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnReplace<SOURCE_ITEM, ITEM_WITH_INDEX>(val p0: List<ITEM_WITH_INDEX>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnAddThrobber<SOURCE_ITEM, ITEM_WITH_INDEX>(val position: ViewPosition) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnRemoveThrobber<SOURCE_ITEM, ITEM_WITH_INDEX> : CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>
internal class OnAddStub<SOURCE_ITEM, ITEM_WITH_INDEX>(val stubType: StubType, val position: ViewPosition) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>

internal class OnRemoveStub<SOURCE_ITEM, ITEM_WITH_INDEX> : CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>