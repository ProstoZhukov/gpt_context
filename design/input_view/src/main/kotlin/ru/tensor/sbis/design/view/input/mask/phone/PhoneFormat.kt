package ru.tensor.sbis.design.view.input.mask.phone

import ru.tensor.sbis.design.view.input.mask.phone.formatter.COMMON_FORMAT
import ru.tensor.sbis.design.view.input.mask.phone.formatter.MOB_FORMAT
import ru.tensor.sbis.design.view.input.mask.phone.formatter.MOB_LEN
import ru.tensor.sbis.design.view.input.mask.phone.formatter.PhoneFormatDecoration

/**
 * Варианты форматирования телефона в [PhoneInputView].
 *
 * @author ps.smirnyh
 */
enum class PhoneFormat(@PhoneFormatDecoration val format: Int) {

    /**
     * Возможность вводить только мобильные номера телефоном.
     * Не поддерживаются стационарные номера.
     * Работает ограничение по длине.
     */
    MOBILE(MOB_FORMAT or MOB_LEN),

    /**
     * Возможность вводить любые номера телефонов.
     * Есть поддержка стационарных номеров.
     */
    MIXED(COMMON_FORMAT or MOB_FORMAT)
}