package ru.tensor.sbis.our_organisations.data

/**
 * Перечисление типов элементов в списке организаций.
 *
 * @author mv.ilin
 */
internal enum class OurOrgItemType {
    /** Отображение без города организации. **/
    SIMPLE,
    /** Отображение вместе с городом организации. **/
    COMPLEX;
}
