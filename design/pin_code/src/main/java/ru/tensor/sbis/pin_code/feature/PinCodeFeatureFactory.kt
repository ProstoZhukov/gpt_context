package ru.tensor.sbis.pin_code.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.pin_code.decl.PinCodeRepository

/**
 * Фабрика создания вью-модели фичи
 *
 * @author mb.kruglova
 */
internal class PinCodeFeatureFactory<RESULT>(
    private val repositoryProducer: () -> PinCodeRepository<RESULT>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        require(modelClass == PinCodeFeatureImpl::class.java) {
            "Unsupported ViewModel type $modelClass"
        }
        return PinCodeFeatureImpl(repositoryProducer) as VM
    }
}