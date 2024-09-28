package ru.tensor.sbis.list.view.section

/**
 * Опции углов карточки [SingleCard].
 *
 * @param roundTopLeft будет ли скруглен верхний левый угл карточки
 * @param roundTopRight будет ли скруглен верхний правый угл карточки
 * @param roundBottomLeft будет ли скруглен нижний левый угл карточки
 * @param roundBottomRight будет ли скруглен нижний правый угл карточки
 */
@Suppress("unused")
class CardCorners(
    val roundTopLeft: Boolean = true,
    val roundTopRight: Boolean = true,
    val roundBottomLeft: Boolean = true,
    val roundBottomRight: Boolean = true
)