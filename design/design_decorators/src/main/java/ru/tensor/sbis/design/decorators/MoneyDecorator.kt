package ru.tensor.sbis.design.decorators

import android.content.Context
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import ru.tensor.sbis.design.decorators.money.MoneyConfiguration
import ru.tensor.sbis.design.decorators.money.MoneyDecoratorApi
import ru.tensor.sbis.design.decorators.money.MoneyDecoratorConfig
import ru.tensor.sbis.design.decorators.money.MoneyDecoratorConfigureApi
import ru.tensor.sbis.design.decorators.money.MoneyDecoratorSpan
import ru.tensor.sbis.design.decorators.number.AbbreviationType
import ru.tensor.sbis.design.decorators.number.NumberConfiguration
import ru.tensor.sbis.design.decorators.number.NumberDecoratorApi
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontSize
import ru.tensor.sbis.design.decorators.utils.setSpan
import ru.tensor.sbis.design.decorators.utils.takeIfExistDecimal
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.extentions.clearSpans
import java.math.BigDecimal

/**
 * Денежный декоратор.
 *
 * [Стандарт](https://www.figma.com/proto/9gNeGciO6aEWjk1vPs0ru1/%D0%A7%D0%B8%D1%81%D0%BB%D0%BE%D0%B2%D1%8B%D0%B5-%D0%B8-%D0%B4%D0%B5%D0%BD%D0%B5%D0%B6%D0%BD%D1%8B%D0%B5-%D0%B4%D0%B5%D0%BA%D0%BE%D1%80%D0%B0%D1%82%D0%BE%D1%80%D1%8B?node-id=1554-146148&scaling=min-zoom&page-id=47%3A431&starting-point-node-id=1554%3A146148)
 *
 * @author ps.smirnyh
 */
class MoneyDecorator internal constructor(
    private val context: Context,
    config: MoneyConfiguration? = null,
    private val numberDecorator: NumberDecorator
) : NumberDecoratorApi by numberDecorator, MoneyDecoratorApi, MoneyDecoratorConfigureApi {

    constructor(context: Context, value: Double? = null, config: MoneyConfiguration? = null) :
        this(context, config, NumberDecorator(context, value))

    constructor(context: Context, value: String?, config: MoneyConfiguration? = null) :
        this(context, config, NumberDecorator(context, value))

    constructor(context: Context, value: Int?, config: MoneyConfiguration? = null) :
        this(context, config, NumberDecorator(context, value))

    constructor(context: Context, value: BigDecimal?, config: MoneyConfiguration? = null) :
        this(context, config, NumberDecorator(context, value?.toDouble()))

    private val config: MoneyDecoratorConfig = MoneyDecoratorConfig()

    private val newNumberConfig: NumberConfiguration
        get() = {
            fontSize = config.fontSize
            fontColorStyle = config.fontColorStyle
            fontWeight = config.fontWeight
            isFontStrikethrough = config.isFontStrikethrough
            precision = config.precision
            roundMode = config.roundMode
            useGrouping = config.useGrouping
            showEmptyDecimals = config.showEmptyDecimals
            abbreviationType = config.abbreviationType
        }

    init {
        numberDecorator.updateValueCallback = {
            numberDecorator.updateValue(it)
            changeFractionStyle()
            changeCurrency()
        }
        if (config != null) {
            config(this.config)
            numberDecorator.configure(newNumberConfig)
        }
        configureForce()
    }

    override fun configure(newConfig: MoneyConfiguration): Boolean {
        val oldConfig = config.copy()
        newConfig(config)
        val changeNumberConfig = numberDecorator.configure(newNumberConfig)
        val changeCurrency = changeNumberConfig || config.changedCurrency(oldConfig)

        if (changeNumberConfig) changeFractionStyle()
        if (changeCurrency) changeCurrency()

        return changeNumberConfig || changeCurrency
    }

    override fun changeValue(newValue: BigDecimal?) = numberDecorator.updateValueCallback(newValue?.toDouble())

    private fun configureForce() {
        numberDecorator.configureForce()
        changeFractionStyle()
        changeCurrency()
    }

    private fun changeCurrency() {
        if (formattedValue.isEmpty()) return
        formattedValue.clearSpans<MoneyDecoratorSpan>()
        config.currency ?: return
        val isLeftPosition = config.currencyPosition == HorizontalPosition.LEFT

        formattedValue.setSpan(
            MoneyDecoratorSpan(
                if (isLeftPosition) "${config.currency?.char} " else " ${config.currency?.char}",
                getCurrencyColor(),
                getCurrencySize(),
                config.currencyPosition
            ),
            if (isLeftPosition) 0 else formattedValue.length - 1,
            if (isLeftPosition) 1 else formattedValue.length,
            Spanned.SPAN_POINT_MARK
        )
    }

    private fun getCurrencyColor(): Int =
        if (config.isFontStrikethrough) {
            0
        } else {
            config.currencyStyle?.getColor(context)
                ?: config.fontColorStyle.integerPartColor.getColor(context)
        }

    private fun getCurrencySize(): Int =
        config.currencySize?.getScaleOnDimenPx(context)
            ?: config.fontSize.getIntegerPartSizePx(context)

    private fun changeFractionStyle() {
        if (config.abbreviationType != AbbreviationType.NONE) return
        formattedValue.takeIfExistDecimal()?.let {
            val isZeroFraction = formattedValue.substring(it + 1..formattedValue.lastIndex).toInt() == 0
            val isDefaultStyle = config.fontColorStyle.integerPartColor == FontColorStyle.Defaults.DEFAULT

            val color = when {
                config.isFontStrikethrough -> FontColorStyle.Defaults.READ_ONLY.getColor(context)
                isZeroFraction -> getZeroFractionColor()
                isDefaultStyle -> getDefaultStyleFractionColor()
                else -> getFractionColor()
            }

            formattedValue.apply {
                setSpan(ForegroundColorSpan(color), it)
                setSpan(AbsoluteSizeSpan(config.fontSize.getFractionPartSizePx(context)), it)
            }
        }
    }

    private fun getZeroFractionColor() = config.numberConfig.fontColorStyle.fractionPartColor?.getColor(context)
        ?: FontColorStyle.Defaults.READ_ONLY.getColor(context)

    private fun getDefaultStyleFractionColor() = if (config.numberConfig.fontSize.getIntegerPartSizePx(context) <
        NumberDecoratorFontSize.Defaults.X5L.integerPartSize.getScaleOnDimenPx(context)
    ) {
        config.numberConfig.fontColorStyle.fractionPartColor?.getColor(context)
            ?: TextColor.LABEL_CONTRAST.getValue(context)
    } else {
        config.numberConfig.fontColorStyle.fractionPartColor?.getColor(context)
            ?: FontColorStyle.Defaults.DEFAULT.getColor(context)
    }

    private fun getFractionColor() = config.fontColorStyle.fractionPartColor?.getColor(context)
        ?: config.fontColorStyle.integerPartColor.getColor(context)
}