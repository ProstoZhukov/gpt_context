package ru.tensor.sbis.wizard.decl.step

import kotlin.reflect.KClass

/**
 * Держатель шага мастера
 *
 * @author sa.nikitin
 */
interface StepHolder {

    /**
     * Получить шаг мастера по его классу [stepClass]
     *
     * @return Шаг или null, если шаг не найден или не является текущим в мастере
     */
    fun <S : Step> getStep(stepClass: KClass<S>): S?
}