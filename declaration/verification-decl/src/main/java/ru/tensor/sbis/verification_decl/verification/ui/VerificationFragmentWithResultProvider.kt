package ru.tensor.sbis.verification_decl.verification.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.verification.VerificationContact
import ru.tensor.sbis.verification_decl.verification.VerificationResultKeys

/**
 * Поставщик фрагментов верификации с результатом верификации.
 *
 * @author ar.leschev
 */
interface VerificationFragmentWithResultProvider : Feature {

    /**
     * Создать фрагмент для верификации контакта [contact], с отправкой результата через Fragment Result API.
     * @param verificationResultKeys - ключи для получения результата, см [VerificationResultKeys].
     * @param needExtraTopPadding - флаг, добавляющий отступ для контента. Необходим для
     * корректного отображения на экранах с прозрачными статус барами. Опциональный, по умолчанию false.
     * @param editContactEnabled - доступность поля контакта для редактирования.
     */
    fun createVerificationContactFragmentWithResult(
        contact: VerificationContact,
        verificationResultKeys: VerificationResultKeys,
        needExtraTopPadding: Boolean,
        editContactEnabled: Boolean = true
    ): Fragment

}