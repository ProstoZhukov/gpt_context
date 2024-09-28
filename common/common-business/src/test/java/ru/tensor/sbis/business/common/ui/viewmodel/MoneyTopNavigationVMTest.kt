package ru.tensor.sbis.business.common.ui.viewmodel

import android.os.Build
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.business.common.ui.base.router.BaseRouter

// TODO("https://online.sbis.ru/opendoc.html?guid=05303f02-cfc3-46c4-9258-f61e0c3dc0ac&client=3")
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class MoneyTopNavigationVMTest {

    private lateinit var vm: MoneyTopNavigationVM

    private val mockRouter: BaseRouter = mock()

    @Before
    fun setUp() {
        buildVm()
    }

    @After
    fun tearDown() {
        clearInvocations(mockRouter)
    }

    @Test
    fun `On build title use regular title size, semi-bold font, title color`() {
//        vm.buildTitle()
//        assertTrue(vm.title.get() is Spanned)
//        with(vm.title.get() as Spanned) {
//            val sizeSpans = getSpans(0, length, AbsoluteSizeSpan::class.java)
//            assertEquals(TITLE_SIZE, sizeSpans[0].size)
//
//            val fontSpans = getSpans(0, length, CustomTypefaceSpan::class.java)
//            assertTrue(fontSpans.isNotEmpty())
//
//            val colorSpans = getSpans(0, length, ForegroundColorSpan::class.java)
//            assertEquals(TITLE_COLOR, colorSpans[0].foregroundColor)
//        }
//        assertNotNull(vm.titleAction.get())
//        vm.titleAction.get()!!.invoke()
//        verify(mockRouter).goBack()
    }

    @Test
    fun `On build title without title action do not assign title action`() {
//        vm.buildTitle(setTitleActionGoBack = false)
//        assertNull(vm.titleAction.get())
    }

    @Test
    fun `On build title with badge set title with colored badge`() {
//        vm.buildTitle(badge = "99")
//
//        assertEquals("$TEST_TITLE 99", vm.title.get().toString())
//        assertTrue(vm.title.get() is Spanned)
//        with(vm.title.get() as Spanned) {
//            val badgeStart = TEST_TITLE.length + 1
//
//            val sizeSpans = getSpans(badgeStart, length, AbsoluteSizeSpan::class.java)
//            assertEquals(TITLE_SIZE, sizeSpans[0].size)
//
//            val colorSpans = getSpans(badgeStart, length, ForegroundColorSpan::class.java)
//            assertEquals(BADGE_COLOR, colorSpans[0].foregroundColor)
//        }
//        assertNotNull(vm.titleAction.get())
    }

    @Test
    fun `On build title with minified title use minified title size`() {
//        vm.isUsingMinifiedTitle = true
//        vm.buildTitle()
//        assertTrue(vm.title.get() is Spanned)
//        with(vm.title.get() as Spanned) {
//            val sizeSpans = getSpans(0, length, AbsoluteSizeSpan::class.java)
//            assertEquals(MINIFIED_TITLE_SIZE, sizeSpans[0].size)
//        }
//        assertNotNull(vm.titleAction.get())
//        vm.titleAction.get()!!.invoke()
//        verify(mockRouter).goBack()
    }

    @Test
    fun `Verify spans on build right title with units`() {
        val rightTitle = "Согласовано"
        val units = "99"
//        vm.buildRightTitle(rightTitle, units)
//        assertEquals("$rightTitle $units", vm.rightTitle.get().toString())
//
//        assertTrue(vm.rightTitle.get() is Spanned)
//        with(vm.rightTitle.get() as Spanned) {
//            // Заголовок
//            val titleSizeSpans = getSpans(0, rightTitle.length, AbsoluteSizeSpan::class.java)
//            assertEquals(TITLE_SIZE, titleSizeSpans[0].size)
//
//            val titleFontSpans = getSpans(0, rightTitle.length, CustomTypefaceSpan::class.java)
//            assertTrue(titleFontSpans.isNotEmpty())
//
//            val titleColorSpans = getSpans(0, rightTitle.length, ForegroundColorSpan::class.java)
//            assertEquals(TITLE_COLOR, titleColorSpans[0].foregroundColor)
//
//            // Единицы измерения
//            val unitsSizeSpans = getSpans(rightTitle.length + 1, length, AbsoluteSizeSpan::class.java)
//            assertEquals(UNITS_SIZE, unitsSizeSpans[0].size)
//
//            val unitsColorSpans = getSpans(rightTitle.length + 1, length, ForegroundColorSpan::class.java)
//            assertEquals(UNITS_COLOR, unitsColorSpans[0].foregroundColor)
//        }
    }

    @Test
    fun `Verify spans on build subtitle`() {
//        vm.subtitle.set(TEST_SUBTITLE)
//        vm.buildSubtitle()
//
//        assertTrue(vm.subtitle.get() is Spanned)
//        with(vm.subtitle.get() as Spanned) {
//            val sizeSpans = getSpans(0, length, AbsoluteSizeSpan::class.java)
//            assertEquals(SUBTITLE_TITLE_SIZE, sizeSpans[0].size)
//
//            val colorSpans = getSpans(0, length, ForegroundColorSpan::class.java)
//            assertEquals(SUBTITLE_COLOR, colorSpans[0].foregroundColor)
//        }
    }

    @Test
    fun `Verify title is minified when has subtitle`() {
//        vm.subtitle.set(TEST_SUBTITLE)
//        vm.buildTitle()
//
//        with(vm.title.get() as Spanned) {
//            val sizeSpans = getSpans(0, length, AbsoluteSizeSpan::class.java)
//            assertEquals(MINIFIED_TITLE_SIZE, sizeSpans[0].size)
//        }
    }

    private fun buildVm() {
        vm = spy(object : MoneyTopNavigationVM(mockRouter) {})
//        vm.title.set(TEST_TITLE)
    }
}

private const val TEST_TITLE = "Деньги"
private const val TEST_SUBTITLE = "Без выписки"