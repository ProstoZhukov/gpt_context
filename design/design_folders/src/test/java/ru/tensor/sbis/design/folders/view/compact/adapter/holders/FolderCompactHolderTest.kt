package ru.tensor.sbis.design.folders.view.compact.adapter.holders

import android.content.Context
import android.content.res.Resources
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.databinding.DesignFoldersItemViewCompactBinding
import ru.tensor.sbis.design.folders.test_utils.folder
import ru.tensor.sbis.design.chips.item.SbisChipsItemView

/**
 * @author da.zolotarev
 *
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class FolderCompactHolderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val mockRootView: FrameLayout = mock { on { context } doReturn context }

    private val mockChips: SbisChipsItemView = mock()

    private val mockLayoutParams: RecyclerView.LayoutParams = mock()

    private lateinit var binding: DesignFoldersItemViewCompactBinding

    private val mockResources: Resources = mock()

    private lateinit var holder: FolderCompactHolder

    @Before
    fun init() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
        // моки, для сгенерированного DesignFoldersItemViewCompactBinding класса
        whenever(mockRootView.findViewById<SbisChipsItemView>(R.id.design_folders_item_compact)).thenReturn(mockChips)
        whenever(mockRootView.childCount).thenReturn(1)
        whenever(mockRootView.getChildAt(0)).thenReturn(mockChips)
        whenever(mockChips.findViewById<SbisChipsItemView>(any())).doReturn(mockChips)

        whenever(mockRootView.resources).doReturn(mockResources)
        whenever(mockRootView.layoutParams).doReturn(mockLayoutParams)

        whenever(mockResources.getDimensionPixelSize(any())).thenReturn(0)

        binding = DesignFoldersItemViewCompactBinding.bind(mockRootView)

        holder = FolderCompactHolder(binding)
    }

    @Test
    fun `When view holder bound to folder, then view should receive it`() {
        val folder = folder()

        holder.bind(folder)

    }

    private fun FolderCompactHolder.bind(folder: Folder) =
        bind(folder, isFirst = false, isLast = false, isFolderIconVisible = false)
}
