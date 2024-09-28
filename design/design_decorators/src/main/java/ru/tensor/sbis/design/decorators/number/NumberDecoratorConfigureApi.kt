package ru.tensor.sbis.design.decorators.number

typealias NumberConfiguration = NumberDecoratorConfigApi.() -> Unit

/**
 * Интерфейс конфигурирования декоратора.
 *
 * @author ps.smirnyh
 */
interface NumberDecoratorConfigureApi {

    /** Изменение конфигурации декоратора. */
    fun configure(newConfig: NumberConfiguration): Boolean
}