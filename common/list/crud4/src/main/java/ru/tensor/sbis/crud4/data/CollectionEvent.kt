package ru.tensor.sbis.crud4.data

import ru.tensor.sbis.service.generated.DirectionStatus
import ru.tensor.sbis.service.generated.Mark
import ru.tensor.sbis.service.generated.Selection
import ru.tensor.sbis.service.generated.SelectionCounter
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import java.util.ArrayList

/**
 * Событие коллекции микросервиса. Соответствуют методам кобека коллекции микросервиса, ими же и производятся.
 * [SOURCE_ITEM] класс элемента списка, который отдает микросервис.
 * [ITEM_WITH_INDEX] класс пары, содержащей элемент и индекс, который используется в методах микросервиса.
 */
internal sealed interface CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnReset<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val p0: List<SOURCE_ITEM>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnRemove<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val p0: List<Long>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnMove<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val p0: List<Pair<Long, Long>>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnAdd<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val p0: List<ITEM_WITH_INDEX>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnReplace<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val p0: List<ITEM_WITH_INDEX>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnAddThrobber<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val position: ViewPosition) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnRemoveThrobber<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL> :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnAddStub<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(
    val stubType: StubType,
    val position: ViewPosition,
    val message: String?
) : CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnPath<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val path: List<PATH_MODEL>) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnRemoveStub<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL> :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnEndUpdate<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val haveMore: DirectionStatus) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnMark<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val marked: Mark) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>

internal class OnSelect<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val selected: ArrayList<Selection>, val counter :SelectionCounter) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>
internal class OnRestorePosition<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>(val pos: Long) :
    CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>