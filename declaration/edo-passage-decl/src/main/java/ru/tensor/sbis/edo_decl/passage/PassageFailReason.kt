package ru.tensor.sbis.edo_decl.passage

import ru.tensor.sbis.edo.additional_fields.decl.AdditionalFieldsComponent
import ru.tensor.sbis.edo.additional_fields.decl.service.validation.AdditionalFieldsValidationFailure

/**
 * Причина неудачи перехода
 *
 * @author sa.nikitin
 */
sealed class PassageFailReason {

    /**
     * Ошибка валидации доп. полей
     *
     * ВНИМАНИЕ!
     * На прикладной стороне не нужно отображать ошибки, в том числе тостом или уведомлением
     * Если компонент доп полей встроен в карточку, то это же событие он отловит сам и отобразит ошибки
     * Нужно лишь прокрутить к первому доп полю с ошибкой
     * Для этого следует подписаться на [AdditionalFieldsComponent.autoValidationFails]
     * Т.е. на эту причину ошибки реагировать никак не нужно
     */
    class AdditionalFieldsValidationFail(
        val validationFailures: List<AdditionalFieldsValidationFailure>
    ) : PassageFailReason()

    /**
     * Внутренняя ошибка, т.е. обрабатывается самим компонентом
     *
     * Это либо штатная ошибка, например, нет интернета при переходе с подписанием,
     * либо неожиданная, т.е. ошибка в коде
     */
    class Internal : PassageFailReason()
}