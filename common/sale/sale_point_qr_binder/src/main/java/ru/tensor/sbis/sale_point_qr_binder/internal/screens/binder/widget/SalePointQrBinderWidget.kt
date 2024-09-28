package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import ru.tensor.sbis.barcode_decl.barcodereader.Barcode
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeScanEventContainer
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.dialogs.CompleteDialogContract
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.dialogs.ErrorDialogContact
import ru.tensor.sbis.sale_point_qr_binder.R

/**
 * Компонент, обеспечивающий привязку QR-кода к точке продаж.
 *
 * @param rootView
 * @param progressBar
 * @param lifecycleOwner
 * @param fragmentManager
 * @param barcodeEventContainer
 * @param store
 * @param onCancel действие при отмене.
 * @param onComplete действеи по завершению привязки.
 *
 * @author kv.martyshenko
 */
internal class SalePointQrBinderWidget(
    private val rootView: View,
    private val progressBar: SbisLoadingIndicator,
    private val lifecycleOwner: LifecycleOwner,
    fragmentManager: FragmentManager,
    barcodeEventContainer: BarcodeScanEventContainer,
    private val store: SalePointQrBinderStore,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    private val errorDialogContract = ErrorDialogContact(
        lifecycleOwner,
        fragmentManager,
        onRepeat = { store.accept(SalePointQrBinderStore.Intent.TryAgain) },
        onCancel = onCancel
    )

    private val completeDialogContract = CompleteDialogContract(
        lifecycleOwner,
        fragmentManager,
        onComplete
    )

    init {
        barcodeEventContainer.listenBarcode(lifecycleOwner) { barcode ->
            store.accept(SalePointQrBinderStore.Intent.BindQrCode(barcode))
        }

        bindToStore()
    }

    private fun bindToStore() {
        val lifecycle = lifecycleOwner.lifecycle.asEssentyLifecycle()

        bind(
            lifecycle = lifecycle,
            mode = BinderLifecycleMode.CREATE_DESTROY
        ) {
            store.states.bindTo { state ->
                if (state.isBindingInProgress) {
                    progressBar.postDefaultDelayedVisible()
                } else {
                    progressBar.forceSetVisibility(View.GONE)
                }
            }
        }

        bind(
            lifecycle = lifecycle,
            mode = BinderLifecycleMode.RESUME_PAUSE
        ) {
            store.labels.bindTo {
                when (it) {
                    is SalePointQrBinderStore.Label.BindQrFailureLabel -> {
                        errorDialogContract.show(
                            title = null,
                            description = it.error.getString(rootView.context)
                        )
                    }
                    SalePointQrBinderStore.Label.BindQrCompleted -> {
                        completeDialogContract.show(
                            title = null,
                            description = rootView.context.getString(R.string.spqrb_bind_completed)
                        )
                    }
                }
            }
        }
    }

    private fun BarcodeScanEventContainer.listenBarcode(
        lifecycleOwner: LifecycleOwner,
        onBarcode: (Barcode) -> Unit
    ) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            private val scannerListener = object : BarcodeScanEventContainer.Listener {
                override fun onBarcodeEvent(barcode: Barcode) {
                    onBarcode(barcode)
                }
            }

            override fun onStart(owner: LifecycleOwner) {
                this@listenBarcode.listener = scannerListener
            }

            override fun onStop(owner: LifecycleOwner) {
                this@listenBarcode.listener = null
            }

            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

}