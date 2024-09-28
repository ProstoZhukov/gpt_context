package ru.tensor.sbis.hallscheme.v2.business

/**
 * Тип стола.
 */
internal enum class TableType(
    vararg typeValues: Int
) {
    /**
     * Прямоугольный, стулья с 4 сторон.
     */
    FOUR_SIDES(0),

    /**
     * Прямоугольный, стулья с 2 сторон.
     */
    TWO_SIDES(1),

    /**
     * Круглый, стулья с 4 сторон.
     */
    CIRCLE(2),

    /**
     * Овальный с полукруглыми краями, стулья с 2 сторон.
     */
    OVAL(3),

    /**
     * 4 - Стол с диваном с 1 стороны.
     * 5 - Стол с диваном с 2 сторон.
     * 6 - L-образный слева.
     * 7 - L-образный справа.
     * 8 - U-образный.
     */
    SOFA_ONE_SIDE(4, 6, 7, 8),

    /**
     * Стол с диваном с 2 сторон.
     */
    SOFA_TWO_SIDES(5),

    /**
     * Смешанный стол (сторона с диваном + стулья напротив).
     */
    COMBINED(9);

    /**@SelfDocumented*/
    val values = typeValues

    companion object {
        /**@SelfDocumented*/
        fun getByValue(value: Int): TableType =
            values().find { value in it.values } ?: FOUR_SIDES
    }
}