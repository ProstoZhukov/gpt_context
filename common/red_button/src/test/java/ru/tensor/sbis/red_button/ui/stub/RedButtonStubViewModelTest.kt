package ru.tensor.sbis.red_button.ui.stub

import androidx.annotation.StringRes
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.interactor.RedButtonPreferencesInteractor
import ru.tensor.sbis.common.R as RCommon

/**
 * @author ra.stepanov
 */
@RunWith(RobolectricTestRunner::class)
class RedButtonStubViewModelTest {

    private lateinit var viewModel: RedButtonStubViewModel
    private val preferencesInteractor = mock<RedButtonPreferencesInteractor>()

    @Before
    fun setup() {
        viewModel = RedButtonStubViewModel(preferencesInteractor)
        Mockito.clearInvocations(preferencesInteractor)
    }

    @Test
    fun `Given stub type CLOSE_STUB, then title has red_button_stub_title_close value`() {
        viewModel.refreshStubContent(RedButtonStubType.CLOSE_STUB)
        //act
        val content = getStubContentWithTitle(R.string.red_button_stub_title_close)
        //verify
        assertEquals(content, viewModel.stubContent.value)

    }

    @Test
    fun `Given stub type OPEN_STUB, then title has red_button_stub_title_open value`() {
        viewModel.refreshStubContent(RedButtonStubType.OPEN_STUB)
        //act
        val content = getStubContentWithTitle(R.string.red_button_stub_title_open)
        //verify
        assertEquals(content, viewModel.stubContent.value)
    }

    @Test
    fun `Given stub type NO_STUB, then title has common_unknown_error value`() {
        viewModel.refreshStubContent(RedButtonStubType.NO_STUB)
        //act
        val content = getStubContentWithTitle(RCommon.string.common_unknown_error)
        //verify
        assertEquals(content, viewModel.stubContent.value)
    }

    private fun getStubContentWithTitle(@StringRes titleId: Int) = ImageStubContent(
        imageType = StubViewCase.SBIS_ERROR.imageType,
        messageRes = titleId,
        detailsRes = R.string.red_button_stub_subtitle
    )
}