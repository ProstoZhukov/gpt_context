package ru.tensor.sbis.design.chips.models

/**
 * Модель элемента.
 *
 * @author ps.smirnyh
 */
data class SbisChipsItem(

    /** @SelfDocumented */
    val id: Int,

    /**
     * Модель заголовка.
     *
     * При значении null заголовка не будет.
     */
    val caption: SbisChipsCaption? = null,

    /**
     * Значение счетчика.
     *
     * При значении null счетчика не будет.
     */
    val counter: Int? = null,

    /**
     * Модель иконки.
     *
     * При значении null иконки не будет.
     */
    val icon: SbisChipsIcon? = null
)