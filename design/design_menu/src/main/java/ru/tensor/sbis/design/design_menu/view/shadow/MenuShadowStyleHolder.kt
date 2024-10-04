package ru.tensor.sbis.design.design_menu.view.shadow

/**
 * StyleHolder для параметров [ShadowView] тени.
 *
 * @author ra.geraskin
 */
internal interface MenuShadowStyleHolder {

    /** Цвет тени, отображаемой на верхней и нижней границе списка элементов меню при отображении в шторке. */
    val shadowColor: Int

    /** Высота тени, отображаемой на верхней и нижней границе списка элементов меню при отображении в шторке. */
    val shadowHeight: Int

}