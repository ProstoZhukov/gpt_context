package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder

import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderFeature
import ru.tensor.sbis.network_native.apiservice.contract.ApiService

/**
 * Внешние зависимости для [SalePointQrBinderFragment].
 *
 * @author kv.martyshenko
 */
internal interface SalePointQrBinderFragmentDIContainer {
    val barcodeFeature: BarcodeReaderFeature
    val apiServiceProvider: ApiService.Provider

    interface Provider {
        fun from(fragment: SalePointQrBinderFragment): SalePointQrBinderFragmentDIContainer
    }
}