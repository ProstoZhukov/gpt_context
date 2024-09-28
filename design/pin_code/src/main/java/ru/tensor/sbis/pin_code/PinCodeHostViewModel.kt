package ru.tensor.sbis.pin_code

import androidx.lifecycle.SavedStateHandle
import ru.tensor.sbis.mvvm.StatefulViewModel
import ru.tensor.sbis.pin_code.decl.PinCodeAnchor
import ru.tensor.sbis.pin_code.decl.PinCodeSuccessResult
import ru.tensor.sbis.pin_code.decl.PinCodeUseCase
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl.Companion.ARG_CONFIRMATION_USE_CASE
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl.Companion.ARG_ON_CANCEL
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl.Companion.ARG_ON_RESULT
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl.Companion.ARG_POPOVER_ANCHOR
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl.Companion.ARG_USE_CASE

/**
 * Вью-модель хост-фрагмента.
 *
 * @author mb.kruglova
 */
class PinCodeHostViewModel<RESULT>(
    state: SavedStateHandle
) : StatefulViewModel(state) {

    val useCase: PinCodeUseCase by arg(ARG_USE_CASE)
    val popoverAnchor: PinCodeAnchor? by arg(ARG_POPOVER_ANCHOR)
    val confirmationUseCase: PinCodeUseCase? by arg(ARG_CONFIRMATION_USE_CASE)
    val onCancel: (() -> Unit)? by arg(ARG_ON_CANCEL)
    val onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)? by arg(ARG_ON_RESULT)
}