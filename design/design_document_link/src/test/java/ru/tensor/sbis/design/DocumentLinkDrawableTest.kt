package ru.tensor.sbis.design

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.documentlink.models.DocumentLinkModel
import ru.tensor.sbis.design.documentlink.utils.DocumentLinkStyle
import ru.tensor.sbis.design.documentlink.view.DocumentLinkDrawable
import ru.tensor.sbis.design.documentlink.view.DocumentLinkView

/**
 * @author da.zolotarev
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class DocumentLinkDrawableTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
    }

    @Test
    fun `When drawable created, it does not have any listeners`() {
        val documentLinkDrawable = DocumentLinkDrawable(activity)

        assertFalse(documentLinkDrawable.withArrowIcon)
    }

    @Test
    fun `Given view with click listener, when model changed, then click listener should not be modified`() {
        val listener: View.OnClickListener = mock()
        val documentLinkView = DocumentLinkView(activity)

        documentLinkView.setOnClickListener(listener)
        documentLinkView.documentLinkModel = documentLinkView.documentLinkModel.copy()

        verify(spy(documentLinkView), never()).setOnClickListener(argThat { this != listener })
    }

    @Test
    fun `When drawable created, then selected default unaccented style`() {
        val documentLinkDrawable = DocumentLinkDrawable(activity)

        assertTrue(documentLinkDrawable.style == DocumentLinkStyle.DocumentLinkDefaultStyle)
    }

    @Test
    fun `Given model with header and comment, when it is assigned to drawable,  then both text will drawn`() {
        val documentLinkDrawable = spy(DocumentLinkDrawable(activity))
        val canvas = Canvas(Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888))

        documentLinkDrawable.documentLinkModel = DocumentLinkModel(
            "Заголовок",
            "Комментарий"
        )
        documentLinkDrawable.draw(canvas)

        assertTrue(
            documentLinkDrawable.headerBounds != RectF(0f, 0f, 0f, 0f) &&
                documentLinkDrawable.commentBounds != RectF(0f, 0f, 0f, 0f)
        )
    }

    @Test
    fun `Given model with header, when it is assigned to drawable,  then be called drawHeader method`() {
        val documentLinkDrawable = spy(DocumentLinkDrawable(activity))
        val canvas = Canvas(Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888))

        documentLinkDrawable.documentLinkModel = DocumentLinkModel(
            "Заголовок",
            ""
        )
        documentLinkDrawable.draw(canvas)

        assertTrue(
            documentLinkDrawable.headerBounds != RectF(0f, 0f, 0f, 0f) &&
                documentLinkDrawable.commentBounds == RectF(0f, 0f, 0f, 0f)
        )
    }

    @Test
    fun `Given model with header, when it is assigned to drawable,  then be called drawComment method`() {
        val documentLinkDrawable = spy(DocumentLinkDrawable(activity))
        val canvas = Canvas(Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888))

        documentLinkDrawable.documentLinkModel = DocumentLinkModel(
            "",
            "Комментарий"
        )
        documentLinkDrawable.draw(canvas)

        assertTrue(
            documentLinkDrawable.headerBounds == RectF(0f, 0f, 0f, 0f) &&
                documentLinkDrawable.commentBounds != RectF(0f, 0f, 0f, 0f)
        )
    }
}