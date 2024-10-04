package ru.tensor.sbis.hallscheme.v2

/**
 * Содержит спецификации для отображения элементов схемы зала (размеры, отступы и т.п.).
 * @author aa.gulevskiy
 */
internal class HallSchemeSpecHolder(
    val tableSpec: TableSpec,
    val chairSpec: ChairSpec,
    val sofaSpec: SofaSpec,
    val billSpec: BillSpec,
    val bookingSpec: BookingSpec,
    val assigneeSpec: AssigneeSpec
) {
    companion object {
        /**
         * Коэффициент для расчёта ширины (высоты) бара (широкого).
         * Получен путём деления значений ширины баров (широкого и стандартного), взятых из веба.
         */
        const val BARS_HEIGHT_FACTOR = 1.4F

        /**
         * Коэффициент для расчёта длины верхнего края, подобран эмпирическим путём.
         *
         *   |
         *   v
         *  ___
         * |   |________
         * |            |
         * |____________|
         */
        const val BARS_TOP_EDGE_WIDTH_FACTOR = 0.8F
    }

    /**
     * Спецификация стола.
     * @param extraWidth дополнительное место от крайних стульев до конца столешницы.
     * @param cornerRadius радиус скругления угла столешницы.
     * @param circleEnhancement величина, на которую увеличивается круглый или овальный стол.
     * @param padding отступ до столешницы по умолчанию. В пределах этого отступа отображаются стулья и диваны.
     */
    class TableSpec(
        val extraWidth: Int = 0,
        val cornerRadius: Int = 0,
        val circleEnhancement: Int = 0,
        val padding: Int = 0
    )

    /**
     * Спецификация декора.
     * @param width длина.
     * @param height высота.
     */
    class DecorSpec(val width: Int, val height: Int)

    /**
     * Спецификация для стула.
     * @param height высота обрезанного изображения стула.
     * @param fullHeight высота необрезанного изображения стула.
     * @param width ширина стула.
     */
    class ChairSpec(val height: Int, val fullHeight: Int, val width: Int)

    /**
     * Спецификация для дивана.
     * @param straightHeight высота прямого дивана.
     * @param straightWidth ширина прямого дивана.
     * @param sectionWidth ширина одной секции дивана.
     * @param cornerHeight высота углового дивана.
     * @param cornerWidth ширина углового дивана.
     */
    class SofaSpec(
        val straightHeight: Int,
        val straightWidth: Int,
        val sectionWidth: Int,
        val cornerHeight: Int,
        val cornerWidth: Int
    )

    /**
     * Спецификация для счёта.
     * @param width длина.
     * @param height высота.
     * @param offset отступ относительно другого счёта.
     */
    class BillSpec(val height: Int, val width: Int, val offset: Int)

    /**
     * Спецификация для брони.
     * @param height высота.
     * @param intersection на сколько вторая бронь "наползает" на первую.
     */
    class BookingSpec(val height: Int, val intersection: Int)

    /**
     * Спецификация для аватарки привязанного пользователя.
     * @param size ширина и высота.
     */
    class AssigneeSpec(val size: Int)

    /**
     * Спецификация места.
     * @param width длина.
     * @param height высота.
     */
    class PlaceSpec(val width: Int, val height: Int)
}