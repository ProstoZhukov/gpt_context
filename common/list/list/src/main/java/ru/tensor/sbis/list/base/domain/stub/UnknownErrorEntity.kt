package ru.tensor.sbis.list.base.domain.stub

import ru.tensor.sbis.design.stubview.R
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.base.presentation.StubEntity
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory

/**
 * Состояние "бизнес модели"(БМ) экрана списка, когда получена неожиданная необработанная ошибка в ходе выполнения кода.
 *
 * @property updateAction событие по нажатию кнопки "Обновить" в заглушке.
 */
class UnknownErrorEntity(
    private val updateAction: () -> Unit
) : StubEntity {

    override fun provideStubViewContentFactory(): StubViewContentFactory = {
        StubViewCase.SBIS_ERROR.getContent(mapOf(R.string.design_stub_view_sbis_error_details_clickable to updateAction))
    }
}