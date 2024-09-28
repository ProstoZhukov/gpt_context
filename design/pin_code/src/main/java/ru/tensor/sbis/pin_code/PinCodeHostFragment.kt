package ru.tensor.sbis.pin_code

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl
import ru.tensor.sbis.pin_code.util.findOrCreateViewModelHierarchical

/**
 * Хост-фрагмент фрагмента ввода пин-кода.
 * Пересоздает фрагмент ввода пин-кода при смене конфигурации.
 *
 * @author mb.kruglova
 */
internal class PinCodeHostFragment<RESULT> : Fragment() {
    private val feature: PinCodeFeatureImpl<RESULT> by lazy {
        findOrCreateViewModelHierarchical(this, findHostOwner()) {
            PinCodeFeatureImpl() { null }
        }
    }

    private val pinCodeHostViewModel: PinCodeHostViewModel<RESULT> by viewModels()

    private fun findHostOwner(): ViewModelStoreOwner {
        return try {
            requireParentFragment()
        } catch (e: IllegalStateException) {
            requireActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // В подтверждающий сценарий заложено пересоздание пин-код фрагмента,
        // для этого ему нужен всегда живой FragmentManager
        if (savedInstanceState == null || pinCodeHostViewModel.confirmationUseCase != null) {
            feature.showPinCodeFragment(
                this.requireActivity(),
                this.childFragmentManager,
                if (feature.isConfirmationFlow) {
                    pinCodeHostViewModel.confirmationUseCase
                } else {
                    pinCodeHostViewModel.useCase
                },
                pinCodeHostViewModel.popoverAnchor,
                if (feature.isConfirmationFlow) {
                    null
                } else {
                    pinCodeHostViewModel.confirmationUseCase
                },
                pinCodeHostViewModel.onCancel,
                pinCodeHostViewModel.onResult
            )
        }

        feature.completePinCodeEntering.observe(this) {
            remove()
        }

        feature.hidePinCodeFragment.observe(this) {
            PinCodeFragment.dismiss(this@PinCodeHostFragment.childFragmentManager)
            remove()
        }
    }

    private fun remove() {
        feature.isConfirmationFlow = false
        this@PinCodeHostFragment.parentFragmentManager.beginTransaction().remove(this@PinCodeHostFragment).commit()
    }
}