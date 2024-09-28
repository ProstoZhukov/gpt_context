package ru.tensor.sbis.list.view.section

/**
 * Настройки стиля для блока элементов списка.
 *
 * @property hasDividers Boolean нужно ли отображать стандартные разделители между ячейками в блоке.
 * @property indicatorColor Int цвет полоски-индикатора над первым элементом группы.
 * @property hasIndicatorColor Boolean признак наличия полоски-индикатора.
 * @property needDrawDividerUnderFirst Boolean нужно ли отображать разделитель под первым элементом в блоке (если
 * разделители отображаются в принципе, т.е. `[hasDividers] = true`). Как правило, это не требуется, потому что первый
 * элемент - заголовок блока.
 * @property cardOption опции настройки отображения карточек.
 * @property needDrawDividerUpperLast Boolean нужно ли отображать разделитель над последним элементом в блоке (если
 * разделители отображаются в принципе, т.е. `[hasDividers] = true`).
 */
interface SectionOptions {
    val hasDividers: Boolean
    val indicatorColor: Int
    val hasIndicatorColor: Boolean
    val needDrawDividerUnderFirst: Boolean
    val cardOption: CardOption
    val needDrawDividerUpperLast: Boolean

    /**
     * Признак необходимости отображения элементов списка карточками,
     */
    val hasTopMargin: Boolean

    /**
     * Отступы с боков и сверху для карточек.
     */
    val cardMarginDp: Int
}

/** @SelfDocumented */
fun SectionOptions.compare(other: SectionOptions?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (hasDividers != other.hasDividers) return false
    if (indicatorColor != other.indicatorColor) return false
    if (needDrawDividerUnderFirst != other.needDrawDividerUnderFirst) return false
    if (cardOption != other.cardOption) return false
    if (needDrawDividerUpperLast != other.needDrawDividerUpperLast) return false
    if (hasTopMargin != other.hasTopMargin) return false
    if (cardMarginDp != other.cardMarginDp) return false
    if (hasIndicatorColor != other.hasIndicatorColor) return false
    return true
}