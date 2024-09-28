package ru.tensor.sbis.design.rating.model

/**
 * Режим закрашивания иконок.
 *
 * @author ps.smirnyh
 */
enum class SbisRatingPrecision {

    /** Полное закрашивание иконки, независимо от дробной части. */
    FULL,

    /** Поддержка половинчатого закрашивания иконки при дробной части >= 0.5. */
    HALF
}