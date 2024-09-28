package ru.tensor.sbis.design.contact_data_view.model

import ru.tensor.sbis.design.contact_data_view.api.SbisContactDataController.Companion.DEFAULT_NUMBER_OF_VISIBLE_ELEMENTS

/**
 * Модель данных для отображения списка из контактных данных
 *
 * @property data список данных для отображения
 * @property maxNumberVisibleElements количество видимых элементов из списка(остальные будут скрыты)
 *
 * @author av.efimov1
 */
data class SbisContactDataModel(
    val data: List<SbisContactPhoneNumberModel>,
    val maxNumberVisibleElements: Int = DEFAULT_NUMBER_OF_VISIBLE_ELEMENTS
)