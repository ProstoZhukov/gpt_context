package ru.tensor.sbis.richtext.view.gesture

import android.text.Layout
import android.text.Spanned

/**
 * Интерфейс для поиска спанов в тексте на основе жестов пользователя.
 *
 * @author am.boldinov
 */
interface GestureSpanFinder {

    /**
     * Выполняет поиск спанов по координатам касания [x] и [y] внутри [text] на [layout].
     * Точность поиска регулируется с помощью [strategy].
     */
    fun <T> find(
        layout: Layout,
        text: Spanned,
        x: Int,
        y: Int,
        type: Class<T>,
        strategy: Strategy = Strategy.STRONG_CHARACTER
    ): Array<T>

    /**
     * Определяет стратегию поиска спанов в тексте.
     */
    enum class Strategy {
        /**
         * Ищет спаны с погрешностью в рамках ширины строки.
         * Если заданы координаты, которые превышают ширину строки (касание вне текста), то будет
         * произведен поиск в рамках крайних символов этой строки.
         */
        SOFT_LINE,

        /**
         * Ищет спаны строго внутри текста.
         * Поиск осуществляется только по координатам, под которыми отрисованы символы текста,
         * в противном случае спаны найдены не будут.
         */
        STRONG_CHARACTER
    }
}