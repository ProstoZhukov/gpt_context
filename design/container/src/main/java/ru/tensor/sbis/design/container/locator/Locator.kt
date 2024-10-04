package ru.tensor.sbis.design.container.locator

import android.graphics.Rect
import android.view.View
import io.reactivex.Observable

/**
 * Локатор это класс который инкассирует в себе логику расчета положения контейнера. Каждый локатор вычисляет положение только по одной из осей.
 * @author ma.kolpakov
 */
interface Locator {
    /**
     * Применить локатор к контейнеру.
     * @param root корневая вью фрагмента в котором рисуется сам контейнер
     * @param parent корневая вью окна вызывающего контейнер
     * @param contentRect область необходимая контенту
     */
    fun apply(root: View, parent: View, contentRect: Rect)
    fun dispose()
    val offsetSubject: Observable<LocatorCalculatedData>

    /**
     * Правила, по которым применяются стандартные отступы от краёв экрана.
     * Для изменения через публичное API доступны только вертикальные отступы.
     */
    val rules: ScreenLocatorRules
}

/**
 * Данные которые генерирует локатор
 * @param position итоговая координата контейнера
 * @param maxSize максимально допустимы размер контейнера на случай если контент не помещается в отведенное место
 * если не 0 то контейнер будет установлен этот размер
 * @param gravity выравнивание контейнера в родителе
 */
data class LocatorCalculatedData(val position: Int, val maxSize: Int, val gravity: Int)

/**
 * Данные которые локатор использует для вычисления позиции контейнера
 *
 */
internal data class LocatorSrcData(
    /**
     * Размер родителя на котором отрисовывается контейнер
     */
    var rootSize: Int = 0,

    /**
     * Смещение вызывающего контента относительно родительской для конетейнера вью (Требуется для перевода позиции
     * вызывающего элемента в систему координат контейнера)
     */
    var rootOffset: Int = 0,
    /**
     * Размер ограничивающей области
     */
    var boundsSize: Int = 0,
    /**
     * Позиция ограничивающей области
     */
    var boundsPos: Int = 0,
    /**
     * Размер контента в контейнере
     */
    var contentSize: Int = 0,
    /**
     * Отступ вначале
     */
    var marginStart: Int = 0,
    /**
     * Отступ в конце
     */
    var marginEnd: Int = 0
)

/**
 * Дополнительные данные для позиционирования относительно вызывающего элемента при позиции контейнера
 */
class AnchorLocatorSrcData(
    var anchorSize: Int = 0,
    var anchorPosition: Int = 0,
    var innerPosition: Boolean = false,
    var pixelOffset: Int = 0,
    var force: Boolean = false
)

/**
 * Типы выравнивания контента
 */
enum class LocatorAlignment {
    START,
    CENTER,
    END;

    internal fun invert() = when (this) {
        START -> END
        CENTER -> CENTER
        END -> START
    }
}