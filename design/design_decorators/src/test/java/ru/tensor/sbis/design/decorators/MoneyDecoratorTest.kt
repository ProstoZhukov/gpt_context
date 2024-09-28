package ru.tensor.sbis.design.decorators

import android.content.Context
import android.os.Build.VERSION_CODES
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.core.text.getSpans
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.decorators.money.MoneyDecoratorCurrency
import ru.tensor.sbis.design.decorators.money.MoneyDecoratorSpan
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontSize
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.R as RDesign

/**
 * Тестовый класс для [MoneyDecorator].
 *
 * @author ps.smirnyh
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [VERSION_CODES.P])
class MoneyDecoratorTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var moneyDecorator: MoneyDecorator

    @Before
    fun setUp() {
        context.theme.applyStyle(RDesign.style.AppGlobalTheme, true)
        moneyDecorator = MoneyDecorator(context, 10)
    }

    @Test
    fun `When value has fraction part then fraction part smaller size`() {
        moneyDecorator.changeValue(25.25)
        assert(
            moneyDecorator.formattedValue.getSpans<AbsoluteSizeSpan>(0, moneyDecorator.formattedValue.indexOf('.'))
                .lastOrNull()?.size == FontSize.M.getScaleOnDimenPx(context)
        )
        assert(
            moneyDecorator.formattedValue.getSpans<AbsoluteSizeSpan>(
                moneyDecorator.formattedValue.indexOf('.'),
                moneyDecorator.formattedValue.length
            ).lastOrNull()?.size == FontSize.XS.getScaleOnDimenPx(context)
        )
    }

    @Test
    fun `When fraction part is zero then fraction part has read only color`() {
        moneyDecorator.configure {
            precision = 2u
            showEmptyDecimals = true
        }
        assert(
            moneyDecorator.formattedValue.getSpans<ForegroundColorSpan>(
                moneyDecorator.formattedValue.indexOf('.'),
                moneyDecorator.formattedValue.length
            ).lastOrNull()?.foregroundColor == FontColorStyle.Defaults.READ_ONLY.getColor(context)
        )
    }

    @Test
    fun `When integer part of default style and size is less than 5XL then fraction part has label contrast color`() {
        moneyDecorator.configure {
            precision = 2u
            showEmptyDecimals = true
        }
        moneyDecorator.changeValue(25.25)
        assert(
            moneyDecorator.formattedValue.getSpans<ForegroundColorSpan>(
                moneyDecorator.formattedValue.indexOf('.'),
                moneyDecorator.formattedValue.length
            ).lastOrNull()?.foregroundColor == TextColor.LABEL_CONTRAST.getValue(context)
        )
    }

    @Test
    fun `When integer part of default style and size is more than 4XL then fraction part has text color`() {
        moneyDecorator.configure {
            precision = 2u
            showEmptyDecimals = true
            fontSize = NumberDecoratorFontSize.Defaults.X6L
        }
        moneyDecorator.changeValue(25.25)
        assert(
            moneyDecorator.formattedValue.getSpans<ForegroundColorSpan>(
                moneyDecorator.formattedValue.indexOf('.'),
                moneyDecorator.formattedValue.length
            ).lastOrNull()?.foregroundColor == FontColorStyle.Defaults.DEFAULT.getColor(context)
        )
    }

    @Test
    fun `When currency is dollar then formattedValue has span for dollar char`() {
        moneyDecorator.configure {
            currency = MoneyDecoratorCurrency.Dollar
        }
        assert(
            moneyDecorator.formattedValue.getSpans<MoneyDecoratorSpan>(0, 1)
                .any { it.charters.getOrNull(0) == MoneyDecoratorCurrency.Dollar.char }
        )
    }

    @Test
    fun `When currency position is right then formattedValue has span for currency in end`() {
        moneyDecorator.configure {
            currency = MoneyDecoratorCurrency.Dollar
            currencyPosition = HorizontalPosition.RIGHT
        }
        assert(
            moneyDecorator.formattedValue.getSpans<MoneyDecoratorSpan>(
                moneyDecorator.formattedValue.length - 1,
                moneyDecorator.formattedValue.length
            ).any { it.charters.getOrNull(1) == MoneyDecoratorCurrency.Dollar.char }
        )
    }

    @Test
    fun `When currency size is XL then formattedValue has size span for currency`() {
        moneyDecorator.configure {
            currency = MoneyDecoratorCurrency.Dollar
            currencySize = FontSize.XL
        }
        assert(
            moneyDecorator.formattedValue.getSpans<MoneyDecoratorSpan>(0, 1)
                .any { it.currencySize == FontSize.XL.getScaleOnDimenPx(context) }
        )
    }

    @Test
    fun `When currency style is secondary then formattedValue has color span for currency`() {
        moneyDecorator.configure {
            currency = MoneyDecoratorCurrency.Dollar
            currencyStyle = FontColorStyle.Defaults.SECONDARY
        }
        assert(
            moneyDecorator.formattedValue.getSpans<MoneyDecoratorSpan>(0, 1)
                .any { it.currencyColor == FontColorStyle.Defaults.SECONDARY.getColor(context) }
        )
    }
}