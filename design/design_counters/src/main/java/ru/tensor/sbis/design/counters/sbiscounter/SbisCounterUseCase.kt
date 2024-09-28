package ru.tensor.sbis.design.counters.sbiscounter

/**
 * Стилизация счетчика в зависимости от места его применения.
 *
 * @author da.zolotarev
 */
enum class SbisCounterUseCase {

    /** Стилизация для размещения во всех местах, кроме навигации. */
    REGULAR,

    /** Стилизация для компонентов навигации (ННП). */
    NAVIGATION
}