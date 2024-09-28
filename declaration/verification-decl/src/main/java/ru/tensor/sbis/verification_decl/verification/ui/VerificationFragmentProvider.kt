package ru.tensor.sbis.verification_decl.verification.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.verification.ConfirmationType
import ru.tensor.sbis.verification_decl.verification.VerificationContact

/**
 * Поставщик фрагментов для верификации.
 *
 * @author ar.leschev
 */
interface VerificationFragmentProvider : Feature {

    /**
     * Создать фрагмент для верификации контакта [contact]
     */
    fun createVerificationContactFragment(contact: VerificationContact): Fragment

    /**
     * Создать фрагмент для верификации контакта с возможностью выбора из списка неподтвержденных контактов(если таковые имеются)
     */
    fun createVerificationFragment(confirmationType: ConfirmationType = ConfirmationType.CALL): Fragment

    /**
     * Создать фрагмент с информационным сообщением о необходимости подтвердить контакт и возможностью выбора из списка неподтвержденных контактов(если таковые имеются)
     * @param forMessage будет ли отображен созданный диалог в сообщениях(от этого зависит отображаемое текстовое сообщение)
     */
    fun createVerificationWithAlertFragment(
        confirmationType: ConfirmationType = ConfirmationType.CALL,
        forMessage: Boolean = false
    ): Fragment
}