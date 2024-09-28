package ru.tensor.sbis.design.view.input.base.utils.factory

import ru.tensor.sbis.design.view.input.base.ValidationStatusAdapter
import ru.tensor.sbis.design.view.input.base.utils.style.BaseStyleHolder

/**
 * Класс для создания адаптера валидации полей ввода.
 *
 * @author ps.smirnyh
 */
internal class ValidationStatusAdapterFactory {

    /** @SelfDocumented */
    fun create(styleHolder: BaseStyleHolder): ValidationStatusAdapter =
        ValidationStatusAdapter(styleHolder)
}