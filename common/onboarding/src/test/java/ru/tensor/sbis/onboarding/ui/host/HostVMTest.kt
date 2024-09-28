package ru.tensor.sbis.onboarding.ui.host

import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.onboarding.domain.interactor.HostInteractor
import ru.tensor.sbis.onboarding.domain.interactor.usecase.HostState
import ru.tensor.sbis.onboarding.ui.banner.BannerVM

class HostVMTest {
    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private lateinit var viewModel: OnboardingHostVM

    private var hostState = HostState()
    private val mockInteractor = mock<HostInteractor> {
        on { observeHostState() } doAnswer { Observable.just(hostState) }
    }
    private val mockBanner = mock<BannerVM>()

    @Test
    fun `initialization required state fields in host view model`() {
        createVM(HostState(true, true))
        assertTrue(viewModel.isPreventBack.get())
    }

    @After
    fun tearDown() {
        hostState = HostState()
    }

    private fun createVM(stateVm: HostState) {
        hostState = stateVm
        viewModel = OnboardingHostVM(
            mockBanner,
            mockInteractor,
            mock(),
            mock(),
            mock(),
            mock(),
            mock()
        )
    }
}