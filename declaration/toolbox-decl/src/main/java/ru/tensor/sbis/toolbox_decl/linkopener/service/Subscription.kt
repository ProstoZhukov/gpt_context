package ru.tensor.sbis.toolbox_decl.linkopener.service

/**
 * Подписка на обновления от контроллера.
 *
 * @author us.bessonov
 */
interface Subscription {

    /** @SelfDocumented */
    fun enable()

    /** @SelfDocumented */
    fun disable()
}