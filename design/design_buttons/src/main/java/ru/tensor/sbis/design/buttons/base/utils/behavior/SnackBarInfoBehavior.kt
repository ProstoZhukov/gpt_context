package ru.tensor.sbis.design.buttons.base.utils.behavior

import androidx.annotation.Px

/**
 * Интерфейс поведения, которое поддерживает взаимодействие с SnackBar.
 *
 * @author ma.kolpakov
 */
internal interface SnackBarInfoBehavior {

    /**
     * Высота снэкбара.
     */
    @get:Px
    val snackBarHeight: Int

    /**
     * Расположение низа снэкбара.
     */
    @get:Px
    val snackBarBottom: Int
}