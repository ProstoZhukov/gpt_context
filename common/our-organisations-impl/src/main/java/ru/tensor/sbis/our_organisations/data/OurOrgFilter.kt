package ru.tensor.sbis.our_organisations.data

import java.util.UUID

/**
 * Фильтр для списка организаций.
 *
 * @property searchString Строка поиска по названию/ИНН/КПП.
 * @property parent Идентификатор начала дерева.
 * @property ids Список идентификаторов для фильтрации списка.
 * @property addSalesPoints Отбирать также точки продаж.
 * @property salePointOnly Отбирать только точки продаж.
 * @property primaryOrgOnly Отобрать только главную организацию.
 * @property headsOnly Отбирать только головные организации.
 * @property requestNewData
 * @property offset С какого элемента начинать список.
 * @property count Сколько элементов в списке.
 * Если передать -1, то будут отобраны все от from без ограничений
 * @property withEliminated отображать организации включая ликвидированные.
 * @property scopesAreas список зон доступа, по которым отбирать компании.
 * @property showInnerCompany Отбирать также запись 'Управленческий учет'.
 *
 * @author mv.ilin
 */
internal data class OurOrgFilter(
    var searchString: String? = null,
    var parent: UUID? = null,
    var ids: List<Int> = emptyList(),
    var addSalesPoints: Boolean = true,
    var salePointOnly: Boolean = false,
    var primaryOrgOnly: Boolean = false,
    var headsOnly: Boolean = true,
    var requestNewData: Boolean = true,
    var offset: Long = 0,
    var count: Int = 0,
    var withEliminated: Boolean,
    var scopesAreas: List<String> = emptyList(),
    val showInnerCompany: Boolean = false
)
