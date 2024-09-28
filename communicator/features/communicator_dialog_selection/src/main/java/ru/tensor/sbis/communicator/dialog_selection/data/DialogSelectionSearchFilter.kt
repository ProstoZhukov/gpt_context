package ru.tensor.sbis.communicator.dialog_selection.data

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.dialog_selection.presentation.RECIPIENT_LIST_SIZE
import ru.tensor.sbis.communicator.generated.ThemeFilter

/**
 * Комбинированный фильтр для контроллеров экрана выбора диалога/участников
 * @property recipientsFilter фильтр для контроллера получателей
 * @property dialogsFilter    фильтр для контроллера диалогов
 *
 * @author vv.chekurda
 */
internal data class DialogSelectionSearchFilter(
    val recipientsFilter: RecipientsFilter,
    val dialogsFilter: DialogsFilter
)

/**
 * Фильтр для контроллера получателей
 * @property searchString поисковая строка
 * @property excludeList  список идентификаторов персон, которых необходимо исключить для запроса
 * @property count        запрашиваемое количество
 */
internal data class RecipientsFilter(
    val searchString: String = StringUtils.EMPTY,
    val excludeList: List<String> = arrayListOf(),
    val count: Int = RECIPIENT_LIST_SIZE
)

/**
 * Фильтр для контроллера диалогов
 */
internal typealias DialogsFilter = ThemeFilter