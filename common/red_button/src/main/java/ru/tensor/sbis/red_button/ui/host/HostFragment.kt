package ru.tensor.sbis.red_button.ui.host

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import ru.tensor.sbis.common.util.DeviceConfigurationUtils.isTablet
import ru.tensor.sbis.common.util.di.withInjection
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.pin_code.decl.PinCodeFeature
import ru.tensor.sbis.pin_code.decl.PinCodeUseCase
import ru.tensor.sbis.pin_code.decl.createPinCodeFeature
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.RedButtonDependency
import ru.tensor.sbis.red_button.databinding.RedButtonFragmentPinHostBinding
import ru.tensor.sbis.red_button.di.RedButtonComponent
import ru.tensor.sbis.red_button.ui.host.data.PinConfirmationResult
import ru.tensor.sbis.red_button.ui.host.di.DaggerHostFragmentComponent
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon

/**
 * Фрагмент для размещения в своем контейнере экранов подтверждения красной кнопки
 *
 * @author ra.stepanov
 */
internal class HostFragment : Fragment() {

    private var pinCodeFeature: PinCodeFeature<PinConfirmationResult>? = null
    private val injector = withInjection {
        DaggerHostFragmentComponent.factory()
            .create(
                RedButtonComponent.get(requireContext()),
                this
            )
            .inject(this)
    }

    /** Вью модель */
    @Inject
    lateinit var viewModel: HostViewModel

    @Inject
    lateinit var dependency: RedButtonDependency

    /**
     * Открытие дочернего фрагмента с нужным режимом работы в зависимости от того включена ли "Красная Кнопка" и создание контейнера
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pinCodeFeature?.onRequestCheckCodeResult?.observe(viewLifecycleOwner, viewModel.onRequestCheckCodeHandler)
        pinCodeFeature?.onCanceled?.observe(viewLifecycleOwner, Observer {
            closeFragment()
        })
        viewModel.onCloseFragment.observe(viewLifecycleOwner, Observer { closeFragment() })
        viewModel.onCloseContent.observe(viewLifecycleOwner, Observer {
            (childFragmentManager.fragments.firstOrNull() as? ContainerBottomSheet)?.dismiss()
        })
        viewModel.onConfigurationChanged.observe(
            viewLifecycleOwner,
            Observer {
                pinCodeFeature?.show(
                    this,
                    PinCodeUseCase.Custom(it),
                    confirmationUseCase = if (viewModel.isPinCodeCreation) viewModel.pinCodeCreationConfirmationCase else null
                )
            })
        viewModel.onNeedVerifyPhone.observe(viewLifecycleOwner, Observer {
            showPhoneVerification()
        })
        viewModel.onError.observe(viewLifecycleOwner, Observer {
            PopupConfirmation.newSimpleInstance(DIALOG_CODE)
                .requestTitle(it)
                .requestPositiveButton(getString(RCommon.string.dialog_button_ok), false)
                .show(childFragmentManager, PopupConfirmation::class.java.simpleName)
        })
        return RedButtonFragmentPinHostBinding.inflate(inflater, container, false).root
    }

    /** @SelfDocumented */
    override fun onAttach(context: Context) {
        retainInstance = true
        injector.inject()
        pinCodeFeature = createPinCodeFeature(this) { viewModel }
        super.onAttach(context)
    }

    private fun showPhoneVerification() {
        childFragmentManager
            .beginTransaction()
            .addToBackStack(VERIFICATION_FRAGMENT_TAG)
            .add(R.id.container, dependency.createVerificationWithAlertFragment(), VERIFICATION_FRAGMENT_TAG)
            .commit()
    }

    private fun closeFragment() {
        pinCodeFeature = null
        if (!isTablet(requireContext())) {
            requireActivity().onBackPressed()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {

        /** Код для диалогового окна отображающего ошибку */
        const val DIALOG_CODE = 10011

        /** Тег фрагмента подтверждения номера телефона */
        var VERIFICATION_FRAGMENT_TAG = "${HostFragment::class.java.simpleName}_VERIFICATION_FRAGMENT_TAG"

        /**
         * Метод для создания данного фрагмента
         */
        @JvmStatic
        fun newInstance() = HostFragment()
    }
}