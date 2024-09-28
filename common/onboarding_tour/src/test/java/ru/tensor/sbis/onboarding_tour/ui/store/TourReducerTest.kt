package ru.tensor.sbis.onboarding_tour.ui.store

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.onboarding_tour.domain.TourCreatorImpl
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect

internal class TourReducerTest {

    private val mockContext: Context = mock()
    private val mockDependency: OnboardingTourDependency = mock {
        on { loginInterface } doAnswer { null }
    }
    private val tourCreator = TourCreatorImpl(mockContext, mockDependency)
    private val tourReducer = TourReducer()

    @Test
    fun `Apply Message_UpdateTour and Message_MoveToPosition to OnboardingTourStore_State properly`() {
        val tourContent = tourCreator.create {
            rules {
                swipeTransition = false
            }
            defaultBanner {
                logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111))
            }
            page {
                nextButtonTitle = 111
            }
            page {
                title = 333
                description = 333
                image = 333
                button {
                    title = 333
                    titlePosition = HorizontalPosition.RIGHT
                    icon = SbisMobileIcon.Icon.smi_arrow
                }
                permissions {
                    permissions = listOf("permission")
                    isRequired = true
                }
            }
            page {
                nextButtonTitle = 555
            }
        } as TourContent

        var newState = tourReducer.run {
            val message = Message.UpdateTour(
                pageCount = tourContent.pages.size,
                pagePosition = 1,
                pages = tourContent.pages,
                isSwipeEnabled = tourContent.rules.swipeTransition,
                swipeCloseable = true,
                hasLaunchedCommand = false,
                backgroundEffect = BackgroundEffect.DYNAMIC,
                dismissCommand = tourContent.command
            )
            OnboardingTourStore.State().reduce(message)
        }

        assertEquals(1, newState.pagePosition)
        assertEquals(3, newState.pageCount)
        assertEquals(false, newState.isSwipeSupported)
        assertEquals(true, newState.swipeCloseable)
        assertEquals(false, newState.isInLastPassage)
        assertEquals(BackgroundEffect.DYNAMIC, newState.backgroundEffect)
        assertEquals(false, newState.arePermissionsChecked)
        assertEquals(true, newState.requirePermissions)
        val page = newState.passages[newState.pagePosition]
        assertEquals(true, page.isMandatoryPermits)
        assertEquals(333, page.titleResId)
        assertEquals(333, page.messageResId)
        assertEquals(333, page.imageResId)
        assertEquals(listOf("permission"), page.permissions)
        assertEquals(333, page.buttonTitle)
        assertEquals(HorizontalPosition.RIGHT, page.buttonTitlePosition)
        assertEquals(SbisMobileIcon.Icon.smi_arrow, page.buttonIcon)

        newState = tourReducer.run {
            val message = Message.MoveToPosition(2)
            newState.reduce(message)
        }

        assertEquals(2, newState.pagePosition)
        assertEquals(true, newState.isInLastPassage)
    }
}