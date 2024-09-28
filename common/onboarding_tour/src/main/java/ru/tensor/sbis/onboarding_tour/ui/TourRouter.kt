package ru.tensor.sbis.onboarding_tour.ui

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.tensor.sbis.common.util.openInBrowserApp
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.mvi_extension.router.fragment.allowPopBackStack
import ru.tensor.sbis.onboarding_tour.ui.di.TourFragmentScope
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.RationaleCallback
import javax.inject.Inject

/**
 * Роутер тура онбординга.
 */
@TourFragmentScope
internal class TourRouter @Inject constructor() : FragmentRouter() {

    private var rationalJob: Job? = null

    /** @SelfDocumented */
    fun closeTour() = execute {
        if (parentFragmentManager.allowPopBackStack()) {
            parentFragmentManager.popBackStack()
        }
    }

    /** @SelfDocumented */
    fun openInBrowserApp(url: String) = execute {
        openInBrowserApp(requireContext(), url)
    }

    /** @SelfDocumented */
    fun performRationale(
        permissions: List<String>,
        command: RationaleCallback,
        resultConsumer: (Boolean) -> Unit
    ) = execute {
        rationalJob?.cancel()
        val flow: Flow<Boolean> = command(this, permissions)
        rationalJob = lifecycleScope.launchWhenStarted {
            flow.first { isCompleted ->
                resultConsumer(isCompleted)
                true
            }
        }
    }

    /** @SelfDocumented */
    fun cancelCommandContinuation() {
        rationalJob?.cancel()
    }
}
