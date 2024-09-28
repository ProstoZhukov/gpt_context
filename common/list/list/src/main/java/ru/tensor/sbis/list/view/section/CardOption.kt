package ru.tensor.sbis.list.view.section

import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager

/**
 * Настройка отображения элементов списка в секции карточками.
 */
sealed interface CardOption

/**
 * Элементы не должны отображаться карточками.
 */
object NoCards : CardOption

/**
 * Элементы отображаются карточкой, [spanSize] используется для определения размера карточки в [GridLayoutManager].
 * В небольших пространства, например, в телефонной ориентации, карточки всегда отображаются в одну колонку.
 */
sealed class Spanned(val spanSize: Int) : CardOption

/**
 * Размер ячейки - 1 колонка всегда.
 */
@Suppress("unused")
object SpanBig : Spanned(6)

/**
 * Размер ячейки - 2 колонки для самого большого возможного пространства, для остальных - 1
 */
@Suppress("unused")
object SpanMedium : Spanned(3)

/**
 * Размер ячейки - 3 колонки для самого большого возможного пространства, для остальных - 2 или 1.
 */
@Suppress("unused")
object SpanSmall : Spanned(2)

/**
 * Вся секция является карточкой.
 * @param cardMarginDp отступы карточки-секции от краев.
 * @param cardCorners опции углов карточки.
 */
@Suppress("unused")
class SingleCard(
    val cardMarginDp: Rect = Rect(4, 4, 4, 4),
    val cardCorners: CardCorners
) : Spanned(6) {

    /**
     * @param roundTopCorner будут ли скруглены верхние углы карточки.
     * @param roundBottomCorner будут ли скруглены нижние углы карточки.
     */
    constructor(
        cardMarginDp: Rect = Rect(4, 4, 4, 4),
        roundTopCorner: Boolean = true,
        roundBottomCorner: Boolean = true
    ) : this(cardMarginDp, CardCorners(roundTopCorner, roundTopCorner, roundBottomCorner, roundBottomCorner))
}
