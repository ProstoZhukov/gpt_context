package ru.tensor.sbis.design.decorators.number

import ru.tensor.sbis.design.decorators.FontWeight

/**
 * @author ps.smirnyh
 */
interface NumberDecoratorConfigApi {

    /** Размер шрифта. */
    var fontSize: NumberDecoratorFontSize

    /** Стиль текста. */
    var fontColorStyle: NumberDecoratorFontColorStyle

    /** Толщина текста. */
    var fontWeight: FontWeight

    /** Зачеркнутый стиль текста. */
    var isFontStrikethrough: Boolean

    /** Количество знаков после запятой. */
    var precision: UByte

    /** Режим округления. */
    var roundMode: RoundMode

    /** Использование группировки по разрядам. */
    var useGrouping: Boolean

    /** Отображение нулевых копеек. */
    var showEmptyDecimals: Boolean

    /** Режим отображение аббривиатуры сокращения числа. */
    var abbreviationType: AbbreviationType

    /** @SelfDocumented */
    fun changedStyle(other: NumberDecoratorConfigApi): Boolean

    /** @SelfDocumented */
    fun changedFormatting(other: NumberDecoratorConfigApi): Boolean

    /** @SelfDocumented */
    fun changedAbbreviation(other: NumberDecoratorConfigApi): Boolean
}