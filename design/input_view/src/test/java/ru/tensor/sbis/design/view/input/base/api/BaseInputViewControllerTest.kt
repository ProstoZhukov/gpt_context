package ru.tensor.sbis.design.view.input.base.api

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.TextKeyListener
import android.view.View
import org.apache.commons.lang3.StringUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyString
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.MaskEditText
import ru.tensor.sbis.design.view.input.base.ValidationStatusAdapter
import ru.tensor.sbis.design.view.input.base.utils.factory.CircularProgressFactory
import ru.tensor.sbis.design.view.input.base.utils.factory.TextLayoutFactory
import ru.tensor.sbis.design.view.input.base.utils.factory.ValidationStatusAdapterFactory
import ru.tensor.sbis.design.view.input.base.utils.style.BaseStyleHolder

/**
 * Класс для тестирования [BaseInputViewController].
 *
 * @author ps.smirnyh
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class BaseInputViewControllerTest {

    private companion object {
        const val TEST_TITLE = "Test title"
        const val TEST_PLACEHOLDER = "Test placeholder"
        const val TEST_VALUE = "Test value"
    }

    @Mock
    private lateinit var mockBaseInputView: BaseInputView

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockMaskEditText: MaskEditText

    private lateinit var baseInputViewController: BaseInputViewController

    private var textLayoutConfig = TextLayout.TextLayoutParams(paint = mock())

    private lateinit var textKeyListener: MockedStatic<TextKeyListener>

    private lateinit var typefaceManager: MockedStatic<TypefaceManager>

    @Before
    fun setUp() {
        textKeyListener = mockStatic {
            on<TextKeyListener> { TextKeyListener.getInstance() }.thenReturn(
                TextKeyListener(
                    TextKeyListener.Capitalize.NONE,
                    false
                )
            )
        }
        typefaceManager = mockStatic {
            on<Typeface> { TypefaceManager.getRobotoRegularFont(any()) }.thenReturn(mock())
        }
        val styleHolder: BaseStyleHolder = spy(BaseStyleHolder())
        val validationStatusAdapter: ValidationStatusAdapter = mock()
        val validationStatusAdapterFactory: ValidationStatusAdapterFactory = mock()
        val textLayoutFactory: TextLayoutFactory = mock()
        val circularProgressFactory: CircularProgressFactory = mock()
        val mockTextLayout: TextLayout = mock {
            on { configure(anyBoolean(), any()) } doAnswer {
                val oldConfig = textLayoutConfig
                it.getArgument<TextLayoutConfig>(1).invoke(textLayoutConfig)
                oldConfig != textLayoutConfig
            }
        }
        doReturn(mock<TextPaint>()).whenever(mockTextLayout).textPaint
        doReturn(mockTextLayout).whenever(textLayoutFactory).create(any())
        doReturn(validationStatusAdapter).whenever(validationStatusAdapterFactory)
            .create(styleHolder)
        doNothing().whenever(styleHolder).loadStyle(same(mockContext), eq(null), eq(0), eq(0))
        doReturn(mockMaskEditText).whenever(mockBaseInputView).inputView
        doReturn(arrayOf<InputFilter>()).whenever(mockMaskEditText).filters
        doReturn(mockContext).whenever(mockBaseInputView).context
        baseInputViewController = BaseInputViewController(
            styleHolder,
            validationStatusAdapterFactory,
            textLayoutFactory,
            circularProgressFactory
        )
        baseInputViewController.attach(mockBaseInputView, null, 0, 0)
    }

    @After
    fun after() {
        textKeyListener.closeOnDemand()
        typefaceManager.closeOnDemand()
    }

    @Test
    fun `When class is initialized then showPlaceholderAsTitle is true`() {
        assertTrue(baseInputViewController.showPlaceholderAsTitle)
    }

    @Test
    fun `When title value is set and isFocused is false and showPlaceholderAsTitle is true then title value is shown in placeholder`() {
        doReturn(false).whenever(mockMaskEditText).isFocused
        // act
        baseInputViewController.title = TEST_TITLE
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).hint = arg.capture()
        assertEquals(TEST_TITLE, arg.value)
        assertEquals(StringUtils.EMPTY, textLayoutConfig.text)
        assertTrue(textLayoutConfig.isVisible)
    }

    @Test
    fun `When title value is set and isFocused is true and showPlaceholderAsTitle is true then title value is shown in title`() {
        doReturn(true).whenever(mockMaskEditText).isFocused
        // act
        baseInputViewController.title = TEST_TITLE
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).hint = arg.capture()
        assertEquals(StringUtils.EMPTY, arg.value)
        assertEquals(TEST_TITLE, textLayoutConfig.text)
        assertTrue(textLayoutConfig.isVisible)
    }

    @Test
    fun `When placeholder value is set and isFocused is false and showPlaceholderAsTitle is true then placeholder value is shown in placeholder`() {
        doReturn(false).whenever(mockMaskEditText).isFocused
        // act
        baseInputViewController.placeholder = TEST_PLACEHOLDER
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).hint = arg.capture()
        assertEquals(TEST_PLACEHOLDER, arg.value)
        assertEquals(StringUtils.EMPTY, textLayoutConfig.text)
        assertTrue(textLayoutConfig.isVisible)
    }

    @Test
    fun `When placeholder value is set and isFocused is true and showPlaceholderAsTitle is true then placeholder value is shown in title`() {
        doReturn(true).whenever(mockMaskEditText).isFocused
        // act
        baseInputViewController.placeholder = TEST_PLACEHOLDER
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).hint = arg.capture()
        assertEquals(StringUtils.EMPTY, arg.value)
        assertEquals(TEST_PLACEHOLDER, textLayoutConfig.text)
        assertTrue(textLayoutConfig.isVisible)
    }

    @Test
    fun `When value is set and placeholder value is set and showPlaceholderAsTitle is true then placeholder value is shown in title`() {
        var textValue = ""
        val editable: Editable = object : SpannableStringBuilder() {
            override fun toString(): String {
                return textValue
            }
        }
        doAnswer {
            textValue = it.getArgument(0)
        }.whenever(mockMaskEditText).setText(anyString())
        doReturn(editable).whenever(mockMaskEditText).text
        // act
        baseInputViewController.placeholder = TEST_PLACEHOLDER
        baseInputViewController.value = TEST_VALUE
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).setText(arg.capture())
        assertEquals(TEST_VALUE, arg.value)
        assertEquals(TEST_PLACEHOLDER, textLayoutConfig.text)
        assertTrue(textLayoutConfig.isVisible)
    }

    @Test
    fun `When value is set and showPlaceholderAsTitle is false then title isn't shown`() {
        var textValue = ""
        val editable: Editable = object : SpannableStringBuilder() {
            override fun toString(): String {
                return textValue
            }
        }
        doAnswer {
            textValue = it.getArgument(0)
        }.whenever(mockMaskEditText).setText(anyString())
        doReturn(editable).whenever(mockMaskEditText).text
        baseInputViewController.showPlaceholderAsTitle = false
        // act
        baseInputViewController.placeholder = TEST_PLACEHOLDER
        baseInputViewController.value = TEST_VALUE
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).setText(arg.capture())
        assertEquals(TEST_VALUE, arg.value)
        assertFalse(textLayoutConfig.isVisible)
    }

    @Test
    fun `When placeholder is set and isFocused is true and showPlaceholderAsTitle is false then title isn't shown`() {
        val textValue = ""
        val editable: Editable = object : SpannableStringBuilder() {
            override fun toString(): String {
                return textValue
            }
        }
        doReturn(editable).whenever(mockMaskEditText).text
        baseInputViewController.showPlaceholderAsTitle = false
        doReturn(true).whenever(mockMaskEditText).isFocused
        // act
        baseInputViewController.placeholder = TEST_PLACEHOLDER
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).hint = arg.capture()
        assertEquals(TEST_PLACEHOLDER, arg.value)
        assertFalse(textLayoutConfig.isVisible)
    }

    @Test
    fun `When placeholder is set and title is set and showPlaceholderAsTitle is false then title and placeholder are shown`() {
        val textValue = ""
        val editable: Editable = object : SpannableStringBuilder() {
            override fun toString(): String {
                return textValue
            }
        }
        doReturn(editable).whenever(mockMaskEditText).text
        baseInputViewController.showPlaceholderAsTitle = false
        // act
        baseInputViewController.placeholder = TEST_PLACEHOLDER
        baseInputViewController.title = TEST_TITLE
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).hint = arg.capture()
        assertEquals(TEST_PLACEHOLDER, arg.value)
        assertEquals(TEST_TITLE, textLayoutConfig.text)
        assertTrue(textLayoutConfig.isVisible)
    }

    @Test
    fun `When click is performed on editText then passed click listener invokes`() {
        mockStatic<KeyboardUtils>()
        val clickListener: View.OnClickListener = mock()
        baseInputViewController.onFieldClickListener = clickListener
        // act
        baseInputViewController.clickListener.onClick(mockMaskEditText)
        // verify
        verify(clickListener).onClick(mockBaseInputView)
    }

    @Test
    fun `When focus changes on editText then passed focus listener invokes`() {
        val focusChangedListener: View.OnFocusChangeListener = mock()
        baseInputViewController.focusChangedListener.outer = focusChangedListener
        // act
        baseInputViewController.focusChangedListener.onFocusChange(mockMaskEditText, true)
        // verify
        verify(focusChangedListener).onFocusChange(mockBaseInputView, true)
    }

    @Test
    fun `When value is set then passed value changed listener invokes`() {
        val onValueChanged: (view: BaseInputView, value: String) -> Unit = mock()
        var textValue = ""
        val editable: Editable = object : SpannableStringBuilder() {
            override fun toString(): String {
                return textValue
            }
        }
        doAnswer {
            textValue = it.getArgument(0)
            baseInputViewController.valueChangedWatcher.afterTextChanged(editable)
        }.whenever(mockMaskEditText).setText(anyString())
        baseInputViewController.onValueChanged = onValueChanged
        // act
        baseInputViewController.value = TEST_VALUE
        // verify
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(mockMaskEditText, atLeastOnce()).setText(arg.capture())
        verify(onValueChanged).invoke(mockBaseInputView, TEST_VALUE)
        assertEquals(TEST_VALUE, arg.value)
    }

    @Test
    fun `When readOnly is true then editText enabled is false`() {
        var isEnabled = true
        doAnswer {
            isEnabled = it.getArgument(0)
        }.whenever(mockMaskEditText).isEnabled = anyBoolean()
        doReturn(isEnabled).whenever(mockMaskEditText).isEnabled
        // act
        baseInputViewController.readOnly = true
        // verify
        assertFalse(isEnabled)
    }

    @Test
    fun `When isRequiredField is true then star is added to the end of the title`() {
        doReturn(true).whenever(mockMaskEditText).isFocused
        // act
        baseInputViewController.title = TEST_TITLE
        baseInputViewController.isRequiredField = true
        // verify
        assertEquals("$TEST_TITLE*", textLayoutConfig.text)
    }
}