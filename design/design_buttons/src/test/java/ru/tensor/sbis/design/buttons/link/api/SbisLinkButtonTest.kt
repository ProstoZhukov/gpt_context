package ru.tensor.sbis.design.buttons.link.api

import android.app.Activity
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.BonusButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.BrandButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.DangerButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.DefaultButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.InfoButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.InternalDefaultStyle
import ru.tensor.sbis.design.buttons.base.models.style.LabelButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.LinkButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.NavigationButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PaleButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SecondaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.WarningButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Тесты API компонента [SbisLinkButton].
 *
 * @author mb.kruglova
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisLinkButtonTest {

    private lateinit var button: SbisLinkButton

    companion object {
        const val text = "HELLO"
        const val icon = '\ue9EB'
    }

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        button = SbisLinkButton(activity)
    }

    @Test
    fun `SbisLinkButton has size S by default`() {
        assertEquals(button.size, SbisButtonSize.S)
    }

    @Test
    fun `Set XL size`() {
        button.size = SbisButtonSize.XL
        assertEquals(button.size, SbisButtonSize.XL)
    }

    @Test
    fun `SbisLinkButton has InternalDefaultStyle by default`() {
        assertEquals(button.style, InternalDefaultStyle)
    }

    @Test
    fun `Set style`() {
        val range = 1..14
        range.forEach {
            val newStyle = getStyle(it)
            button.style = newStyle
            assertEquals(button.style, newStyle)
        }
    }

    @Test
    fun `SbisLinkButton is enabled by default`() {
        assertEquals(button.state, SbisButtonState.ENABLED)
    }

    @Test
    fun `Set disabled state`() {
        button.state = SbisButtonState.DISABLED
        assertEquals(button.state, SbisButtonState.DISABLED)
    }

    @Test
    fun `SbisLinkButton has center horizontal alignment by default`() {
        assertEquals(button.align, HorizontalAlignment.CENTER)
    }

    @Test
    fun `Set left horizontal alignment`() {
        button.align = HorizontalAlignment.LEFT
        assertEquals(button.align, HorizontalAlignment.LEFT)
    }

    @Test
    fun `SbisLinkButton doesn't have title and icon by default`() {
        assertEquals(
            button.model.title,
            SbisButtonTitle(
                text = null,
                position = HorizontalPosition.RIGHT,
                size = SbisButtonTitleSize.M,
                style = null
            )
        )
        assertEquals(button.model.icon, null)
    }

    @Test
    fun `Set title via model`() {
        button.model = SbisButtonModel(
            title = SbisButtonTitle(
                text = text
            )
        )

        assertEquals(button.model.title?.text, text)
    }

    @Test
    fun `Set title via method`() {
        button.setTitle(text)

        assertEquals(button.model.title?.text, text)
    }

    @Test
    fun `Set icon via model`() {
        button.model = SbisButtonModel(
            icon = SbisButtonTextIcon(
                icon = icon.toString()
            )
        )

        assertEquals((button.model.icon as SbisButtonTextIcon).icon, icon.toString())
    }

    @Test
    fun `Set icon via method`() {
        button.setIconChar(icon)

        assertEquals((button.model.icon as SbisButtonTextIcon).icon, icon.toString())
    }

    @Test
    fun `SbisLinkButtonTitle has right position by default`() {
        button.setTitle(text)

        assertEquals(button.model.title?.position, HorizontalPosition.RIGHT)
    }

    @Test
    fun `Set left position for SbisLinkButtonTitle`() {
        button.model = SbisButtonModel(
            title = SbisButtonTitle(
                text = text,
                position = HorizontalPosition.LEFT
            )
        )

        assertEquals(button.model.title?.position, HorizontalPosition.LEFT)
    }

    @Test
    fun `SbisLinkButtonTitle has size M by default`() {
        button.setTitle(text)

        assertEquals(button.model.title?.size, SbisButtonTitleSize.M)
    }

    @Test
    fun `Set XL size for SbisLinkButtonTitle`() {
        button.model = SbisButtonModel(
            title = SbisButtonTitle(
                text = text,
                size = SbisButtonTitleSize.XL
            )
        )

        assertEquals(button.model.title?.size, SbisButtonTitleSize.XL)
    }

    @Test
    fun `SbisLinkButtonIcon has null size by default`() {
        button.setIconChar(icon)

        assertEquals((button.model.icon as SbisButtonTextIcon).size, null)
    }

    @Test
    fun `Set X3L size for SbisLinkButtonIcon`() {
        button.model = SbisButtonModel(
            icon = SbisButtonTextIcon(
                icon = icon.toString(),
                size = SbisButtonIconSize.X3L
            )
        )

        assertEquals((button.model.icon as SbisButtonTextIcon).size, SbisButtonIconSize.X3L)
    }

    private fun getStyle(styleCode: Int) = when (styleCode) {
        1 -> PrimaryButtonStyle
        2 -> SecondaryButtonStyle
        3 -> SuccessButtonStyle
        4 -> UnaccentedButtonStyle
        5 -> BonusButtonStyle
        6 -> DangerButtonStyle
        7 -> WarningButtonStyle
        8 -> InfoButtonStyle
        9 -> DefaultButtonStyle
        10 -> NavigationButtonStyle
        11 -> PaleButtonStyle
        12 -> BrandButtonStyle
        13 -> LinkButtonStyle
        14 -> LabelButtonStyle
        else -> InternalDefaultStyle
    }
}