package ru.tensor.sbis.catalog_decl.catalog

/**
 * Перечисление весовых единиц измерения
 * @param code код ед. измерения
 *
 * @author sp.lomakin
 */
enum class MeasureUnit(val code: String) {
    GRAM("163"),
    KILOGRAM("166"),
    TON("168"),
    METER("006"),
    MILILITER("111"),
    LITER("112"),
    CUBIC_METER("113");

    /**
     * Проверяет является ли ед. измерения весовой
     */
    fun isWeightMeasureUnit(): Boolean = when (this) {
        GRAM,
        KILOGRAM,
        TON -> true
        else -> false
    }

    /**
     * Явояется ли вес объемом
     */
    fun isVolume(): Boolean = when (this) {
        LITER,
        MILILITER -> true
        else -> false
    }

    companion object {

        /**
         * Получить [MeasureUnit] из кода.
         *
         * @param code
         */
        fun fromCode(code: String): MeasureUnit? {
            return values().find { it.code == code }
        }

        /**
         * Проверяет является ли ед. измерения весовой
         * @param measureUnitCode код проверяемой ед. измерения
         */
        fun isWeightMeasureUnit(measureUnitCode: String): Boolean {
            return fromCode(measureUnitCode)?.isWeightMeasureUnit() ?: false
        }
    }

}