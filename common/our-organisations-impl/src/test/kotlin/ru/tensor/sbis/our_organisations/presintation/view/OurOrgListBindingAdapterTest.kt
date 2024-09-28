package ru.tensor.sbis.our_organisations.presintation.view

import android.util.TypedValue
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import io.mockk.junit5.MockKExtension
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.our_organisations.presentation.view.setColorOrganisationName
import ru.tensor.sbis.our_organisations.presentation.view.setSearchText
import ru.tensor.sbis.our_organisations.presentation.view.setVisibilityMark

@RunWith(JUnitParamsRunner::class)
@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class OurOrgListBindingAdapterTest {

    private enum class TestColor(val value: Int) {
        TextColorDEFAULT(1),
        TextColorREADONLY(2),
        TextBackgroundColorDecoratorHighlight(3),
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val textView = mock<SbisTextView> {
        on { context } doAnswer {
            mock {
                on { theme } doAnswer {
                    mock {
                        onGeneric {
                            resolveAttribute(
                                ArgumentMatchers.anyInt(),
                                Mockito.any(TypedValue::class.java),
                                Mockito.anyBoolean()
                            )
                        } doAnswer {
                            setColorFromAttr(it)
                            true
                        }
                    }
                }
            }
        }
    }

    private fun setColorFromAttr(invocationOnMock: InvocationOnMock) {
        val resolveAttribute = invocationOnMock.getArgument<Int>(0)
        invocationOnMock.getArgument(1, TypedValue::class.java).data = when (resolveAttribute) {
            R.attr.textColor -> TestColor.TextColorDEFAULT.value
            R.attr.readonlyTextColor -> TestColor.TextColorREADONLY.value
            R.attr.textBackgroundColorDecoratorHighlight -> TestColor.TextBackgroundColorDecoratorHighlight.value
            else -> -1
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        isAssertOnMainThreadEnabled = false
    }

    @After
    fun tearDown() {
        isAssertOnMainThreadEnabled = true
        Dispatchers.resetMain()
    }

    @Test
    fun `Set Visibility Mark - select, is multi choice`() =
        runTest {
            textView.setVisibilityMark(true, true)

            verify(textView).visibility = View.GONE
        }

    @Test
    fun `Set Visibility Mark - unselect, is multi choice`() =
        runTest {
            textView.setVisibilityMark(false, true)

            verify(textView).visibility = View.GONE
        }

    @Test
    fun `Set Visibility Mark - select, is not multi choice`() =
        runTest {
            textView.setVisibilityMark(true, false)

            verify(textView).visibility = View.VISIBLE
        }

    @Test
    fun `Set Visibility Mark - unselect, is not multi choice`() =
        runTest {
            textView.setVisibilityMark(false, false)

            verify(textView).visibility = View.INVISIBLE
        }

    @Test
    fun `Set Color Organisation Name - is Eliminated`() =
        runTest {
            textView.setColorOrganisationName(true)

            verify(textView).setTextColor(TestColor.TextColorREADONLY.value)
        }

    @Test
    fun `Set Color Organisation Name - is not Eliminated`() =
        runTest {
            textView.setColorOrganisationName(false)

            verify(textView).setTextColor(TestColor.TextColorDEFAULT.value)
        }

    @Test
    fun `Set search text - empty search text`() =
        runTest {
            textView.setSearchText("text", "")

            verify(textView).setTextWithHighlightRanges("text", null)
        }

    @Test
    fun `Set search text - first word`() =
        runTest {
            textView.setSearchText("search text", "search")

            verify(textView).setTextWithHighlightRanges(
                "search text",
                listOf(IntRange(0, 5))
            )
        }

    @Test
    fun `Set search text - last word`() =
        runTest {
            textView.setSearchText("search text", "text")

            verify(textView).setTextWithHighlightRanges(
                "search text",
                listOf(IntRange(7, 10))
            )
        }

    @Test
    fun `Set search text - in order words`() =
        runTest {
            textView.setSearchText("search text test view", "text view")

            verify(textView).setTextWithHighlightRanges(
                "search text test view",
                listOf(IntRange(7, 10), IntRange(17, 20))
            )
        }

    @Test
    fun `Set search text - backward words`() =
        runTest {
            textView.setSearchText("search text test view", "view text")

            verify(textView).setTextWithHighlightRanges(
                "search text test view",
                listOf(IntRange(7, 10), IntRange(17, 20))
            )
        }
}