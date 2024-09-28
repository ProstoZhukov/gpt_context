package ru.tensor.sbis.onboarding.domain

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import io.reactivex.Observable
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.onboarding.domain.interactor.FeatureInteractor
import ru.tensor.sbis.onboarding.domain.provider.StringProvider

class FeatureInteractorTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private lateinit var interactor: FeatureInteractor

    private val mockRepository = mock<OnboardingRepository> {
        on { observe() } doReturn Observable.just(TestOnboarding.onboarding)
        on { getCachedContent() } doReturn TestOnboarding.onboarding
        on { findDeclaredPage("simple_page") } doReturn TestOnboarding.getSimplePage()
        on { findDeclaredPage("button_page") } doReturn TestOnboarding.getCustomButtonPage()
        on { findDeclaredPage("stub_page") } doReturn TestOnboarding.getStubPage()
    }
    private val mockStringProvider = mock<StringProvider> {
        on { getString(any()) } doReturn "text"
        on { findLongestString(any()) } doReturn "longest_text"
    }

    @Test
    fun `return valid state for page WITHOUT button`() {
        createInteractor("simple_page")
        val testOnNext = interactor.observePageState().test()
        testOnNext.assertNoErrors()
        testOnNext.assertValueCount(1)
        testOnNext.values().last().run {
            assertTrue(isFeature)
            assertFalse(hasButton)
            assertTrue(buttonText.isEmpty())
            assertNull(buttonAction)
        }
    }

    @Test
    fun `return valid state for page WITH button`() {
        createInteractor("button_page")
        val testOnNext = interactor.observePageState().test()
        testOnNext.assertNoErrors()
        testOnNext.assertValueCount(1)
        testOnNext.values().last().run {
            assertTrue(isFeature)
            assertTrue(buttonText.isNotEmpty())
            assertTrue(hasButton)
            assertNotNull(buttonAction)
        }
    }

    @Test
    fun `return valid state for stub page`() {
        createInteractor("stub_page")
        val testOnNext = interactor.observePageState().test()
        testOnNext.assertNoErrors()
        testOnNext.assertValueCount(1)
        testOnNext.values().last().run {
            assertFalse(isFeature)
            assertTrue(buttonText.isNotEmpty())
            assertTrue(hasButton)
        }
    }

    private fun createInteractor(uuid: String) {
        interactor = FeatureInteractor(
            uuid,
            mockRepository,
            mockStringProvider,
            mock(),
            mock()
        )
    }
}