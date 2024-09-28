package ru.tensor.sbis.communication_decl.selection.universal

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionDoneButtonVisibilityMode
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.result_manager.EMPTY_REQUEST_KEY
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalItemId

/**
 * Конфигурация компонента универсального выбора.
 * @see SelectionConfig
 *
 * @property isFoldersSelectable true, если папки можно выбирать в качестве результата.
 * В ином случае в папку можно только провалиться.
 * @property closeOnComplete закрыть фрагмент при подтверждении выбора.
 * @property closeOnCancel закрыть фрагмент при отмене выбора.
 *
 * @author vv.chekurda
 */
data class UniversalSelectionConfig(
    override val useCase: UniversalSelectionUseCase,
    override val selectionMode: SelectionMode = useCase.selectionMode,
    override val doneButtonMode: SelectionDoneButtonVisibilityMode = useCase.doneButtonMode,
    override val headerMode: SelectionHeaderMode = useCase.headerMode,
    override val itemsLimit: Int? = useCase.itemsLimit,
    override val excludeList: List<UniversalItemId>? = null,
    override val requestKey: String = EMPTY_REQUEST_KEY,
    override val enableSwipeBack: Boolean = false,
    override val themeRes: Int? = null,
    val isFoldersSelectable: Boolean = useCase.isFoldersSelectable,
    val closeOnComplete: Boolean = true,
    val closeOnCancel: Boolean = closeOnComplete
) : SelectionConfig