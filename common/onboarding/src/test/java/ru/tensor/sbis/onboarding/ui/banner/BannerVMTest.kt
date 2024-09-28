package ru.tensor.sbis.onboarding.ui.banner

import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import io.reactivex.Observable
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.onboarding.domain.interactor.BannerInteractor
import ru.tensor.sbis.onboarding.domain.interactor.usecase.BannerState

class BannerVMTest {
    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private lateinit var viewModel: BannerVM

    private var bannerState = BannerState()
    private val mockInteractor = mock<BannerInteractor> {
        on { observeBannerState() } doAnswer { Observable.just(bannerState) }
    }

    @Test
    fun `initialization required fields in banner view model`() {
        createVM(bannerState)
        assertNotNull(viewModel.titleRes.get())
        assertNotNull(viewModel.logoRes.get())
    }

    @Test
    fun `compress banner title if its gravity is from below in banner view model`() {
        createVM(BannerState(titleGravityBias = 1F))
        assertTrue(viewModel.compressed.get())
    }

    private fun createVM(stateVm: BannerState) {
        bannerState = stateVm
        viewModel = BannerVM(
            interactor = mockInteractor,
            router = mock(),
            intentProvider = mock()
        )
    }
}