package ru.tensor.sbis.onboarding_tour.domain

import android.Manifest
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourCreator
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior

internal class TourCreatorImplTest {

    private val mockDependency: OnboardingTourDependency = mock {
        on { loginInterface } doAnswer { null }
    }
    private lateinit var creator: OnboardingTourCreator

    @Before
    fun setUp() {
        creator = TourCreatorImpl(mock(), mockDependency)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception when creating empty OnboardingTour`() {
        creator.create {
            defaultBanner {
                logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111))
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception on null LoginInterface when property showOnlyOnce is true`() {
        creator.create {
            defaultBanner {
                logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111))
            }
            rules {
                displayBehavior = DisplayBehavior.PER_USER
            }
            page {
                title = 111
                description = 222
                image = 333
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception when creating pages with the same optional IDs`() {
        creator.create {
            defaultBanner {
                logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111))
            }
            page {
                position = 1
                title = 111
            }
            page {
                position = 1
                title = 111
            }
        }
    }

    @Test
    fun `Create simplest tour without exceptions`() {
        val tour = creator.create {
            defaultBanner {
                logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111))
            }
            rules {
                displayBehavior = DisplayBehavior.UNIQUE
            }
            page {
                title = 111
            }
            page {
                title = 111
            }
        }

        tour as TourContent
        assertEquals(2, tour.pages.size)
    }

    @Test
    fun `Proper creation of unsorted complex tour content`() {
        val tour = creator.create {
            rules {
                displayBehavior = DisplayBehavior.UNIQUE
            }
            onDismiss { }
            defaultBanner {
                logoType = logoTextIconApp
                buttonType = BannerButtonType.CLOSE
            }
            page {
                title = 11
                image = 111
                nextButtonTitle = NEXT_BTN
                terms {
                    caption = R.string.onboarding_tour_terms_caption
                    links = listOf("link")
                }
            }
            page {
                title = 22
                image = 222
                nextButtonTitle = NEXT_BTN
                permissions {
                    permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                    isRequired = true
                    onRequestRationale { _, _ -> emptyFlow() }
                }
            }
            page {
                title = 33
                image = 333
                nextButtonTitle = FINISH_BTN
                customBanner {
                    logoType = logoTextIcon
                    buttonType = BannerButtonType.SKIP
                }
            }
        } as TourContent

        assertEquals(3, tour.pages.size)

        tour.pages[0].apply {
            assertNotNull(terms)
            assertTrue(banner.bannerLogoType is SbisLogoType.TextIconAppName)
            assertEquals(BannerButtonType.CLOSE, banner.bannerButtonType)
            assertEquals(NEXT_BTN, button.titleResId)
            assertEquals(11, titleResId)
            assertEquals(111, imageResId)
            assertNull(permissions)
            assertEquals(R.string.onboarding_tour_terms_caption, terms!!.termsCaption)
            assertTrue(terms.termsLinks.isNotEmpty())
            assertEquals("link", terms.termsLinks.first())
        }
        tour.pages[1].apply {
            assertNull(terms)
            assertTrue(banner.bannerLogoType is SbisLogoType.TextIconAppName)
            assertEquals(BannerButtonType.CLOSE, banner.bannerButtonType)
            assertEquals(NEXT_BTN, button.titleResId)
            assertEquals(22, titleResId)
            assertEquals(222, imageResId)
            assertNotNull(permissions)
            assertTrue(permissions!!.isMandatory)
            assertEquals(2, permissions.permissions.size)
        }
        tour.pages[2].apply {
            assertNull(terms)
            assertTrue(banner.bannerLogoType is SbisLogoType.TextIcon)
            assertEquals(BannerButtonType.SKIP, banner.bannerButtonType)
            assertEquals(FINISH_BTN, button.titleResId)
            assertEquals(33, titleResId)
            assertEquals(333, imageResId)
            assertNull(permissions)
        }
    }

    @Test
    fun `Received tour content with sorted pages`() {
        val tour = creator.create {
            defaultBanner { logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111)) }
            rules { displayBehavior = DisplayBehavior.UNIQUE }
            page {
                position = 2
                title = 222
            }
            page {
                position = 4
                title = 444
            }
            page {
                position = 1
                title = 111
            }
            page {
                position = 3
                title = 333
            }
        } as TourContent

        tour.apply {
            assertEquals(4, pages.size)
            assertEquals(111, pages[0].titleResId)
            assertEquals(222, pages[1].titleResId)
            assertEquals(333, pages[2].titleResId)
            assertEquals(444, pages[3].titleResId)
        }
    }

    private companion object {
        val logoTextIconApp = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111))
        val logoTextIcon = SbisLogoType.TextIcon
        const val NEXT_BTN = 1234
        const val FINISH_BTN = 4321
    }
}