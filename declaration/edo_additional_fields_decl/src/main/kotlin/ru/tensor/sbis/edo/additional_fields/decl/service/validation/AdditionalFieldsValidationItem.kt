package ru.tensor.sbis.edo.additional_fields.decl.service.validation

import ru.tensor.sbis.edo.additional_fields.decl.model.AdditionalItemModel
import java.util.UUID

/**
 * Элемент валидации полей
 *
 * @property id     Идентификатор элемента. Равен [AdditionalItemModel.id]
 * @property title  Название элемента. Равен [AdditionalItemModel.title]
 *
 * @author sa.nikitin
 */
class AdditionalFieldsValidationItem(val id: UUID, val title: String)