package ru.tensor.sbis.onboarding_tour.data.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.domain.DispatcherProvider
import ru.tensor.sbis.onboarding_tour.testUtils.SOURCE_TERM_CAPTION
import ru.tensor.sbis.onboarding_tour.testUtils.buildSimpleTourProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.TourPriority
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class TourStorageTest {

    private val mockContext: Context = mock {
        on { getString(R.string.onboarding_tour_terms_caption) } doReturn SOURCE_TERM_CAPTION
    }
    private val mockDataStore: TourProgressDataStore = mock {
        onBlocking { putPreference<Boolean>(any(), any()) } doAnswer { invocationOnMock ->
            @Suppress("UNCHECKED_CAST")
            tourActiveList[invocationOnMock.arguments[0] as Preferences.Key<Boolean>] =
                invocationOnMock.arguments[1] as Boolean
        }
        onBlocking { getPreference<Boolean>(any(), any()) } doAnswer { invocationOnMock ->
            tourActiveList.getOrDefault(
                invocationOnMock.arguments.first() as Preferences.Key<*>,
                invocationOnMock.arguments[1] as Boolean
            )
        }
    }
    private val mockUserAccount: UserAccount = mock {
        on { uuid } doReturn UUID.randomUUID()
        on { personId } doReturn "person_id"
    }
    private val mockLoginInterface: LoginInterface = mock {
        on { getCurrentAccount() } doReturn mockUserAccount
    }
    private val mockDependency: OnboardingTourDependency = mock {
        on { tourFeatureProviderSet } doAnswer { tourProviders }
        on { loginInterface } doReturn mockLoginInterface
    }

    private var tourProviders: Set<FeatureProvider<OnboardingTourProvider>> = buildDefaultTourProviders()
    private val tourActiveList = mutableMapOf<Preferences.Key<Boolean>, Boolean>()
    private val dispatcherProvider = DispatcherProvider()
    private lateinit var storage: TourStorage

    @Test
    fun `Check tour by name`() {
        storage = TourStorage(mockDataStore, dispatcherProvider, mockDependency)

        assertTrue(storage.hasTour(OnboardingTour.Name(TOUR_LOW)))
        assertTrue(storage.hasTour(OnboardingTour.Name(TOUR_NORMAL)))
        assertTrue(storage.hasTour(OnboardingTour.Name(TOUR_HIGH)))
        assertFalse(storage.hasTour(OnboardingTour.Name("UNKNOWN")))
    }

    @Test
    fun `Check tour deactivation`(): Unit = runBlocking {
        tourProviders = mutableSetOf<FeatureProvider<OnboardingTourProvider>>().apply {
            add(
                FeatureProvider {
                    mockContext.buildSimpleTourProvider(
                        TOUR_LOW,
                        TourPriority.LOW,
                        dependency = mockDependency
                    )
                }
            )
        }
        storage = TourStorage(mockDataStore, dispatcherProvider, mockDependency)
        assertNotNull(storage.hasActiveTour())

        storage.deactivateTour(OnboardingTour.Name(TOUR_LOW))

        assertNull(storage.hasActiveTour())
        assertNotNull(storage.getTour(OnboardingTour.Name(TOUR_LOW)))
    }

    @Test
    fun `Check getting the tour in priority order`(): Unit = runBlocking {
        storage = TourStorage(mockDataStore, dispatcherProvider, mockDependency)

        var tour = storage.hasActiveTour()?.let(storage::getTour)
        assertNotNull(tour)
        assertEquals(3, tour!!.pages.size)
        storage.deactivateTour(OnboardingTour.Name(TOUR_HIGH))

        tour = storage.hasActiveTour()?.let(storage::getTour)
        assertNotNull(tour)
        assertEquals(2, tour!!.pages.size)
        storage.deactivateTour(OnboardingTour.Name(TOUR_NORMAL))

        tour = storage.hasActiveTour()?.let(storage::getTour)
        assertNotNull(tour)
        assertEquals(1, tour!!.pages.size)
        storage.deactivateTour(OnboardingTour.Name(TOUR_LOW))

        tour = storage.hasActiveTour()?.let(storage::getTour)
        assertNull(tour)
    }

    private fun buildDefaultTourProviders(): Set<FeatureProvider<OnboardingTourProvider>> =
        mutableSetOf<FeatureProvider<OnboardingTourProvider>>().apply {
            add(
                FeatureProvider {
                    mockContext.buildSimpleTourProvider(
                        name = TOUR_LOW,
                        priority = TourPriority.LOW,
                        pageCount = 1,
                        dependency = mockDependency
                    )
                }
            )
            add(
                FeatureProvider {
                    mockContext.buildSimpleTourProvider(
                        name = TOUR_HIGH,
                        priority = TourPriority.HIGH,
                        pageCount = 3,
                        dependency = mockDependency
                    )
                }
            )
            add(
                FeatureProvider {
                    mockContext.buildSimpleTourProvider(
                        name = TOUR_NORMAL,
                        priority = TourPriority.NORMAL,
                        pageCount = 2,
                        dependency = mockDependency
                    )
                }
            )
        }

    private companion object {
        const val TOUR_LOW = "TOUR_LOW"
        const val TOUR_NORMAL = "TOUR_NORMAL"
        const val TOUR_HIGH = "TOUR_HIGH"
    }
}