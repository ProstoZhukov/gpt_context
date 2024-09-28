package ru.tensor.sbis.design.logo

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.logo.utils.LogoIcon
import ru.tensor.sbis.design.logo.utils.PriorityLogoIconProvider
import ru.tensor.sbis.design.logo.utils.SbisLogoStyle
import ru.tensor.sbis.design.logo.utils.SbisLogoStyleHolder
import ru.tensor.sbis.design.theme.res.createString

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.HorizontalPosition


/**
 * Тесты контроллера логотипа.
 *
 * @author ra.geraskin
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class LogoControllerTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()


    private lateinit var controller: LogoController
    private val iconProvider: PriorityLogoIconProvider = mockk()

    private val defaultIconDrawableStub = ColorDrawable(Color.RED).apply {
        bounds.set(Rect(0, 0, 50, 50))
    }

    @Before
    fun setup() {
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
    }

    @Test
    fun `When icon is default icon and IconText type is set, then icon is on the left and text is on the right`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.DefaultIcon(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.IconText

        // Act
        controller.measureViewWidth()

        // Verify
        assertEquals(controller.icon.iconDrawable.bounds.left, 0)
        assertNotEquals(controller.title.left, 0)
    }


    @Test
    fun `When icon is default icon and TextIcon type is set, then text is on the left and icon is on the right`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.DefaultIcon(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.TextIcon

        // Act
        controller.measureViewWidth()

        // Verify
        assertEquals(controller.title.left, 0)
        assertNotEquals(controller.icon.iconDrawable.bounds.left, 0)
    }

    @Test
    fun `When icon is default icon and TextIconAppName type is set, then custom text is on the left and icon is on the right`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.DefaultIcon(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.TextIconAppName(createString("демо"))

        // Act
        controller.measureViewWidth()

        // Verify
        assertNotEquals(controller.title.left, 0)
        assertEquals(controller.icon.iconDrawable.bounds.left, 0)
    }

    @Test
    fun `When icon is brand image and TextIconAppName type is set, then title is not drawn`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.BrandImage(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.TextIconAppName(createString("демо"))

        // Act
        controller.measureViewWidth()

        // Verify
        assertEquals(controller.title.innerLayoutRect.height(), 0f)
        assertEquals(controller.title.innerLayoutRect.width(), 0f)
    }

    @Test
    fun `When icon is brand image and TextIconAppName type is set, then the image takes up all the space`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.BrandImage(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.TextIconAppName(createString("демо"))

        // Act
        val viewWidth = controller.measureViewWidth()

        // Verify
        assertEquals(viewWidth, controller.iconWidth)
    }

    @Test
    fun `When icon is brand image and TextIconAppName type with right icon position is set, then icon is on the right border of view`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.BrandLogo(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.TextIconAppName(createString("демо"), iconPosition = HorizontalPosition.RIGHT)

        // Act
        val viewWidth = controller.measureViewWidth()

        // Verify
        assertEquals(controller.icon.iconDrawable.bounds.right, viewWidth)
    }


    @Test
    fun `When icon is default icon and Icon type is set, then icon takes up all the space`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.DefaultIcon(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.Icon

        // Act
        val viewWidth = controller.measureViewWidth()

        // Verify
        assertEquals(viewWidth, controller.iconWidth)
    }

    @Test
    fun `When icon is brand icon and Icon type is set, then icon takes up all the space`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.BrandLogo(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.type = SbisLogoType.Icon

        // Act
        val viewWidth = controller.measureViewWidth()

        // Verify
        assertEquals(viewWidth, controller.iconWidth)
    }

    @Test
    fun `When icon is default icon and Icon type and Navigation style are set, then icon height equals`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.DefaultIcon(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.style = SbisLogoStyle.Navigation
        controller.type = SbisLogoType.Icon

        // Act
        controller.measureViewWidth()

        // Verify
        assertEquals(
            controller.icon.iconDrawable.bounds.height(),
            SbisLogoStyleHolder(activity).brandIconHeightNavigation
        )
    }


    @Test
    fun `When icon is default icon and Icon type and Page style are set, then icon height equals`() {
        every { iconProvider.getIcon(any()) } returns LogoIcon.DefaultIcon(defaultIconDrawableStub)
        controller = LogoController(activity, iconProvider, SbisLogoStyleHolder(activity))
        controller.attach(View(activity))
        controller.style = SbisLogoStyle.Page
        controller.type = SbisLogoType.Icon

        // Act
        controller.measureViewWidth()

        // Verify
        assertEquals(
            controller.icon.iconDrawable.bounds.height(),
            SbisLogoStyleHolder(activity).brandIconHeightPage
        )
    }


}