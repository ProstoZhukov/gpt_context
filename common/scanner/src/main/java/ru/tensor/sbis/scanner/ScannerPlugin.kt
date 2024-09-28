package ru.tensor.sbis.scanner

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import ru.tensor.sbis.edo_decl.scanner.ScannerEventProvider
import ru.tensor.sbis.edo_decl.scanner.ScannerIntentProvider
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.scanner.contract.ScannerFeature
import ru.tensor.sbis.scanner.contract.ScannerFeatureImpl
import ru.tensor.sbis.scanner.di.ScannerSingletonComponentInitializer
import ru.tensor.sbis.scanner.files_picker_tab.ScannerTabProvider
import ru.tensor.sbis.storage.contract.ExternalStorageProvider

/**
 * Плагин для сканера
 *
 * @author kv.martyshenko
 */
object ScannerPlugin : BasePlugin<Unit>() {
    private val scannerFeature: ScannerFeature by lazy {
        ScannerFeatureImpl()
    }
    internal val singletonComponent by lazy {
        ScannerSingletonComponentInitializer(externalStorageProvider.get()).init(commonSingletonComponentProvider.get())
    }
    internal val analyticsUtil by lazy {
        analyticsUtilFeatureProvider?.get()?.getAnalyticsUtil()
    }

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var externalStorageProvider: FeatureProvider<ExternalStorageProvider>
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ScannerEventProvider::class.java) { scannerFeature },
        FeatureWrapper(ScannerIntentProvider::class.java) { scannerFeature },
        FeatureWrapper(SbisFilesPickerTabFeatureProvider::class.java, ::ScannerTabProvider)
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(ExternalStorageProvider::class.java) { externalStorageProvider = it }
        .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}