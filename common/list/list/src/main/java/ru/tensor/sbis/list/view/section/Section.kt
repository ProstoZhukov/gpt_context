package ru.tensor.sbis.list.view.section

import androidx.annotation.ColorInt
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Блок элементов.
 * @property items List<Item<out Any, out ViewHolder>> @SelDocumented.
 * @property options SectionOptions @SelDocumented.
 * @constructor
 */
data class Section(
    val items: List<AnyItem>,
    private val options: SectionOptions = Options()
) : SectionOptions by options

/**
 * Опции блоков элементов.
 */
class Options(
    override val hasDividers: Boolean = true,
    @ColorInt
    override val indicatorColor: Int = NO_COLOR,
    override val needDrawDividerUnderFirst: Boolean = false,
    override val cardOption: CardOption = NoCards,
    override val needDrawDividerUpperLast: Boolean = true,
    override val hasTopMargin: Boolean = true,
    override val cardMarginDp: Int = cardPaddingByStandardDp
) : SectionOptions {

    override val hasIndicatorColor = indicatorColor != NO_COLOR

    companion object {
        private const val NO_COLOR = 0
        val defaultValue = Options(hasTopMargin = false, needDrawDividerUnderFirst = true)
    }
}

private const val cardPaddingByStandardDp = 8