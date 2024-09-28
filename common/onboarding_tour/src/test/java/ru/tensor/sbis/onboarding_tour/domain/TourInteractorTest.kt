package ru.tensor.sbis.onboarding_tour.domain

import android.content.Context
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.onboarding_tour.data.storage.TourStorage
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior

internal class TourInteractorTest {

    private val mockContext: Context = mock {}
    private val mockPerformanceProvider: DevicePerformanceProvider = mock {
        on { isLowPerformanceDevice() } doAnswer { isLowPerformance }
    }
    private val mockStorage: TourStorage = mock {
        onBlocking { getTour(anyOrNull()) } doAnswer { tour }
    }
    private val mockDependency: OnboardingTourDependency = mock {
        on { loginInterface } doAnswer { null }
    }

    private var isLowPerformance = false
    private lateinit var tour: TourContent
    private lateinit var interactor: TourInteractor

    @Before
    fun setUp() {
        interactor = TourInteractor(mockStorage, mockPerformanceProvider, mock())
    }

    @Test
    fun `Received tour content contains only relevant pages`(): Unit = runBlocking {
        tour = TourCreatorImpl(mockContext, mockDependency).create {
            defaultBanner { logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111)) }
            rules { displayBehavior = DisplayBehavior.UNIQUE }
            page { checkIsRequired { false } }
            page { checkIsRequired { true } }
            page { checkIsRequired { false } }
            page { checkIsRequired { true } }
        } as TourContent

        interactor.getContent(OnboardingTourProvider.DEFAULT_NAME).apply {
            assertEquals(2, pages.size)
        }
    }

    @Test
    fun `Received tour content contains correct current page`(): Unit = runBlocking {
        tour = TourCreatorImpl(mockContext, mockDependency).create {
            defaultBanner { logoType = SbisLogoType.TextIconAppName(PlatformSbisString.Res(111)) }
            rules { displayBehavior = DisplayBehavior.ALWAYS }
            page { }
            page { }
            page { }
            page { }
        } as TourContent

        interactor.getContent(OnboardingTourProvider.DEFAULT_NAME, restoredId = 2).apply {
            assertEquals(4, pages.size)
            assertEquals(2, startPosition)
            assertEquals(DisplayBehavior.ALWAYS, rules.displayBehavior)
        }
    }

    @Test
    fun `Use animation by default`() {
        assertTrue(interactor.isAnimated())
    }

    @Test
    fun `Do not use animation when autotests`() {
        DebugTools.updateIsAutoTestLaunch(
            mock {
                on { categories } doReturn setOf("SBIS_AUTOTEST_LAUNCH")
            }
        )
        assertFalse(interactor.isAnimated())
    }

    @Test
    fun `Do not use animation when low performing device`() {
        isLowPerformance = true
        assertFalse(interactor.isAnimated())
    }
}