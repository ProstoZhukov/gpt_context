package ru.tensor.sbis.communication_decl.selection.universal

import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.SelectionUseCase

/**
 * Use-case компонента универсального выбора.
 *
 * @author vv.chekurda
 */
sealed class UniversalSelectionUseCase(override val name: String) : SelectionUseCase {

    /**
     * Признак доступности выбора папок в качестве элемента результата.
     * При false в папку можно только провалиться.
     */
    abstract val isFoldersSelectable: Boolean

    override val itemsLimit: Int? = null

    class SelectAppliedAddFieldsUseCase(
        typeName: String,
        kindName: String?,
        isMultiSelectable: Boolean
    ): UniversalSelectionUseCase("select_applied_add_fields") {

        override val args: HashMap<String, String?> = hashMapOf<String, String?>()
            .apply {
                put("add_field_type", typeName)
                put("add_field_kind", kindName)
            }

        override val selectionMode: SelectionMode =
            if (isMultiSelectable) SelectionMode.REPLACE_ALL_IF_FIRST else SelectionMode.SINGLE

        // Временный хардкод по kindName, т.к. именно для этого типа нужно убрать выбор папок
        // Планируем сделать передачу этого признака с контроллера по задаче
        // https://dev.saby.ru/opendoc.html?guid=c5d8892c-3018-4bdb-9361-1fd098ac2001
        override val isFoldersSelectable: Boolean = kindName != "employee_on_pos"
    }
}