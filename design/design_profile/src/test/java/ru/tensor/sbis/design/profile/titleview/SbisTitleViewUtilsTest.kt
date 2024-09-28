package ru.tensor.sbis.design.profile.titleview

import androidx.annotation.DimenRes
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.titleview.Default
import ru.tensor.sbis.design.profile_decl.titleview.ListContent
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewContent
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewItem
import ru.tensor.sbis.design.profile.titleview.utils.chooseSmallerTitleTextSize
import ru.tensor.sbis.design.profile.titleview.utils.chooseTitleMaxLines
import ru.tensor.sbis.design.profile.titleview.utils.chooseTitleTextSize
import ru.tensor.sbis.design.profile.titleview.utils.isNeedHideImage

/**
 * @author ns.staricyn
 */
@Suppress("unused")
@RunWith(JUnitParamsRunner::class)
class SbisTitleViewUtilsTest {

    @Test
    @Parameters(method = "parametersForValidationTitleTextSize")
    fun `Verify title text size`(content: TitleViewContent, @DimenRes expected: Int) {
        val actual = chooseTitleTextSize(content)

        assertEquals(expected, actual)
    }

    @Test
    @Parameters(method = "parametersForValidationSmallerTitleTextSize")
    fun `Verify smaller title text size`(content: TitleViewContent, @DimenRes expected: Int) {
        val actual = chooseSmallerTitleTextSize(content)

        assertEquals(expected, actual)
    }

    @Test
    @Parameters(method = "parametersForValidationTitleMaxLines")
    fun `Verify title max lines`(singleLine: Boolean, content: TitleViewContent, expected: Int) {
        val actual = chooseTitleMaxLines(singleLine, content)

        assertEquals(expected, actual)
    }

    @Test
    @Parameters(method = "parametersForValidationImageVisible")
    fun `Verify image visible`(content: TitleViewContent, expected: Boolean) {
        val actual = isNeedHideImage(content).not()

        assertEquals(expected, actual)
    }

    private fun parametersForValidationTitleTextSize() = params {
        add(
            Default("title", subtitle = "subtitle"),
            R.dimen.design_profile_sbis_title_view_title_text_size_medium
        )
        add(Default("title"), R.dimen.design_profile_sbis_title_view_title_text_size_large)
    }

    private fun parametersForValidationSmallerTitleTextSize() = params {
        add(
            Default("title", subtitle = "subtitle"),
            R.dimen.design_profile_sbis_title_view_title_text_size_medium
        )
        add(Default("title"), R.dimen.design_profile_sbis_title_view_title_text_size_small)
    }

    private fun parametersForValidationTitleMaxLines() = params {
        add(true, Default("title", subtitle = "subtitle"), 1)
        add(false, Default("title", subtitle = "subtitle"), 1)
        add(true, Default("title"), 1)
        add(false, Default("title"), 2)
    }

    private fun parametersForValidationImageVisible() = params {
        add(Default("title", imageUrl = ""), false)
        add(Default("title"), false)
        add(Default("title", imageUrl = "url"), true)
        add(ListContent(), true)
        add(ListContent(title = "title", subtitle = "subtitle"), true)
        add(
            ListContent(listOf(TitleViewItem(PersonData(photoUrl = "url"), "item title", "item subtitle"))),
            true
        )
    }
}