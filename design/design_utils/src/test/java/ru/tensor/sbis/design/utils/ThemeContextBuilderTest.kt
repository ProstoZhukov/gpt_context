package ru.tensor.sbis.design.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import org.mockito.kotlin.*
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ThemeContextBuilderTest {

    @StyleRes
    private val themeStyleRes = 1
    @StyleRes
    private val contextStyleRes = 2
    @StyleRes
    private val defStyleRes = 3
    @AttrRes
    private val defStyleAttr = 4
    @StyleRes
    private val fallbackRes = 5

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var attrs: AttributeSet

    @Mock
    private lateinit var contextFactory: (Context, Int) -> Context

    private lateinit var builder: ThemeContextBuilder

    @Test
    fun `When theme defined in xml, then it should be returned`() {
        val attrArray: TypedArray = mock { on { getResourceId(0, ID_NULL) } doReturn themeStyleRes }
        whenever(context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.theme))).thenReturn(attrArray)
        builder = ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes)
        builder.themeContextFactory = contextFactory

        builder.build()

        verify(contextFactory).invoke(context, themeStyleRes)
        verify(attrArray).recycle()
    }

    @Test
    fun `When theme does not defined in xml, but attribute contains in context's theme, then context's attribute should be returned`() {
        val theme: Resources.Theme = mock()
        whenever(theme.resolveAttribute(eq(defStyleAttr), any(), any())).thenAnswer {
            it.getArgument(1, TypedValue::class.java).data = contextStyleRes
            true
        }
        whenever(context.theme).thenReturn(theme)
        builder = ThemeContextBuilder(context, null, defStyleAttr, defStyleRes)
        builder.themeContextFactory = contextFactory

        builder.build()

        verify(contextFactory).invoke(context, contextStyleRes)
    }

    @Test
    fun `When nor xml theme not context one present, then attribute resolved by fallback resolver should be returned`() {
        val theme: Resources.Theme = mock()
        val fallbackResolver = mock<() -> Int?> {
            on { invoke() } doReturn fallbackRes
        }
        whenever(theme.resolveAttribute(eq(defStyleAttr), any(), any())).thenReturn(false)
        whenever(context.theme).thenReturn(theme)
        builder = ThemeContextBuilder(context, null, defStyleAttr, defStyleRes, fallbackResolver)
        builder.themeContextFactory = contextFactory

        builder.build()

        verify(contextFactory).invoke(context, fallbackRes)
    }

    @Test
    fun `When nor xml theme not context one present, and fallback resolver is useless, then default style should be returned`() {
        val theme: Resources.Theme = mock()
        val fallbackResolver = mock<() -> Int?>()
        whenever(theme.resolveAttribute(eq(defStyleAttr), any(), any())).thenReturn(false)
        whenever(context.theme).thenReturn(theme)
        builder = ThemeContextBuilder(context, null, defStyleAttr, defStyleRes, fallbackResolver)
        builder.themeContextFactory = contextFactory

        builder.build()

        verify(contextFactory).invoke(context, defStyleRes)
    }
}