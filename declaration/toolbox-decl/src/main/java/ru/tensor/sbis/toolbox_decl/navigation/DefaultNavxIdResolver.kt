package ru.tensor.sbis.toolbox_decl.navigation

import timber.log.Timber

/**
 * Предназачен для маппинга строкового идентификатора элемента в [NavxIdDecl].
 *
 * @author us.bessonov
 */
object DefaultNavxIdResolver {

    /**
     * Стратегия преобразования заданной строки в [NavxIdDecl] по умолчанию.
     * Явным образом использовать только для тестов, переопределять значение в приложении не следует.
     */
    var resolver: ((String) -> NavxIdDecl?)? = null
        set(value) {
            if (field != null) {
                Timber.w("Default NavxId resolver is replaced by $value")
            }
            field = value
        }

    /**
     * Получить [NavxIdDecl] для заданной строки, если возможно.
     */
    fun navxIdOf(identifier: String): NavxIdDecl? = resolver?.invoke(identifier)
}