package ru.tensor.sbis.wizard.impl

import android.os.Parcelable
import dagger.Reusable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.wizard.decl.WizardSteps
import ru.tensor.sbis.wizard.decl.result.FinalStepResultData
import ru.tensor.sbis.wizard.decl.result.PreviousStepResult
import ru.tensor.sbis.wizard.decl.result.StepResult
import ru.tensor.sbis.wizard.decl.result.WizardResult
import ru.tensor.sbis.wizard.decl.step.Step
import ru.tensor.sbis.wizard.decl.step.StepHolder
import ru.tensor.sbis.wizard.impl.router.WizardRouter
import ru.tensor.sbis.wizard.impl.state.StateProviderRegistry
import ru.tensor.sbis.wizard.impl.state.StateRestorer
import ru.tensor.sbis.wizard.impl.state.StepMetadata
import ru.tensor.sbis.wizard.impl.state.WizardPresenterState
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Презентер мастера
 *
 * @author sa.nikitin
 */
@Reusable
internal class WizardPresenter @Inject constructor(
    private val steps: WizardSteps,
    private val router: WizardRouter,
    stateProviderRegistry: StateProviderRegistry<WizardPresenterState>,
    stateRestorer: StateRestorer<WizardPresenterState>
) : StepHolder, BackNavigationEventHandler {

    private val stepRouteDisposable = SerialDisposable()
    private val stepResultDisposable = SerialDisposable()
    private val finishDisposable = SerialDisposable()

    private val state: WizardPresenterState = stateRestorer.restore() ?: WizardPresenterState()
    private val stepResults: MutableList<Parcelable> get() = state.result.stepResults

    init {
        stateProviderRegistry.registerStateProvider(::state)
        state.wizardResult
            ?.let(::finish)
            ?: state.currentStepMetadata?.let { subscribeToStepResult(it.step) }
            ?: toNextStep()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <S : Step> getStep(stepClass: KClass<S>): S? =
        if (stepClass.isInstance(state.currentStepMetadata?.step)) {
            state.currentStepMetadata?.step as S
        } else {
            unexpectedStepRequest(stepClass)
            null
        }

    override fun handleBackNavigationEvent() {
        state.currentStepMetadata
            ?.let { stepMetadata ->
                when (router.dispatchBackNavigationEventToStepFragment(stepMetadata.tag)) {
                    null -> Unit
                    true -> Unit
                    false -> toPreviousStep()
                }
            }
            ?: kotlin.run { toPreviousStep() }
    }

    fun release() {
        stepRouteDisposable.dispose()
        stepResultDisposable.dispose()
        finishDisposable.dispose()
    }

    private fun toNextStep() {
        val step: Step? = steps.step(state.result, previousStepResult = null)
        if (step != null) {
            val nextStepMetadata = StepMetadata(step, stepResults.size)
            stepRouteDisposable.set(
                router
                    .toStep(fragmentFactory = step, nextStepTag = nextStepMetadata.tag)
                    .subscribe {
                        state.currentStepMetadata = nextStepMetadata
                        subscribeToStepResult(step)
                    }
            )
        } else {
            finish(WizardResult.Complete(state.result.stepResults.lastOrNull()))
        }
    }

    private fun toNextStep(currentStepResult: StepResult.Success) {
        val currentStepMetadata = state.currentStepMetadata
        if (currentStepMetadata != null && currentStepMetadata.number == stepResults.size) {
            if (currentStepResult.data is FinalStepResultData) {
                finish(WizardResult.Complete(currentStepResult.data))
            } else {
                stepResults.add(currentStepResult.data)
                toNextStep()
            }
        } else {
            unexpectedCurrentStepNumber(currentStepMetadata)
            finish(WizardResult.Error())
        }
    }

    private fun toPreviousStep(backInitiatorStepData: Parcelable? = null) {
        val currentStepMetadata = state.currentStepMetadata
        if (currentStepMetadata != null && currentStepMetadata.number > 0) {
            val lastCompletedStepNumber: Int = currentStepMetadata.number - 1
            val lastCompletedStepResult: Parcelable =
                if (stepResults.lastIndex == lastCompletedStepNumber) {
                    stepResults.removeAt(lastCompletedStepNumber)
                } else {
                    unexpectedLastCompletedStepNumber(lastCompletedStepNumber)
                    return
                }
            val lastCompletedStep: Step? =
                steps.step(
                    result = state.result,
                    previousStepResult = PreviousStepResult(lastCompletedStepResult, backInitiatorStepData)
                )
            if (lastCompletedStep != null) {
                val lastCompletedStepMetadata = StepMetadata(lastCompletedStep, stepResults.size)
                stepRouteDisposable.set(
                    router
                        .toPreviousStep(
                            fragmentFactory = lastCompletedStep,
                            previousStepTag = lastCompletedStepMetadata.tag
                        )
                        .subscribe {
                            state.currentStepMetadata = lastCompletedStepMetadata
                            subscribeToStepResult(lastCompletedStep)
                        }
                )
            } else {
                finish(WizardResult.Back())
            }
        } else {
            finish(WizardResult.Back())
        }
    }

    private fun subscribeToStepResult(step: Step) {
        stepResultDisposable.set(
            step.result
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onStepResult, ::unexpectedStepResultSubscriptionError)
        )
    }

    private fun onStepResult(stepResult: StepResult) {
        when (stepResult) {
            is StepResult.Success -> toNextStep(stepResult)
            is StepResult.Back -> toPreviousStep(stepResult.backInitiatorStepData)
            is StepResult.Cancel -> finish(WizardResult.Cancel())
        }
    }

    private fun finish(result: WizardResult) {
        state.wizardResult = result
        finishDisposable.set(router.finish(result).subscribe())
    }

    private fun <S : Step> unexpectedStepRequest(stepClass: KClass<S>) {
        illegalState { "Unexpected $stepClass request. Current step metadata - ${state.currentStepMetadata}" }
    }

    private fun unexpectedLastCompletedStepNumber(number: Int) {
        illegalState { "Unexpected last completed step number $number" }
    }

    private fun unexpectedStepMissingOnBackNavigation() {
        illegalState { "Unexpected step missing when back navigation" }
    }

    private fun unexpectedCurrentStepNumber(currentStepMetadata: StepMetadata?) {
        illegalState { "Unexpected current step number, metadata - $currentStepMetadata" }
    }

    private fun unexpectedStepResultSubscriptionError(error: Throwable) {
        illegalState { "Unexpected step result subscription error $error" }
    }
}