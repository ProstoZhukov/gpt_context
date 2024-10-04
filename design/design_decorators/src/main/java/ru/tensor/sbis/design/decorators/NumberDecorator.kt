package ru.tensor.sbis.design.decorators

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.text.clearSpans
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.decorators.number.AbbreviationType
import ru.tensor.sbis.design.decorators.number.FormatSettings
import ru.tensor.sbis.design.decorators.number.NumberConfiguration
import ru.tensor.sbis.design.decorators.number.NumberDecoratorApi
import ru.tensor.sbis.design.decorators.number.NumberDecoratorConfig
import ru.tensor.sbis.design.decorators.number.NumberDecoratorConfigureApi
import ru.tensor.sbis.design.decorators.number.NumberDecoratorStrikethroughSpan
import ru.tensor.sbis.design.decorators.number.abbreviationFormat
import ru.tensor.sbis.design.decorators.utils.setSpan
import ru.tensor.sbis.design.decorators.utils.takeIfExistDecimal
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.theme.global_variables.BorderThickness

/**
 * Числовой декоратор.
 *
 * [Стандарт](https://www.figma.com/proto/9gNeGciO6aEWjk1vPs0ru1/%D0%A7%D0%B8%D1%81%D0%BB%D0%BE%D0%B2%D1%8B%D0%B5-%D0%B8-%D0%B4%D0%B5%D0%BD%D0%B5%D0%B6%D0%BD%D1%8B%D0%B5-%D0%B4%D0%B5%D0%BA%D0%BE%D1%80%D0%B0%D1%82%D0%BE%D1%80%D1%8B?node-id=1554-146148&scaling=min-zoom&page-id=47%3A431&starting-point-node-id=1554%3A146148)
 *
 * @author ps.smirnyh
 */
class NumberDecorator(
    private val context: Context,
    internal var value: Double? = null,
    config: NumberConfiguration? = null
) : NumberDecoratorApi, NumberDecoratorConfigureApi {

    constructor(context: Context, value: String?, config: NumberConfiguration? = null) : this(
        context,
        value?.toDoubleOrNull(),
        config
    )

    constructor(context: Context, value: Int?, config: NumberConfiguration? = null) : this(
        context,
        value?.toDouble(),
        config
    )

    private val config: NumberDecoratorConfig = NumberDecoratorConfig()

    internal var updateValueCallback: (newValue: Double?) -> Unit = {
        updateValue(it)
    }

    override var formattedValue: Spannable = SpannableString(StringUtils.EMPTY)
        internal set

    init {
        if (config != null) config(this.config)
        configureForce()
    }

    override fun configure(newConfig: NumberConfiguration): Boolean {
        val oldConfig = config.copy()
        newConfig(config)

        val changeFormatting = config.changedFormatting(oldConfig)
        val changeAbbreviation = changeFormatting || config.changedAbbreviation(oldConfig)
        val changeStyle = changeFormatting || changeAbbreviation || config.changedStyle(oldConfig)

        if (changeFormatting) changeFormatting()
        if (changeAbbreviation) {
            if (config.abbreviationType == AbbreviationType.NONE) changeFormatting()
            changeAbbreviation()
        }
        if (changeStyle) changeStyle(context)

        return changeStyle || changeFormatting || changeAbbreviation
    }

    override fun changeValue(newValue: String?) = updateValueCallback(newValue?.toDoubleOrNull())

    override fun changeValue(newValue: Double?) = updateValueCallback(newValue)

    override fun changeValue(newValue: Int?) = updateValueCallback(newValue?.toDouble())

    internal fun updateValue(newValue: Double?) {
        if (value == newValue) return
        value = newValue
        configureForce()
    }

    /** Обновить конфигурацию без проверки изменений. */
    internal fun configureForce() {
        changeFormatting()
        changeAbbreviation()
        changeStyle(context)
    }

    private fun changeFormatting() {
        formattedValue = value?.let { value ->
            SpannableStringBuilder.valueOf(
                FormatSettings.getDefault(
                    withFractionalPart = config.precision > 0u,
                    fractionalSize = config.precision.toInt(),
                    roundingMode = config.roundMode,
                    useGrouping = config.useGrouping,
                    showEmptyDecimals = config.showEmptyDecimals
                ).decimalFormat.format(value.toBigDecimal())
            )
        } ?: SpannableString(StringUtils.EMPTY)
    }

    private fun changeStyle(context: Context) {
        if (formattedValue.isEmpty()) return
        formattedValue.clearSpans()
        formattedValue.setSpan(AbsoluteSizeSpan(config.fontSize.getIntegerPartSizePx(context)))
        if (config.isFontStrikethrough) {
            formattedValue.setSpan(ForegroundColorSpan(FontColorStyle.Defaults.READ_ONLY.getColor(context)))
            formattedValue.setSpan(NumberDecoratorStrikethroughSpan(BorderThickness.S.getDimenPx(context).toFloat()))
        } else {
            formattedValue.setSpan(ForegroundColorSpan(config.fontColorStyle.integerPartColor.getColor(context)))
        }
        formattedValue.setSpan(
            StyleSpan(config.fontWeight.style),
            end = formattedValue.takeIfExistDecimal() ?: formattedValue.length
        )
        val isNegative = formattedValue.indexOf('-') == 0
        val insertPosition = 1
        if (isNegative && formattedValue.length > 1) {
            formattedValue.setSpan(
                CharSequenceSpan(
                    StringUtils.SPACE,
                    if (config.isFontStrikethrough) {
                        FontColorStyle.Defaults.READ_ONLY.getColor(context)
                    } else {
                        config.fontColorStyle.integerPartColor.getColor(context)
                    }
                ),
                insertPosition,
                insertPosition + 1,
                Spannable.SPAN_POINT_MARK
            )
        }
    }

    private fun changeAbbreviation() {
        if (config.abbreviationType == AbbreviationType.NONE) return
        value?.let {
            val abbreviationValue = abbreviationFormat(
                value = it,
                precision = 1,
                roundMode = config.roundMode,
                useEmptyDecimals = config.showEmptyDecimals,
                abbreviationType = config.abbreviationType
            )
            formattedValue = SpannableString(abbreviationValue)
        }
    }
}