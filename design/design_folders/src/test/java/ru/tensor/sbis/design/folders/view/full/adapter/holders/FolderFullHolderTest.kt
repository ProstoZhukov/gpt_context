package ru.tensor.sbis.design.folders.view.full.adapter.holders

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.counters.textcounter.SbisTextCounter
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.design.folders.databinding.DesignFoldersItemViewFullBinding
import ru.tensor.sbis.design.folders.test_utils.folder
import ru.tensor.sbis.design.folders.view.full.adapter.FolderHolderResourceProvider
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem

/**
 * @author da.zolotarev
 */
@RunWith(MockitoJUnitRunner::class)
class FolderFullHolderTest {

    private companion object {
        const val FIRST_ITEM_PADDING = 18
        const val DEPTH_PADDING_SIZE = 20
    }

    @Mock
    private lateinit var mockRootView: SwipeableLayout

    @Mock
    private lateinit var mockContainer: ConstraintLayout

    @Mock
    private lateinit var mockTitleView: SbisTextView

    @Mock
    private lateinit var mockCounter: SbisTextCounter

    @Mock
    private lateinit var mockStateIconView: SbisTextView

    @Mock
    private lateinit var mockResProvider: FolderHolderResourceProvider

    private lateinit var binding: DesignFoldersItemViewFullBinding

    private lateinit var holder: FolderFullHolder

    @Before
    fun init() {
        whenever(mockResProvider.getFirstItemLeftPaddingPx()).doReturn(FIRST_ITEM_PADDING)
        whenever(mockResProvider.getItemLeftPaddingPx()).doReturn(DEPTH_PADDING_SIZE)

        whenever(mockContainer.findViewById<ConstraintLayout>(R.id.design_folders_container)).thenReturn(mockContainer)
        whenever(mockContainer.findViewById<SbisTextView>(R.id.design_folders_title)).thenReturn(mockTitleView)
        whenever(mockContainer.findViewById<SbisTextView>(R.id.design_folders_state_icon)).thenReturn(mockStateIconView)
        whenever(mockContainer.findViewById<View>(R.id.design_folders_marker)).thenReturn(mock())
        whenever(mockContainer.findViewById<SbisTextCounter>(R.id.design_folders_text_counter)).thenReturn(mockCounter)

        whenever(mockRootView.childCount).thenReturn(1)
        whenever(mockRootView.getChildAt(0)).thenReturn(mockContainer)

        binding = DesignFoldersItemViewFullBinding.bind(mockRootView)
        holder = FolderFullHolder(binding, null, mockResProvider)
    }

    @Test
    fun `When bind() called, then apply title text`() {
        val titleText = "some title"
        holder.bind(folder(title = titleText))

        verify(mockTitleView).text = titleText
    }

    @Test
    fun `When bind() called, then set click listener to container`() {
        holder.bind(folder())

        verify(mockContainer).setOnClickListener(any())
    }

    @Test
    fun `Given totalCount not zero, when bind() called, then show totalCount`() {
        val count = 22

        holder.bind(folder(totalContentCount = count))

        verify(mockCounter).unaccentedCounter = count
    }

    @Test
    fun `Given unreadCount not zero, when bind() called, then show unreadCount`() {
        val count = 42
        holder.bind(folder(unreadContentCount = count))

        verify(mockCounter).accentedCounter = count
    }

    @Test
    fun `Given both counters not zero, when bind() called, then sets both counters`() {
        holder.bind(folder(unreadContentCount = 42, totalContentCount = 22))

        verify(mockCounter).accentedCounter = 42
        verify(mockCounter).unaccentedCounter = 22
    }

    @Test
    fun `Given state without icon, when bind() called, then hide stateIcon`() {
        holder.bind(folder(type = FolderType.DEFAULT))

        verify(mockStateIconView).isVisible = false
    }

    @Test
    fun `Given state with icon, when bind() called, then show stateIcon`() {
        val type = FolderType.SHARED
        holder.bind(folder(type = type))

        verify(mockStateIconView).isVisible = true
        verify(mockStateIconView).setText(type.iconRes)
    }

    @Test
    fun `Given state without actions, when bind() called, then lock menu`() {
        holder.bind(folder(type = FolderType.DEFAULT))

        verify(mockRootView).isDragLocked = true
    }

    @Test
    fun `Given state with actions, when bind() called, then show menu`() {
        holder.bind(folder(type = FolderType.EDITABLE))

        verify(mockRootView).isDragLocked = false
        verify(mockRootView).setMenu(any<List<SwipeMenuItem>>())
    }

    @Test
    fun `Given first level folder, when bind() called, then set first level padding for title`() {
        holder.bind(folder(depthLevel = 0))

        verify(mockTitleView).setPadding(eq(FIRST_ITEM_PADDING), any(), any(), any())
    }

    @Test
    fun `Given second level folder, when bind() called, then set second level padding for title`() {
        holder.bind(folder(depthLevel = 1))

        verify(mockTitleView).setPadding(eq(FIRST_ITEM_PADDING + DEPTH_PADDING_SIZE), any(), any(), any())
    }

    @Test
    fun `Given third level folder, when bind() called, then set third level padding for title`() {
        holder.bind(folder(depthLevel = 2))

        verify(mockTitleView).setPadding(eq(FIRST_ITEM_PADDING + DEPTH_PADDING_SIZE * 2), any(), any(), any())
    }

    @Test
    fun `Given folder with level more than 3, when bind() called, then set also third level padding for title`() {
        holder.bind(folder(depthLevel = 3 + 1))

        verify(mockTitleView).setPadding(eq(FIRST_ITEM_PADDING + DEPTH_PADDING_SIZE * 2), any(), any(), any())
    }
}
