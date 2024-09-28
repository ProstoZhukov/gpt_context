package ru.tensor.sbis.design.contact_data_view

import java.util.*

/**
 * Слушатель выбора элемента из [SbisContactDataView]
 *
 * @param phoneNumber индекс выбранного сегмента
 * @param id идентификатор пользователя к которому привязан номер(если есть)
 *
 * @author av.efimov1
 */
typealias ClickElementListener = (phoneNumber: String, id: UUID?) -> Unit