package ru.tensor.sbis.consignment_decl.registry.model

import androidx.annotation.AttrRes
import java.util.UUID

/**
 * Информация по документу.
 *
 * @property activeEventId идентификатор активного события (смотри ЭДО).
 * @property phaseName название фазы.
 * @property phaseColor цвет фазы.
 * @property canExecutePassage можно ли менять фазу.
 * @property docType тип документа.
 * @property regulationName название регламента.
 * @property isImportant признак важности.
 *
 * @author kv.martyshenko
 */
data class DocInfo(
    val activeEventId: UUID?,
    val phaseName: String?,
    @AttrRes val phaseColor: Int,
    val canExecutePassage: Boolean,
    val docType: String,
    val regulationName: String,
    val isImportant: Boolean
)