package ru.tensor.sbis.list.view.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class LastItemBottomPaddingDecorationTest {

    private val mockLayoutParams = mock<RecyclerView.LayoutParams>()
    private val view0 = mock<View> {
        on { layoutParams } doReturn mockLayoutParams
    }
    private val view1 = mock<View> {
        on { layoutParams } doReturn mockLayoutParams
    }
    private val parent = mock<RecyclerView> {
        on { getChildAdapterPosition(view0) } doReturn 1
        on { getChildAdapterPosition(view1) } doReturn 2
    }

    @Test
    fun getItemOffsets() {
        val sectionsHolder = mock<SbisGridLayoutManager> {
            on { isInLastGroup(1) } doReturn false
            on { isInLastGroup(2) } doReturn true
        }
        val decoration = LastItemBottomPaddingDecoration(sectionsHolder, mock(), size, size)

        val outRect0 = Rect()
        val outRect1 = Rect()
        decoration.hasFab = true
        decoration.getItemOffsets(outRect0, view0, parent, mock())
        decoration.getItemOffsets(outRect1, view1, parent, mock())

        //Предпоследний элемент
        assertEquals(Rect(), outRect0)
        //Последний элемент
        assertEquals(Rect(0, 0, 0, size), outRect1)
    }
}

private const val size = 111