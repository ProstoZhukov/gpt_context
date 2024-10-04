package ru.tensor.sbis.design.decorators.number

import ru.tensor.sbis.design.decorators.FontColorStyle
import ru.tensor.sbis.design.decorators.FontWeight

/**
 * Класс конфигурации настроек числового декоратора.
 *
 * @author ps.smirnyh
 */
data class NumberDecoratorConfig(
    override var fontSize: NumberDecoratorFontSize = NumberDecoratorFontSize.Defaults.M,
    override var fontColorStyle: NumberDecoratorFontColorStyle =
        NumberDecoratorFontColorStyle(FontColorStyle.Defaults.DEFAULT),
    override var fontWeight: FontWeight = FontWeight.DEFAULT,
    override var isFontStrikethrough: Boolean = false,
    override var precision: UByte = 4u,
    override var roundMode: RoundMode = RoundMode.TRUNC,
    override var useGrouping: Boolean = false,
    override var showEmptyDecimals: Boolean = false,
    override var abbreviationType: AbbreviationType = AbbreviationType.NONE
) : NumberDecoratorConfigApi {
    override fun changedStyle(other: NumberDecoratorConfigApi): Boolean =
        fontSize != other.fontSize ||
            fontColorStyle != other.fontColorStyle ||
            fontWeight != other.fontWeight ||
            isFontStrikethrough != other.isFontStrikethrough

    override fun changedFormatting(other: NumberDecoratorConfigApi): Boolean =
        precision != other.precision ||
            roundMode != other.roundMode ||
            useGrouping != other.useGrouping ||
            showEmptyDecimals != other.showEmptyDecimals

    override fun changedAbbreviation(other: NumberDecoratorConfigApi): Boolean =
        abbreviationType != other.abbreviationType
}