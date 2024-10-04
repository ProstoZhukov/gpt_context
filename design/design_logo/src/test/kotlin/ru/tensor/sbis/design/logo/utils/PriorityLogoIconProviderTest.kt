package ru.tensor.sbis.design.logo.utils

import android.graphics.drawable.Drawable
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.logo.api.IconSource

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PriorityLogoIconProviderTest {

    private val drawableStub: Drawable = mockk()

    @Test
    fun `When all icons are available then provider returns BrandImage type`() {
        val iconSource: IconSource = mockk()
        every { iconSource.defaultIcon }.returns(drawableStub)
        every { iconSource.brandLogo }.returns(drawableStub)
        every { iconSource.brandImage }.returns(drawableStub)

        // Act
        val iconType = PriorityLogoIconProvider(iconSource).getIcon(drawableStub)

        // Verify
        assert(iconType is LogoIcon.BrandImage)
    }

    @Test
    fun `When defaultIcon and brandLogo icons are available then provider return BrandLogo type`() {
        val iconSource: IconSource = mockk()
        every { iconSource.defaultIcon }.returns(drawableStub)
        every { iconSource.brandLogo }.returns(drawableStub)
        every { iconSource.brandImage }.returns(null)

        // Act
        val iconType = PriorityLogoIconProvider(iconSource).getIcon(null)

        // Verify
        assert(iconType is LogoIcon.BrandLogo)
    }

    @Test
    fun `When no one icons are not available then provider return DefaultIcon type`() {
        val iconSource: IconSource = mockk()
        every { iconSource.defaultIcon }.returns(drawableStub)
        every { iconSource.brandLogo }.returns(null)
        every { iconSource.brandImage }.returns(null)

        // Act
        val iconType = PriorityLogoIconProvider(iconSource).getIcon(null)

        // Verify
        assert(iconType is LogoIcon.DefaultIcon)
    }

    @Test
    fun `When no one icons are not available and API icon was set then provider return DefaultIcon type`() {
        val iconSource: IconSource = mockk()
        every { iconSource.defaultIcon }.returns(drawableStub)
        every { iconSource.brandLogo }.returns(null)
        every { iconSource.brandImage }.returns(null)

        // Act
        val iconType = PriorityLogoIconProvider(iconSource).getIcon(drawableStub)

        // Verify
        assert(iconType is LogoIcon.DefaultIcon)
    }
}