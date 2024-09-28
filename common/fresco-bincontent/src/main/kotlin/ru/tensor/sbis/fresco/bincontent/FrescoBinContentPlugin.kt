package ru.tensor.sbis.fresco.bincontent

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilderSupplier
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.core.ImagePipelineFactory
import ru.tensor.sbis.common.util.safeThrow
import ru.tensor.sbis.desktop.bincontent.generated.BinContentService
import ru.tensor.sbis.frescoutils.initializeFresco
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import java.lang.reflect.Field

/**
 * Плагин для просмотра картинок на базе библиотеки [Fresco] с микросервисом бинарного контента [BinContentService]
 *
 * Будет переопределена работа с сетью [BinContentNetworkFetcher] и с дисковым кэшем [BinContentDiskCache]
 *
 * @author sa.nikitin
 */
object FrescoBinContentPlugin : BasePlugin<FrescoBinContentPluginCustomizationOptions>() {

    override val api: Set<FeatureWrapper<out Feature>> get() = emptySet()

    override val dependency: Dependency get() = Dependency.EMPTY

    override val customizationOptions: FrescoBinContentPluginCustomizationOptions =
        FrescoBinContentPluginCustomizationOptions()

    override fun doAfterInitialize() {
        val binContentService: Lazy<BinContentService> = lazy(BinContentService::instance)
        initializeFresco(
            application,
            nativeCodeEnabled = customizationOptions.isNativeCodeEnabled,
            networkFetcherProvider = { _, executorSupplier ->
                BinContentNetworkFetcher(binContentService, executorSupplier.forLightweightBackgroundTasks())
            },
            cacheDirProvider = customizationOptions.cacheDirProvider,
            cacheSize = customizationOptions.cacheSizeInMB,
            postInitializer = { application, pipelineConfig, draweeConfig ->
                //Инициализируем BinContentPipelineFactory, она станет статичным экземпляром ImagePipelineFactory
                ImagePipelineFactory.setInstance(BinContentPipelineFactory(pipelineConfig, binContentService))
                try {
                    //Переустанавливаем Fresco.sDraweeControllerBuilderSupplier, т.к. обновилась ImagePipelineFactory
                    //Это возможно только через рефлексию
                    val controllerBuilderSupplier = PipelineDraweeControllerBuilderSupplier(application, draweeConfig)
                    val supplierField: Field = Fresco::class.java.getDeclaredField("sDraweeControllerBuilderSupplier")
                    supplierField.isAccessible = true
                    supplierField.set(null, controllerBuilderSupplier)
                    //Инициализируем SimpleDraweeView с новым PipelineDraweeControllerBuilderSupplier
                    SimpleDraweeView.initialize(controllerBuilderSupplier)
                } catch (exception: Exception) {
                    safeThrow(exception)
                }
            }
        )
    }
}