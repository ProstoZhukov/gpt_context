package ru.tensor.sbis.onboarding.ui.page

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.only
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.onboarding.domain.interactor.FeatureInteractor
import ru.tensor.sbis.onboarding.domain.interactor.usecase.PageState
import ru.tensor.sbis.onboarding.ui.banner.BannerVM
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostRouter

class FeatureVMTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private lateinit var viewModel: OnboardingFeatureVM

    private var pageState = PageState()
    private val mockInteractor = mock<FeatureInteractor> {
        on { observePageState() } doAnswer { Observable.just(pageState) }
    }
    private val mockBanner = mock<BannerVM>()
    private val mockRouter = mock<OnboardingHostRouter>()
    private val buttonAction = spy<(() -> Unit)> {}

    @Test
    fun `Initialization required state fields in feature view model`() {
        createVM(pageState)
        assertNotNull(viewModel.description.get())
        assertNotNull(viewModel.image.get())
    }

    @Test
    fun `Start user action by click custom button in feature view model`() {
        val state = PageState(buttonAction = buttonAction)
        createVM(state)

        viewModel.onButtonClick()
        verify(mockRouter, never()).dismiss(any())
        verify(buttonAction, only()).invoke()
    }

    @After
    fun tearDown() {
        pageState = PageState()
    }

    private fun createVM(stateVm: PageState) {
        pageState = stateVm
        viewModel = OnboardingFeatureVM(
            mockBanner,
            mockInteractor,
            mockRouter
        )
    }
}