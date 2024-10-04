package ru.tensor.sbis.design.view.input.mask.api

/**
 * Базовое api поля ввода с маской.
 *
 * @author ps.smirnyh
 */
interface BaseMaskInputViewApi {

    /**
     * Маска, где где 0 - число, А - буква, * - любой символ.
     */
    var mask: String

}