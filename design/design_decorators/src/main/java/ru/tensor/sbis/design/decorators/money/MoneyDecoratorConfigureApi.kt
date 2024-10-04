package ru.tensor.sbis.design.decorators.money

typealias MoneyConfiguration = MoneyDecoratorConfigApi.() -> Unit

/**
 * Интерфейс конфигурирования декоратора.
 *
 * @author ps.smirnyh
 */
interface MoneyDecoratorConfigureApi {

    /** Изменение конфигурации декоратора. */
    fun configure(newConfig: MoneyConfiguration): Boolean
}