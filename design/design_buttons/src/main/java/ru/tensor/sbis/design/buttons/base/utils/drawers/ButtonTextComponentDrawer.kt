package ru.tensor.sbis.design.buttons.base.utils.drawers

/**
 * Интерфейс объекта для рисования текста кнопки. Реализации должны быть легковесными и
 * поддерживать простое сравнивание, чтобы избегать лишних обновлений.
 *
 * @author ma.kolpakov
 */
internal interface ButtonTextComponentDrawer : ButtonComponentDrawer {

    /**
     * Максимальное пространство, которое доступно для текста.
     */
    var maxWidth: Float

    /**
     * Вычислить пространство, которое занимает текст.
     */
    fun measureText(text: String): Float
}