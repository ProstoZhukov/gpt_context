package ru.tensor.sbis.design.folders.view.full.adapter.holders

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewAditionalCommandBinding
import ru.tensor.sbis.design.folders.test_utils.command
import ru.tensor.sbis.design.folders.view.full.adapter.FolderHolderResourceProvider
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class AdditionalCommandHolderTest {

    private companion object {
        const val FIRST_ITEM_PADDING = 10

        const val SHARE_TEXT_SIZE = 889f

        const val CANCEL_SHARING_TEXT_SIZE = 889f
    }

    private val mockTitle: SbisTextView = mock {
        on { findViewById<SbisTextView>(R.id.design_folders_title) } doReturn this@mock.mock
    }

    private val mockStateIcon: SbisTextView = mock {
        on { findViewById<SbisTextView>(R.id.design_folders_state_icon) } doReturn this@mock.mock
    }

    private val mockResProvider: FolderHolderResourceProvider = mock {
        on { getFirstItemLeftPaddingPx() } doReturn FIRST_ITEM_PADDING

        on { getDimen(AdditionalCommandType.SHARE.size) } doReturn SHARE_TEXT_SIZE

        on { getDimen(AdditionalCommandType.CANCEL_SHARING.size) } doReturn CANCEL_SHARING_TEXT_SIZE
    }

    private val mockRootView: ConstraintLayout = mock {
        on { layoutParams } doReturn mock()

        on { childCount } doReturn 3
        on { getChildAt(0) } doReturn mockTitle
        on { getChildAt(1) } doReturn mockStateIcon
    }

    private val holder = AdditionalCommandHolder(
        viewBinding = DesignFoldersViewAditionalCommandBinding.bind(mockRootView),
        folderActionHandler = null,
        resourceProvider = mockResProvider,
    )

    @Test
    fun `When bind() called, then set title`() {
        val title = "some new title"
        holder.bind(command(title = title))

        verify(mockTitle).text = title
    }

    @Test
    @Parameters(method = "paramsForTypeWithoutIcon")
    fun `Given type without icon, when bind() called, then hide stateIcon`(type: AdditionalCommandType) {
        holder.bind(command(type = type))

        verify(mockStateIcon).isVisible = false
    }

    @Suppress("unused")
    private fun paramsForTypeWithoutIcon() = params {
        AdditionalCommandType.values()
            .filterNot(AdditionalCommandType::hasIcon)
            .forEach {
                add(it)
            }
    }

    @Suppress("unused")
    private fun paramsForTypeWithIcon() = params {
        AdditionalCommandType.values()
            .filter(AdditionalCommandType::hasIcon)
            .forEach {
                add(it)
            }
    }

    @Test
    fun `When bind() called, then set first level padding for title`() {
        holder.bind(command())

        verify(mockTitle).setPadding(eq(FIRST_ITEM_PADDING), any(), any(), any())
    }
}
