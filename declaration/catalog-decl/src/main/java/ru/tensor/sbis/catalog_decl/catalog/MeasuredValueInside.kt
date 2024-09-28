package ru.tensor.sbis.catalog_decl.catalog

/**
 * Модель информации о весе груза на весах
 *
 * @property weight вес груза
 * @property tareWeight вес тары
 * @property unitByOkei еденицы измерения
 * @property weightFixationFeature зафиксирован ли вес
 * @property weightCalmingFeature находится ли груз в состоянии покоя
 * @property autoZeroFeature включено ли автозануление
 * @property weightOverflowFeature есть ли перевес
 * @property tareFeature включен ли учет тары
 * @property errorFeature есть ли ошибки при взвешивании
 */
data class MeasuredValueInside(
    val weight: Double,
    val tareWeight: Double,
    val unitByOkei: Int,
    val weightFixationFeature: Boolean,
    val weightCalmingFeature: Boolean,
    val autoZeroFeature: Boolean,
    val weightOverflowFeature: Boolean,
    val tareFeature: Boolean,
    val errorFeature: Boolean
)