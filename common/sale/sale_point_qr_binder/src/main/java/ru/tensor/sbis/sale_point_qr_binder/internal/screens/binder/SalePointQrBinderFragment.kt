package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.args.KeySpec
import ru.tensor.sbis.android_ext_decl.args.getKeySpec
import ru.tensor.sbis.android_ext_decl.args.nonNull
import ru.tensor.sbis.android_ext_decl.args.parcelable
import ru.tensor.sbis.android_ext_decl.args.putKeySpec
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderParams
import ru.tensor.sbis.barcode_decl.barcodereader.ManualInputStrategy
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.sale_point_qr_binder.InternalSalePointQrBinderPlugin
import ru.tensor.sbis.sale_point_qr_binder.databinding.SpqrbFragmentSalePointQrScannerBinding
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.model.SalePointBindInfo
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.SalePointQrBinderStore
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.SalePointQrBinderWidget
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.create

/**
 * Реализация [Fragment] для сканирования QR-кода и привязки его к ТП.
 *
 * @author kv.martyshenko
 */
internal class SalePointQrBinderFragment @JvmOverloads constructor(
    diProvider: SalePointQrBinderFragmentDIContainer.Provider = InternalSalePointQrBinderPlugin
) : BaseFragment() {
    private val diContainer by lazy { diProvider.from(this) }

    private val barcodeFeature by lazy { diContainer.barcodeFeature }

    private val salePointBindInfo by lazy {
        requireArguments().getKeySpec(salePointBindSpec)
    }

    // region Fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = SpqrbFragmentSalePointQrScannerBinding
            .inflate(inflater, container, false)

        if (savedInstanceState == null) {
            showBarcodeScannerScreen(binding.scannerContainer.id)
        }

        SalePointQrBinderWidget(
            binding.root,
            binding.scannerProgress,
            viewLifecycleOwner,
            childFragmentManager,
            barcodeFeature.getBarcodeScanEventContainer(requireContext()),
            provideStore(key = "${salePointBindInfo.bindUrl}_${salePointBindInfo.salePointId}") { stateKeeper ->
                SalePointQrBinderStore.create(
                    salePointBindInfo,
                    stateKeeper,
                    diContainer.apiServiceProvider
                )
            },
            onCancel = {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            },
            onComplete = {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        )

        return binding.root
    }
    // endregion

    private fun showBarcodeScannerScreen(containerId: Int) {
        childFragmentManager.beginTransaction()
            .add(
                containerId,
                barcodeFeature.createBarcodeReaderHostFragment(
                    params = BarcodeReaderParams(
                        manualInputStrategy = ManualInputStrategy.NONE
                    )
                )
            )
            .commitNow()
    }

    companion object {
        private val salePointBindSpec = KeySpec.parcelable<SalePointBindInfo>("sale_point_bind_info").nonNull()

        fun newInstance(
            bindInfo: SalePointBindInfo
        ): Fragment {
            return SalePointQrBinderFragment().apply {
                arguments = Bundle().apply {
                    putKeySpec(salePointBindSpec, bindInfo)
                }
            }
        }
    }

}