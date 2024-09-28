package ru.tensor.sbis.logging.log_packages.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.di.withInjection
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubFactoryOneForAll
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.databinding.LoggingPackagesFragmentBinding
import ru.tensor.sbis.logging.log_packages.data.Crud3LogServiceWrapper
import ru.tensor.sbis.logging.log_packages.di.DaggerLogComponent
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.platform.logdelivery.generated.LogCollectionProvider

/**
 * Экран для показа списка логов с кнопкой отправки. Обрабатывает результат диалог подтверждения действия отправки логов.
 */
class LogPackagesFragment : Fragment(), PopupConfirmation.DialogYesNoWithTextListener {

    private lateinit var screenViewModel: LogPackageViewModel
    private lateinit var logPackageViewHolderCallback: LogPackageViewHolderCallback
    private lateinit var clipboardCopier: ClipboardCopier

    @Suppress("unused", "LeakingThis")
    val injector = withInjection {
        val component = DaggerLogComponent.factory().create(this)
        screenViewModel = component.provideLogPackageViewModel()
        logPackageViewHolderCallback = component.logPackageViewHolderCallback()
        clipboardCopier = component.clipboardCopier()
    }

    /** @SelfDocumented */
    override fun onAttach(context: Context) {
        injector.inject()
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        @Suppress("DEPRECATION")
        retainInstance = true
        screenViewModel.toastMsg.observe(viewLifecycleOwner) { message ->
            message?.also {
                SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message)
            }
        }

        val factory = object : StubFactory {
            override fun create(type: StubType) = ImageStubContent(
                imageType = StubViewImageType.EMPTY,
                messageRes = R.string.logging_disable_log_stub_title,
                detailsRes = R.string.logging_disable_log_stub_description
            )
        }

        return LoggingPackagesFragmentBinding
            .inflate(inflater)
            .also {
                it.viewModel = screenViewModel
                it.lifecycleOwner = viewLifecycleOwner

                it.loggingFloatingContainer.setOnClickListener {
                    screenViewModel.onSendLog()
                }
                it.loggingListComponent.inject(
                    this,
                    lazy { Crud3LogServiceWrapper(LogCollectionProvider.instance()) },
                    lazy { Mapper(logPackageViewHolderCallback, clipboardCopier) },
                    lazy { StubFactoryOneForAll(factory) }
                )
            }
            .root
    }

    override fun onYes(requestCode: Int, text: String?) {
        if (requestCode == LOGGING_CONFIRM_REQUEST_CODE) {
            screenViewModel.sendLogWithoutConfirmation()
        }
    }
}