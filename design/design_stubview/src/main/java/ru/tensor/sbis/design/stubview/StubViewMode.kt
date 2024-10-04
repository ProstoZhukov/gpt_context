package ru.tensor.sbis.design.stubview

import ru.tensor.sbis.design.stubview.R.styleable.StubView_StubView_mode

/**
 * Режим отображения заглушки
 *
 * @author ma.kolpakov
 */
enum class StubViewMode {

    /** Стандартный режим отображения */
    BASE,

    /** Режим отображения в блоке*/
    BLOCK,

    /** Плотный режим отображения (Без отступов по вертикали)*/
    DENS;

    internal companion object {

        /**
         * Получение режима отображения по [Int] значению
         *
         * @param modeId значение, соответствующее атрибуту [StubView_StubView_mode]
         */
        fun fromId(modeId: Int): StubViewMode =
            when (modeId) {
                0 -> BASE
                1 -> BLOCK
                2 -> DENS
                else -> error("Unexpected stub view mode $modeId")
            }
    }
}
