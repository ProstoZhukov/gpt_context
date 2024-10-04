package ru.tensor.sbis.pin_code

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl

/**
 * Фабрика создания вью-модели фрагмента ввода пин-кода.
 *
 * @author mb.kruglova
 */
internal class PinCodeVmFactory<RESULT>(
    private val fragment: Fragment,
    private val pinCodeFeatureImpl: PinCodeFeatureImpl<RESULT>?
) : AbstractSavedStateViewModelFactory(fragment, fragment.requireArguments()) {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(
        key: String,
        modelClass: Class<VM>,
        state: SavedStateHandle
    ): VM {
        require(modelClass == PinCodeViewModel::class.java) {
            "Unsupported ViewModel type $modelClass"
        }
        val appContext = fragment.requireContext().applicationContext
        return PinCodeViewModel(pinCodeFeatureImpl, appContext = appContext, state = state) as VM
    }
}