package ru.tensor.sbis.design.view.input.mask.phone.api

import ru.tensor.sbis.design.view.input.mask.phone.PhoneFormat
import ru.tensor.sbis.design.view.input.mask.phone.PhoneInputView

/**
 * Api поля ввода телефона [PhoneInputView].
 *
 * @author ps.smirnyh
 */
interface PhoneInputViewApi {

    /**
     * Режим ввода телефона.
     * Может быть мобильный или общий.
     */
    var phoneFormat: PhoneFormat

    /**
     * Код страны для мобильного номера телефона.
     * По умолчанию +7 [RUSSIA_COUNTRY_CODE].
     */
    var areaCode: UShort
}

/** Код страны по умолчанию. */
const val RUSSIA_COUNTRY_CODE: UShort = 7u