package ru.tensor.sbis.frescoutils

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.facebook.animated.giflite.GifDecoder
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imageformat.DefaultImageFormats
import com.facebook.imagepipeline.cache.CacheKeyFactory
import com.facebook.imagepipeline.core.DefaultExecutorSupplier
import com.facebook.imagepipeline.core.ExecutorSupplier
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImageTranscoderType
import com.facebook.imagepipeline.core.MemoryChunkType
import com.facebook.imagepipeline.decoder.ImageDecoderConfig
import com.facebook.imagepipeline.memory.PoolConfig
import com.facebook.imagepipeline.memory.PoolFactory
import com.facebook.imagepipeline.nativecode.ImagePipelineNativeLoader
import com.facebook.imagepipeline.producers.NetworkFetcher
import timber.log.Timber
import java.io.File

typealias FrescoNetworkFetcherProvider = (Application, ExecutorSupplier) -> NetworkFetcher<*>
typealias FrescoCacheDirProvider = (Application) -> File
typealias FrescoPostInitializer = (Application, ImagePipelineConfig, DraweeConfig) -> Unit

fun initializeFresco(
    application: Application,
    nativeCodeEnabled: Boolean,
    networkFetcherProvider: FrescoNetworkFetcherProvider,
    cacheDirProvider: FrescoCacheDirProvider,
    cacheSize: Int,
    cacheKeyFactory: CacheKeyFactory = FrescoHostIndependentKeyFactory,
    postInitializer: FrescoPostInitializer?
) {
    val pipelineBuilder: ImagePipelineConfig.Builder = ImagePipelineConfig.newBuilder(application)

    val poolFactory = PoolFactory(PoolConfig.newBuilder().build())
    val executorSupplier = DefaultExecutorSupplier(poolFactory.flexByteArrayPoolMaxNumThreads)

    pipelineBuilder.setPoolFactory(poolFactory)
    pipelineBuilder.setExecutorSupplier(executorSupplier)
    pipelineBuilder.setNetworkFetcher(networkFetcherProvider.invoke(application, executorSupplier))

    val memoryRegistry = FrescoMemoryRegistry()
    val memoryTrimmer = FrescoMemoryTrimmer(memoryRegistry)
    pipelineBuilder.setMemoryTrimmableRegistry(memoryRegistry)
    application.registerComponentCallbacks(
        object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) = Unit

            override fun onLowMemory() = Unit

            override fun onTrimMemory(level: Int) {
                memoryTrimmer.onTrimMemory(level)
            }
        }
    )

    val draweeConfig: DraweeConfig =
        DraweeConfig.newBuilder()
            .addCustomDrawableFactory(FrescoSvgDecoder.SvgDrawableFactory())
            .build()

    val imageDecoderConfig: ImageDecoderConfig.Builder =
        ImageDecoderConfig.newBuilder()
            .addDecodingCapability(
                FrescoSvgDecoder.SVG_FORMAT,
                FrescoSvgDecoder.SvgFormatChecker(),
                FrescoSvgDecoder.SvgDecoder(application.resources)
            )

    if (!nativeCodeEnabled) {
        /*
            При распространении приложения через AppBundle необходимо использовать JAVA реализации вместо Native.
            В противном случае получаем краш SoLoader.

            https://online.sbis.ru/opendoc.html?guid=26157e94-0ce0-4a4c-b206-c13e9eb6df3c&client=3
        */
        imageDecoderConfig.overrideDecoder(DefaultImageFormats.GIF, GifDecoder())
    }

    pipelineBuilder.setImageDecoderConfig(imageDecoderConfig.build())
    pipelineBuilder.setCacheKeyFactory(cacheKeyFactory)
    pipelineBuilder.isDownsampleEnabled = true
    pipelineBuilder.setMainDiskCacheConfig(
        createDiskCacheConfig(
            application = application,
            cacheDirProvider = cacheDirProvider,
            cacheSizeInMB = cacheSize
        )
    )

    var pipelineConfig: ImagePipelineConfig

    if (nativeCodeEnabled) {
        pipelineConfig = pipelineBuilder.build()
        Fresco.initialize(application, pipelineConfig, draweeConfig)
        //Решение взято тут: https://github.com/facebook/fresco/issues/2381#issuecomment-516825599
        //Новая версия Fresco с исправлением похоже ещё не вышла
        try {
            ImagePipelineNativeLoader.load()
        } catch (error: UnsatisfiedLinkError) {
            Fresco.shutDown()
            pipelineBuilder.experiment().setNativeCodeDisabled(true)
            pipelineConfig = pipelineBuilder.build()
            Fresco.initialize(application, pipelineConfig, draweeConfig)
            Timber.e(error)
        }
    } else {
        // При упаковке приложения в app bundle, возникает краш SoLoader при использовании nativeCode
        // https://online.sbis.ru/opendoc.html?guid=f0a9d91a-6197-4972-8b5a-98fb00f75a1e
        pipelineConfig = pipelineBuilder
            .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
            .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
            .experiment().setNativeCodeDisabled(true)
            .build()
        Fresco.initialize(application, pipelineConfig, draweeConfig, false)
    }

    logPipelineParameters(pipelineConfig)
    postInitializer?.invoke(application, pipelineConfig, draweeConfig)
}

private fun createDiskCacheConfig(
    application: Application,
    cacheDirProvider: (Application) -> File,
    cacheSizeInMB: Int
): DiskCacheConfig {
    val cacheSizeInBytes: Long = cacheSizeInMB * ByteConstants.MB.toLong()
    return DiskCacheConfig.newBuilder(application)
        .setMaxCacheSize(cacheSizeInBytes)
        .setMaxCacheSizeOnLowDiskSpace(cacheSizeInBytes / 4)
        .setMaxCacheSizeOnVeryLowDiskSpace(cacheSizeInBytes / 20)
        .setBaseDirectoryPath(cacheDirProvider(application))
        .build()
}

private fun logPipelineParameters(pipelineConfig: ImagePipelineConfig) {
    Timber.i(
        """
            Размеры кэшей Fresco:
                Memory (Bitmap): %s MB
                Disk (default): %s MB
                Disk (low space): %s MB
                Disk (very low space): %s MB
            """.trimIndent(),
        pipelineConfig.bitmapMemoryCacheParamsSupplier.get().maxCacheSize / ByteConstants.MB,
        pipelineConfig.mainDiskCacheConfig.defaultSizeLimit / ByteConstants.MB,
        pipelineConfig.mainDiskCacheConfig.lowDiskSpaceSizeLimit / ByteConstants.MB,
        pipelineConfig.mainDiskCacheConfig.minimumSizeLimit / ByteConstants.MB,
    )
}