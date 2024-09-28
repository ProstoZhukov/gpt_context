package ru.tensor.sbis.communication_decl.selection.recipient

import ru.tensor.sbis.communication_decl.selection.SelectionDoneButtonVisibilityMode
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode
import ru.tensor.sbis.communication_decl.selection.SelectionStringsConfig
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientId
import ru.tensor.sbis.communication_decl.selection.result_manager.EMPTY_REQUEST_KEY

/**
 * Конфигурация компонента выбора получателей.
 * @see SelectionConfig
 *
 * @property itemsMode режим отображения ячеек.
 * @property unfoldDepartments true, если в результате выбора необходимо вернуть распакованных получателей
 * из подразделений.
 * @property isDepartmentsSelectable true, если подразделение можно выбирать в качестве результата.
 * В ином случае в подразделение можно только провалиться.
 * @property isFinalComplete см [SelectionConfig.isFinalComplete].
 * @property canShowPersonCards true, если компонент выбора может показывать карточки персон. (Не нужно для Share)
 * @property closeOnComplete закрыть фрагмент при подтверждении выбора.
 * @property closeOnCancel закрыть фрагмент при отмене выбора.
 *
 * @author vv.chekurda
 */
data class RecipientSelectionConfig(
    override val useCase: RecipientSelectionUseCase,
    override val selectionMode: SelectionMode = useCase.selectionMode,
    override val doneButtonMode: SelectionDoneButtonVisibilityMode = useCase.doneButtonMode,
    override val headerMode: SelectionHeaderMode = useCase.headerMode,
    override val itemsLimit: Int? = useCase.itemsLimit,
    override val excludeList: List<RecipientId>? = null,
    override val requestKey: String = EMPTY_REQUEST_KEY,
    override val enableSwipeBack: Boolean = false,
    override val stringsConfig: SelectionStringsConfig? = null,
    override val isFinalComplete: Boolean = useCase.isFinalComplete,
    override val themeRes: Int? = null,
    val itemsMode: RecipientSelectionItemsMode = RecipientSelectionItemsMode.DEFAULT,
    val unfoldDepartments: Boolean = useCase.unfoldDepartments,
    val isDepartmentsSelectable: Boolean = useCase.isDepartmentsSelectable,
    val canShowPersonCards: Boolean = true,
    val closeOnComplete: Boolean = true,
    val closeOnCancel: Boolean = closeOnComplete,
) : SelectionConfig