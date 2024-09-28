package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.SpType

/**
 * Тип поддерживаемых точек продажи в приложении.
 *
 * p.s. необходимо перенести в 'sales_point_list.CountryFeatureSalesPointList',
 * т.к. сейчас есть проблемы с цикличными зависимостями между модулями.
 * https://online.sbis.ru/opendoc.html?guid=fcce8c75-b52b-4eba-bf22-e21f3c730585&client=3
 */
enum class SalesPointSupportedType {
    /** Точки продажи: "Магазин". */
    SHOP,

    /** Точки продажи: "Ресторан". */
    RESTAURANT,

    /** Точки продажи: "Салон красоты". */
    BEAUTY
}

/**
 * Преобразование [SpType] в [SalesPointSupportedType].
 *
 * Пока публичное, т.к. используется в 'crud_devices_settings'.
 * https://online.sbis.ru/opendoc.html?guid=fcce8c75-b52b-4eba-bf22-e21f3c730585&client=3
 */
fun SpType.toSupportedSalesPointTypeUI() = when (this) {
    SpType.RETAIL -> SalesPointSupportedType.SHOP
    SpType.PRESTO -> SalesPointSupportedType.RESTAURANT
    SpType.BEAUTY -> SalesPointSupportedType.BEAUTY
}

/**
 * Преобразование [SalesPointSupportedType] в [SpType].
 *
 * Пока публичное, т.к. используется в 'crud_devices_settings'.
 * https://online.sbis.ru/opendoc.html?guid=fcce8c75-b52b-4eba-bf22-e21f3c730585&client=3
 */
fun SalesPointSupportedType.toSpTypeNative() = when (this) {
    SalesPointSupportedType.SHOP -> SpType.RETAIL
    SalesPointSupportedType.RESTAURANT -> SpType.PRESTO
    SalesPointSupportedType.BEAUTY -> SpType.BEAUTY
}