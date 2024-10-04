package ru.tensor.sbis.design.stubview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.R as RDesign

/**
 * @author ma.kolpakov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class StubViewTest {

    private val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), RDesign.style.AppGlobalTheme)

    private val imageType = StubViewImageType.NO_MESSAGES
    private val testIconDrawable: Drawable = imageType.getDrawable(context) ?: ColorDrawable(Color.MAGENTA)
    private val testViewIcon: View = ImageView(context).apply { setImageDrawable(testIconDrawable) }
    private val testMessageRes: Int = R.string.design_stub_view_no_messages_message
    private val testMessage: String = context.getString(testMessageRes)
    private val testDetailsRes: Int = R.string.design_stub_view_no_messages_details
    private val testDetails: String = context.getString(testDetailsRes)

    private val StubView.imageIcon: ImageView
        get() = findViewById(R.id.design_stubview_image_icon)

    private val StubView.viewIcon: ViewGroup
        get() = findViewById(R.id.design_stubview_view_icon)

    private val StubView.message: SbisTextView
        get() = findViewById(R.id.design_stubview_message)

    private val StubView.details: TextView
        get() = findViewById(R.id.design_stubview_details)

    @Test
    fun `When StubView created, then detailsTextView links are clickable`() {
        val stubView = getStubView()

        assertTrue(stubView.details.movementMethod is LinkMovementMethod)
    }

    @Test
    fun `When just init, then icons and texts are empty`() {
        val stubView = getStubView()

        assertNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertTrue(stubView.message.isEmpty)
        assertTrue(stubView.details.isEmpty)
    }

    // region ResourceImageStubContent
    @Test
    fun `Given ResourceImageStubContent with texts only, when setContent() called, then apply texts`() {
        val stubView = getStubView()
        val content = ResourceImageStubContent(
            message = testMessage,
            details = testDetails
        )

        stubView.setContent(content)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `Given ResourceImageStubContent with texts ids only, when setContent() called, then apply texts`() {
        val stubView = getStubView()
        val content = ResourceImageStubContent(
            messageRes = testMessageRes,
            detailsRes = testDetailsRes
        )

        stubView.setContent(content)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `Given ResourceImageStubContent with icon only, when setContent() called, then apply icon`() {
        val stubView = getStubView()
        val content = ImageStubContent(
            imageType = imageType,
            message = null,
            details = null
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertTrue(stubView.message.isEmpty)
        assertTrue(stubView.details.isEmpty)
    }

    @Test
    fun `Given ResourceImageStubContent with icon and texts, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = ImageStubContent(
            imageType = imageType,
            message = testMessage,
            details = testDetails
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `Given ResourceImageStubContent with icon and texts ids, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = ImageStubContent(
            imageType = imageType,
            testMessageRes,
            testDetailsRes
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }
    // endregion ResourceImageStubContent

    // region DrawableImageStubContent
    @Test
    fun `Given DrawableImageStubContent with icon only, when setContent() called, then apply icon`() {
        val stubView = getStubView()
        val content = DrawableImageStubContent(
            icon = testIconDrawable,
            message = null,
            details = null
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertTrue(stubView.message.isEmpty)
        assertTrue(stubView.details.isEmpty)
    }

    @Test
    fun `Given DrawableImageStubContent with icon and texts, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content =
            DrawableImageStubContent(
                icon = testIconDrawable,
                message = testMessage,
                details = testDetails
            )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `Given DrawableImageStubContent with icon and texts ids, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = DrawableImageStubContent(
            icon = testIconDrawable,
            testMessageRes,
            testDetailsRes
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }
    // endregion DrawableImageStubContent

    // region ViewStubContent
    @Test
    fun `Given ViewStubContent with icon only, when setContent() called, then apply icon`() {
        val stubView = getStubView()
        val content = ViewStubContent(
            icon = testViewIcon,
            message = null,
            details = null
        )

        stubView.setContent(content)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(1, stubView.viewIcon.childCount)
        assertTrue(stubView.message.isEmpty)
        assertTrue(stubView.details.isEmpty)
    }

    @Test
    fun `Given ViewStubContent with icon and texts, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = ViewStubContent(
            icon = testViewIcon,
            message = testMessage,
            details = testDetails
        )

        stubView.setContent(content)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(1, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `Given ViewStubContent with icon and texts ids, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = ViewStubContent(
            icon = testViewIcon,
            testMessageRes,
            testDetailsRes
        )

        stubView.setContent(content)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(1, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }
    // endregion ViewStubContent

    // region IconStubContent
    @Test
    fun `Given IconStubContent with icon only, when setContent() called, then apply icon`() {
        val stubView = getStubView()
        val content = IconStubContent(
            icon = SbisMobileIcon.Icon.smi_Add,
            iconColor = android.R.color.background_dark,
            iconSize = android.R.dimen.app_icon_size,
            message = null,
            details = null,
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertTrue(stubView.message.isEmpty)
        assertTrue(stubView.details.isEmpty)
    }

    @Test
    fun `Given IconStubContent with icon and texts, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = IconStubContent(
            icon = SbisMobileIcon.Icon.smi_Add,
            iconColor = android.R.color.background_dark,
            iconSize = android.R.dimen.app_icon_size,
            message = testMessage,
            details = testDetails,
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `Given IconStubContent with icon and texts ids, when setContent() called, then apply all`() {
        val stubView = getStubView()
        val content = IconStubContent(
            icon = SbisMobileIcon.Icon.smi_Add,
            iconColor = android.R.color.background_dark,
            iconSize = android.R.dimen.app_icon_size,
            messageRes = testMessageRes,
            detailsRes = testDetailsRes,
        )

        stubView.setContent(content)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }
    // endregion IconStubContent

    @Ignore("Тест будет исправлен по ошибке: https://online.sbis.ru/opendoc.html?guid=343f12e9-ac12-4c3d-95a9-616217fe1257&client=3")
    @Test
    fun `Given some case, when setCase() called, then apply case content`() {
        val case = StubViewCase.NO_EVENTS
        val stubView = getStubView()

        stubView.setCase(case)

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(context.getString(case.messageRes), stubView.message.textString)
        assertEquals(context.getString(case.detailsRes), stubView.details.textString)
    }

    @Test
    fun `When setContentFactory() called, then update content`() {
        val content = DrawableImageStubContent(
            icon = testIconDrawable,
            testMessageRes,
            testDetailsRes
        )
        val contentFactory: (Context) -> StubViewContent = {
            content
        }
        val stubView = getStubView()

        stubView.setContentFactory(contentFactory)

        assertNotNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
        assertEquals(testMessage, stubView.message.textString)
        assertEquals(testDetails, stubView.details.textString)
    }

    @Test
    fun `When called setContent() with texts and then without text, then text views are empty`() {
        val content1 = ResourceImageStubContent(
            message = testMessage,
            details = testDetails
        )
        val content2 = ResourceImageStubContent(message = null, details = null)
        val stubView = getStubView()

        stubView.setContent(content1)
        stubView.setContent(content2)

        assertTrue(stubView.message.isEmpty)
        assertTrue(stubView.details.isEmpty)
    }

    @Test
    fun `When called setContent() with icon and then without icon, then icon are empty`() {
        val content1 = ResourceImageStubContent(R.drawable.stub_view_hint_arrow, null, null)
        val content2 = ResourceImageStubContent(ID_NULL, null, null)

        val stubView = getStubView()

        stubView.setContent(content1)
        stubView.setContent(content2)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
    }

    @Test
    fun `When called setContent() with view icon and then without icon, then icon are empty`() {
        val content1 = ViewStubContent(testViewIcon, null, null)
        val content2 = ResourceImageStubContent(ID_NULL, null, null)

        val stubView = getStubView()

        stubView.setContent(content1)
        stubView.setContent(content2)

        assertNull(stubView.imageIcon.drawable)
        assertEquals(0, stubView.viewIcon.childCount)
    }

    private fun getStubView() = StubView(context)

    private val TextView.isEmpty: Boolean
        get() = this.text.toString().isEmpty()

    private val TextView.textString: String
        get() = this.text.toString()

    private val SbisTextView.isEmpty: Boolean
        get() = this.text.toString().isEmpty()

    private val SbisTextView.textString: String
        get() = this.text.toString()
}
