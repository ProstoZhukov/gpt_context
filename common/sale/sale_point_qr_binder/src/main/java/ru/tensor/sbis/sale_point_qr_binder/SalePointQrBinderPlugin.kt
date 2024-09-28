package ru.tensor.sbis.sale_point_qr_binder

import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderFeature
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.sale_point_qr_binder.internal.push.SalePointQrNotificationController
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.SalePointQrBinderActivity
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.SalePointQrBinderFragment
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.SalePointQrBinderFragmentDIContainer
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.model.SalePointBindInfo

/**
 * Реализация плагина для функционала привязки QR-кода.
 *
 * @author kv.martyshenko
 */
internal object InternalSalePointQrBinderPlugin :
    BasePlugin<Unit>(),
    SalePointQrBinderFragmentDIContainer.Provider {
    private lateinit var pushCenterProvider: FeatureProvider<PushCenter>
    private lateinit var barcodeReaderFeatureProvider: FeatureProvider<BarcodeReaderFeature>
    private lateinit var apiServiceProvider: FeatureProvider<ApiService.Provider>

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .require(PushCenter::class.java) { pushCenterProvider = it }
        .require(BarcodeReaderFeature::class.java) { barcodeReaderFeatureProvider = it }
        .require(ApiService.Provider::class.java) { apiServiceProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun doAfterInitialize() {
        super.doAfterInitialize()

        pushCenterProvider.get().registerNotificationController(
            PushType.SALE_POINT_BIND_QR_CODE,
            SalePointQrNotificationController(application) { context, model ->
                SalePointQrBinderActivity
                    .createIntent(
                        context,
                        SalePointBindInfo(
                            salePointId = model.salePointId,
                            bindUrl = model.bindUrl,
                            site = model.site,
                            objectType = model.objectType,
                            objectIdentifier = model.objectIdentifier,
                            hall = model.hall
                        )
                    )
            }

        )
    }

    override fun from(fragment: SalePointQrBinderFragment): SalePointQrBinderFragmentDIContainer {
        return object : SalePointQrBinderFragmentDIContainer {
            override val barcodeFeature: BarcodeReaderFeature
                get() = barcodeReaderFeatureProvider.get()
            override val apiServiceProvider: ApiService.Provider
                get() = this@InternalSalePointQrBinderPlugin.apiServiceProvider.get()

        }
    }

}

object SalePointQrBinderPlugin : Plugin<Unit> by InternalSalePointQrBinderPlugin