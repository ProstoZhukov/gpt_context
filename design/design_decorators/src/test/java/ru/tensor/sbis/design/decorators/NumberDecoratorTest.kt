package ru.tensor.sbis.design.decorators

import android.content.Context
import android.graphics.Typeface
import android.os.Build.VERSION_CODES
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.text.getSpans
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.decorators.number.AbbreviationType
import ru.tensor.sbis.design.decorators.number.NumberConfiguration
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontColorStyle
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontSize
import ru.tensor.sbis.design.decorators.number.NumberDecoratorStrikethroughSpan
import ru.tensor.sbis.design.decorators.number.RoundMode
import ru.tensor.sbis.design.R as RDesign

/**
 * Тестовый класс для [NumberDecorator].
 *
 * @author ps.smirnyh
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [VERSION_CODES.P])
class NumberDecoratorTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var numberDecorator: NumberDecorator

    @Before
    fun setUp() {
        context.theme.applyStyle(RDesign.style.AppGlobalTheme, true)
        numberDecorator = NumberDecorator(context, 10)
    }

    @Test
    fun `When call value change with new value then formattedValue is new value`() {
        numberDecorator.changeValue(20)
        assertEquals("20", numberDecorator.formattedValue.toString())
    }

    @Test
    fun `When change the text size then formattedValue is new text size`() {
        numberDecorator.configure {
            fontSize = NumberDecoratorFontSize.Defaults.L
        }
        assert(
            numberDecorator.formattedValue.getSpans<AbsoluteSizeSpan>()
                .any { it.size == NumberDecoratorFontSize.Defaults.L.getIntegerPartSizePx(context) }
        )
    }

    @Test
    fun `When change the text style then formattedValue is new text style`() {
        numberDecorator.configure {
            fontColorStyle = NumberDecoratorFontColorStyle(FontColorStyle.Defaults.WARNING)
        }
        assert(
            numberDecorator.formattedValue.getSpans<ForegroundColorSpan>()
                .any { it.foregroundColor == FontColorStyle.Defaults.WARNING.getColor(context) }
        )
    }

    @Test
    fun `When change the text weight then formattedValue is new text weight`() {
        numberDecorator.configure {
            fontWeight = FontWeight.BOLD
            isFontStrikethrough = true
        }
        assert(numberDecorator.formattedValue.getSpans<StyleSpan>().any { it.style == Typeface.BOLD })
        assert(numberDecorator.formattedValue.getSpans<NumberDecoratorStrikethroughSpan>().isNotEmpty())
    }

    @Test
    fun `When change the text strikethrough then formattedValue is new text strikethrough`() {
        numberDecorator.configure {
            isFontStrikethrough = true
        }
        assert(numberDecorator.formattedValue.getSpans<NumberDecoratorStrikethroughSpan>().isNotEmpty())
    }

    @Test
    fun `When change the precision then formattedValue is new precision`() {
        numberDecorator.changeValue(20.255)
        numberDecorator.configure {
            precision = 2u
        }
        assertEquals("20.25", numberDecorator.formattedValue.toString())
    }

    @Test
    fun `When change the round mode then formattedValue is rounded value`() {
        numberDecorator.changeValue(20.25)
        numberDecorator.configure {
            precision = 1u
            roundMode = RoundMode.ROUND
        }
        assertEquals("20.3", numberDecorator.formattedValue.toString())
    }

    @Test
    fun `When change the use grouping then formattedValue is grouped`() {
        numberDecorator.changeValue(2000)
        numberDecorator.configure {
            useGrouping = true
        }
        assert(numberDecorator.formattedValue.toString()[1].isWhitespace())
        assert(numberDecorator.formattedValue.toString().count { it.isWhitespace() } == 1)
    }

    @Test
    fun `When showEmptyDecimals is true then the missing zeros are added to the fractional part`() {
        numberDecorator.changeValue(20.5)
        numberDecorator.configure {
            precision = 3u
            showEmptyDecimals = true
        }
        assertEquals("20.500", numberDecorator.formattedValue.toString())
    }

    @Test
    fun `When abbreviationType is short then formattedValue has short abbreviation`() {
        numberDecorator.changeValue(20000)
        numberDecorator.configure {
            abbreviationType = AbbreviationType.SHORT
        }
        assertEquals("20к", numberDecorator.formattedValue.toString())
    }

    @Test
    fun `When abbreviationType is long then formattedValue has long abbreviation`() {
        numberDecorator.changeValue(20000)
        numberDecorator.configure {
            abbreviationType = AbbreviationType.LONG
        }
        assertEquals("20 тыс", numberDecorator.formattedValue.toString())
    }

    @Test
    fun `When configure without changes then not configured again`() {
        val config: NumberConfiguration = {
            abbreviationType = AbbreviationType.LONG
            useGrouping = true
            precision = 10u
        }
        numberDecorator.configure(config)
        assertFalse(numberDecorator.configure(config))
    }
}