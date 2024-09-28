package ru.tensor.sbis.message_panel.recorder.util

import android.view.ViewGroup
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.tensor.sbis.recorder.decl.RecorderView

/**
 * Тест проверки наличия родительского элемента [RecorderView] с атрибутом android:clipChildren="false"
 *
 * @author us.bessonov
 */
class RecorderParentClipChildrenAttributeCheckTest {

    @Test
    fun `Returns true if there is a parent with clipChildren = false`() {
        val recorderView = mockRecorderView(hasParentWithClipChildrenFalse = true)
        assertTrue(hasParentWithoutClippingChildren(recorderView))
    }

    @Test
    fun `Returns false if there is no parent with clipChildren = false`() {
        val recorderView = mockRecorderView(hasParentWithClipChildrenFalse = false)
        assertFalse(hasParentWithoutClippingChildren(recorderView))
    }

    private fun mockRecorderView(hasParentWithClipChildrenFalse: Boolean): RecorderView {
        val parent2 = mock<ViewGroup> {
            on { clipChildren } doReturn !hasParentWithClipChildrenFalse
            on { parent } doReturn null
        }
        val parent1 = mock<ViewGroup> {
            on { clipChildren } doReturn true
            on { parent } doReturn parent2
        }
        return mock {
            on { parent } doReturn parent1
        }
    }
}