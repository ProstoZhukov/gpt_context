package ru.tensor.sbis.onboarding_tour.ui

import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.text.getSpans
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.onboarding_tour.OnboardingTourPlugin
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.databinding.OnboardingTourFragmentBinding
import ru.tensor.sbis.onboarding_tour.domain.DispatcherProvider
import ru.tensor.sbis.onboarding_tour.testUtils.buildSimpleTourProvider
import ru.tensor.sbis.onboarding_tour.testUtils.getField
import ru.tensor.sbis.onboarding_tour.testUtils.setField
import ru.tensor.sbis.onboarding_tour.ui.TourFragment.Companion.ARG_TOUR_NAME
import ru.tensor.sbis.plugin_manager.PluginManager
import ru.tensor.sbis.plugin_manager.Tracer
import ru.tensor.sbis.plugin_manager.fake
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

// Попробовать на сл. версиях kotlinx-coroutines выше 1.6.1, падает только на ci в 1 из 10 сборок
@Ignore("Периодически падает на ci")
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.P]
)
internal class TourFragmentTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val testDispatcherProvider = DispatcherProvider(testDispatcher, testDispatcher)

    private val appContext: Context = ApplicationProvider.getApplicationContext()
    private var mockApplication: Application = mock {
        on { applicationContext } doReturn appContext
    }
    private var testProvider: OnboardingTourProvider = mock()

    private val onboardingTourPluginDependencies = object : Plugin<Unit> {
        override val api: Set<FeatureWrapper<out Feature>> = setOf(
            FeatureWrapper(CommonSingletonComponent::class.java) { mock() },
            FeatureWrapper(LoginInterface::class.java) { mock() },
            FeatureWrapper(OnboardingTourProvider::class.java) { testProvider }
        )
        override val dependency = Dependency.Builder().build()
        override val customizationOptions = Unit
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        PluginManager(tracer = Tracer.fake()).apply {
            registerPlugins(OnboardingTourPlugin, onboardingTourPluginDependencies)
            configure(mockApplication)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.coroutineContext.cancelChildren()
    }

    @Test
    fun `Fragment should be created with full args`() {
        val expectedName = OnboardingTour.Name("TestTour")
        val tourFragment = TourFragment.newInstance(expectedName)
        val actualName = tourFragment.arguments?.getParcelable<OnboardingTour.Name>(ARG_TOUR_NAME)

        assertEquals(expectedName, actualName)
    }

    @Test
    @Config(qualifiers = "sw360dp")
    fun `Lock PORTRAIT MODE when fragment is shown on phone`() {
        launch().onFragment { fragment ->
            assertEquals(
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
                fragment.requireActivity().requestedOrientation
            )
        }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `Do not lock PORTRAIT MODE mode when fragment is shown on tablet`() {
        launch().onFragment { fragment ->
            assertEquals(
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                fragment.requireActivity().requestedOrientation
            )
        }
    }

    @Test
    fun `Check if INDICATORS is visible for multi-page tour`() = testScope.runTest {
        val expectedPageCount = 7
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = expectedPageCount,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertEquals(View.VISIBLE, fragment.binding.onboardingTourIndicators.visibility)
                assertEquals(expectedPageCount, fragment.binding.onboardingTourIndicators.itemCount)
            }
        }
    }

    @Test
    fun `Check if INDICATORS is hidden for single-page tour`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 1,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertEquals(View.INVISIBLE, fragment.binding.onboardingTourIndicators.visibility)
                assertEquals(1, fragment.binding.onboardingTourIndicators.itemCount)
            }
        }
    }

    @Test
    fun `NEXT BUTTON is displayed when title is passed`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 3,
            buttonTitle = R.string.onboarding_tour_skip_title,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertTrue(fragment.binding.onboardingTourButton.isVisible)
                assertTrue(fragment.binding.onboardingTourButton.hasOnClickListeners())
            }
        }
    }

    @Test
    fun `NEXT BUTTON is hidden when title is not passed`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 1,
            buttonTitle = ID_NULL,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertFalse(fragment.binding.onboardingTourButton.isVisible)
            }
        }
    }

    @Test
    fun `BANNER BUTTON is displayed when type is specified`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 3,
            buttonBannerType = BannerButtonType.CLOSE,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertTrue(fragment.binding.onboardingTourClose.isVisible)
                assertFalse(fragment.binding.onboardingTourClose.text.isNullOrBlank())
                assertEquals(
                    fragment.binding.onboardingTourClose.typeface,
                    TypefaceManager.getSbisMobileIconTypeface(appContext)
                )
                assertTrue(fragment.binding.onboardingTourClose.hasOnClickListeners())
            }
        }
    }

    @Test
    fun `BANNER BUTTON is hidden when type is not specified`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 3,
            buttonBannerType = BannerButtonType.NONE,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertEquals(fragment.binding.onboardingTourClose.text, "")
                assertFalse(fragment.binding.onboardingTourClose.hasOnClickListeners())
            }
        }
    }

    @Test
    fun `Click close in BANNER to close the tour`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 2,
            buttonBannerType = BannerButtonType.CLOSE,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                val spyRouter = spy(fragment.controller.router)
                fragment.controller.router = spyRouter
                fragment.binding.onboardingTourClose.performClick()
                testScheduler.advanceUntilIdle()
                delay(UI_DELAY)
                verify(spyRouter).closeTour()
            }
        }
    }

    @Test
    fun `Check that carousel content is applied correctly`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 3,
            contentTitle = R.string.onboarding_tour_test_title,
            contentDescription = R.string.onboarding_tour_test_description,
            contentImage = R.drawable.blur_shapes,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                val visiblePage = fragment.binding.onboardingTourContent1
                assertTrue(visiblePage.onboardingTourTitle.isVisible)
                assertEquals(visiblePage.onboardingTourTitle.text, "Title")
                assertTrue(visiblePage.onboardingTourMessage.isVisible)
                assertEquals(visiblePage.onboardingTourMessage.text, "Description")
                assertTrue(visiblePage.onboardingTourMainImage.isVisible)
                assertNotNull(visiblePage.onboardingTourMainImage.drawable)
            }
        }
    }

    @Test
    fun `Check that TERMS and CONDITIONS are applied and handled correctly`() = testScope.runTest {
        val links = listOf("https://sbis.ru/help/integration", "https://sbis.ru/help/plugin/sbis3plugin")
        testProvider = appContext.buildSimpleTourProvider(
            termsCaption = R.string.onboarding_tour_terms_caption,
            termsLinks = links,
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                // Условия и положения видны
                assertTrue(fragment.binding.onboardingTourTerms.isVisible)
                assertThat(
                    fragment.binding.onboardingTourTerms.text,
                    instanceOf(SpannableString::class.java)
                )

                val spannable = fragment.binding.onboardingTourTerms.text as SpannableString
                val spans = spannable.getSpans<ClickableSpan>(0, spannable.length)
                // Условия и положения сформированы корректно
                assertEquals(links.size, spans.size)

                val spyRouter = spy(fragment.controller.router)
                fragment.controller.router = spyRouter
                spans.first().onClick(fragment.binding.onboardingTourTerms)
                testScheduler.advanceUntilIdle()
                // Действие по клику на Условия и положения
                verify(spyRouter).openInBrowserApp(eq("https://sbis.ru/help/integration"))
            }
        }
    }

    @Test
    fun `Check that only REQUIRED pages are displayed in the tour`() = testScope.runTest {
        testProvider = appContext.buildSimpleTourProvider(
            pageCount = 5,
            hiddenPositions = listOf(0, 1),
            dependency = OnboardingTourPlugin.dependencies
        )
        launch().onFragment { fragment ->
            testScheduler.advanceUntilIdle()
            launch {
                delay(UI_DELAY)
                assertEquals(3, fragment.binding.onboardingTourCarousel.count)
            }
        }
    }

    private fun launch(
        initialState: Lifecycle.State = Lifecycle.State.RESUMED,
        inContainer: Boolean = false
    ): FragmentScenario<TourFragment> = if (inContainer) {
        launchFragmentInContainer(
            themeResId = R.style.OnboardingTourTestAppTheme,
            factory = TourFactory(),
            initialState = initialState
        )
    } else {
        launchFragment(
            themeResId = R.style.OnboardingTourTestAppTheme,
            factory = TourFactory(),
            initialState = initialState
        )
    }

    private val TourFragment.binding: OnboardingTourFragmentBinding
        get(): OnboardingTourFragmentBinding = view.getField("binding")

    private val TourFragment.controller: TourController
        get(): TourController = getField("controller")

    private var TourController.router: TourRouter
        get(): TourRouter = getField("router")
        set(value) = setField("router", value)

    private inner class TourFactory : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            TourFragment()
    }

    private companion object {
        const val UI_DELAY = 300L
    }
}