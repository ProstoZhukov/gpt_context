package ru.tensor.sbis.message_panel.helper

import android.text.TextUtils
import android.view.Gravity
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.ID_NULL
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHintConfig.Companion.DEFAULT_HINT

/**
 * @author vv.chekurda
 * @since 7/23/2019
 */
@RunWith(JUnitParamsRunner::class)
class EditTextExtensionsTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var editText: EditText

    @Test
    fun `Test EditText new dialog mode marked as enabled`() {
        editText.markNewMessageMode(true)

        verify(editText).tag = NEW_MESSAGE_MODE_MARKER
    }

    @Test
    fun `Test EditText new dialog mode marked as disabled`() {
        editText.markNewMessageMode(false)

        verify(editText).tag = null
    }

    @Test
    fun `Test MessagePanelTextParams with max height applied to EditText as expected`() {
        val maxHeightParams = MaxHeight(100)
        val params = MessagePanelTextParams(
            minLines = 2,
            maxHeightParams = maxHeightParams,
            ellipsizeEnd = true,
            gravity = Gravity.TOP,
            hint = DEFAULT_HINT
        )
        editText.applyTextParams(params)

        verify(editText).minLines = params.minLines
        verify(editText).maxHeight = maxHeightParams.height
        verify(editText).ellipsize = TextUtils.TruncateAt.END
        verify(editText).gravity = params.gravity
        verify(editText).setHint(DEFAULT_HINT)
    }

    @Test
    fun `Test MessagePanelTextParams with max lines applied to EditText as expected`() {
        val maxHeightParams = MaxLines(5)
        val params = MessagePanelTextParams(
            minLines = 1,
            maxHeightParams = maxHeightParams,
            ellipsizeEnd = false,
            gravity = Gravity.CENTER_VERTICAL,
            hint = ID_NULL
        )
        editText.applyTextParams(params)

        verify(editText).minLines = params.minLines
        verify(editText).maxLines = maxHeightParams.lines
        verify(editText).ellipsize = null
        verify(editText).gravity = params.gravity
        verify(editText).hint = null
    }
}