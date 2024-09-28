package ru.tensor.sbis.fresco.bincontent

import android.content.Context
import android.os.Build
import com.facebook.imageformat.ImageFormat
import com.facebook.imageformat.ImageFormatChecker
import com.facebook.imagepipeline.animated.factory.AnimatedFactory
import com.facebook.imagepipeline.animated.factory.AnimatedFactoryProvider
import com.facebook.imagepipeline.cache.BufferedDiskCache
import com.facebook.imagepipeline.core.*
import com.facebook.imagepipeline.decoder.DefaultImageDecoder
import com.facebook.imagepipeline.decoder.ImageDecoder
import com.facebook.imagepipeline.drawable.DrawableFactory
import com.facebook.imagepipeline.producers.ExperimentalThreadHandoffProducerQueueImpl
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueueImpl
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory
import com.facebook.imagepipeline.transcoder.MultiImageTranscoderFactory
import com.facebook.imagepipeline.transcoder.SimpleImageTranscoderFactory
import ru.tensor.sbis.desktop.bincontent.generated.BinContentService

/**
 * Реализация [ImagePipelineFactory], собирающая [ImagePipeline] с элементами, взаимодействующими с [BinContentService]
 * Ключевыми являются [mainDiskCache] и [smallDiskCache]
 *
 * @author sa.nikitin
 */
internal class BinContentPipelineFactory(
    private val config: ImagePipelineConfig,
    private val binContentService: Lazy<BinContentService>
) : ImagePipelineFactory(config) {

    private val binContentImagePipeline: ImagePipeline by lazy(::createImagePipeline)
    private val mainDiskCache: BinContentDiskCache by lazy(::createMainDiskCache)
    private val smallDiskCache: BinContentDiskCache by lazy(::createSmallDiskCache)
    private val threadHandoffProducerQueue: ThreadHandoffProducerQueue by lazy(::createThreadHandoffProducerQueue)
    private val producerSequenceFactory: ProducerSequenceFactory by lazy(::createProducerSequenceFactory)
    private val producerFactory: ProducerFactory by lazy(::createProducerFactory)
    private val imageDecoder: ImageDecoder by lazy(::createImageDecoder)
    private val animatedFactory: AnimatedFactory? by lazy(::createAnimatedFactory)
    private val imageTranscoderFactory: ImageTranscoderFactory by lazy(::createImageTranscoderFactory)

    override fun getImagePipeline(): ImagePipeline = binContentImagePipeline

    override fun getMainBufferedDiskCache(): BufferedDiskCache = mainDiskCache

    override fun getAnimatedDrawableFactory(context: Context?): DrawableFactory? =
        animatedFactory?.getAnimatedDrawableFactory(context)

    private fun createImagePipeline(): ImagePipeline =
        ImagePipeline(
            producerSequenceFactory,
            config.requestListeners,
            config.requestListener2s,
            config.isPrefetchEnabledSupplier,
            bitmapMemoryCache,
            encodedMemoryCache,
            mainDiskCache,
            smallDiskCache,
            config.cacheKeyFactory,
            threadHandoffProducerQueue,
            config.experiments.suppressBitmapPrefetchingSupplier,
            config.experiments.isLazyDataSource,
            config.callerContextVerifier,
            config
        )

    private fun createMainDiskCache(): BinContentDiskCache =
        BinContentDiskCache(
            binContentService,
            mainFileCache,
            config.poolFactory.getPooledByteBufferFactory(config.memoryChunkType),
            config.poolFactory.pooledByteStreams,
            config.executorSupplier.forLocalStorageRead(),
            config.executorSupplier.forLocalStorageWrite(),
            config.imageCacheStatsTracker
        )

    private fun createSmallDiskCache(): BinContentDiskCache =
        BinContentDiskCache(
            binContentService,
            smallImageFileCache,
            config.poolFactory.getPooledByteBufferFactory(config.memoryChunkType),
            config.poolFactory.pooledByteStreams,
            config.executorSupplier.forLocalStorageRead(),
            config.executorSupplier.forLocalStorageWrite(),
            config.imageCacheStatsTracker
        )

    private fun createThreadHandoffProducerQueue(): ThreadHandoffProducerQueue =
        if (config.experiments.isExperimentalThreadHandoffQueueEnabled) {
            ExperimentalThreadHandoffProducerQueueImpl(config.executorSupplier.forLightweightBackgroundTasks())
        } else {
            ThreadHandoffProducerQueueImpl(config.executorSupplier.forLightweightBackgroundTasks())
        }

    private fun createProducerSequenceFactory(): ProducerSequenceFactory {
        val useBitmapPrepareToDraw =
            //before Android N the Bitmap#prepareToDraw method is no-op so do not need this
            config.experiments.useBitmapPrepareToDraw && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        return ProducerSequenceFactory(
            config.context.applicationContext.contentResolver,
            producerFactory,
            config.networkFetcher,
            config.isResizeAndRotateEnabledForNetwork,
            config.experiments.isWebpSupportEnabled,
            threadHandoffProducerQueue,
            config.isDownsampleEnabled,
            useBitmapPrepareToDraw,
            config.experiments.isPartialImageCachingEnabled,
            config.isDiskCacheEnabled,
            imageTranscoderFactory,
            config.experiments.isEncodedMemoryCacheProbingEnabled,
            config.experiments.isDiskCacheProbingEnabled,
            config.experiments.allowDelay()
        )
    }

    private fun createProducerFactory(): ProducerFactory =
        config
            .experiments
            .producerFactoryMethod
            .createProducerFactory(
                config.context,
                config.poolFactory.smallByteArrayPool,
                imageDecoder,
                config.progressiveJpegConfig,
                config.isDownsampleEnabled,
                config.isResizeAndRotateEnabledForNetwork,
                config.experiments.isDecodeCancellationEnabled,
                config.executorSupplier,
                config.poolFactory.getPooledByteBufferFactory(config.memoryChunkType),
                config.poolFactory.pooledByteStreams,
                bitmapMemoryCache,
                encodedMemoryCache,
                mainDiskCache,
                smallDiskCache,
                config.cacheKeyFactory,
                platformBitmapFactory,
                config.experiments.bitmapPrepareToDrawMinSizeBytes,
                config.experiments.bitmapPrepareToDrawMaxSizeBytes,
                config.experiments.bitmapPrepareToDrawForPrefetch,
                config.experiments.maxBitmapSize,
                closeableReferenceFactory,
                config.experiments.shouldKeepCancelledFetchAsLowPriority(),
                config.experiments.trackedKeysSize
            )

    private fun createImageDecoder(): ImageDecoder = config.imageDecoder ?: run {
        val gifDecoder = animatedFactory?.gifDecoder
        val webPDecoder = animatedFactory?.webPDecoder
        val imageDecoderConfig = config.imageDecoderConfig
        val customImageFormats: List<ImageFormat.FormatChecker>?
        val customImageDecoders: Map<ImageFormat, ImageDecoder>?
        if (imageDecoderConfig == null) {
            customImageFormats = null
            customImageDecoders = null
        } else {
            customImageFormats = imageDecoderConfig.customImageFormats
            customImageDecoders = imageDecoderConfig.customImageDecoders
        }
        ImageFormatChecker.getInstance().setCustomImageFormatCheckers(customImageFormats)
        DefaultImageDecoder(gifDecoder, webPDecoder, platformDecoder, customImageDecoders)
    }

    private fun createAnimatedFactory(): AnimatedFactory? =
        AnimatedFactoryProvider.getAnimatedFactory(
            platformBitmapFactory,
            config.executorSupplier,
            bitmapCountingMemoryCache,
            config.experiments.shouldDownscaleFrameToDrawableDimensions(),
            config.executorServiceForAnimatedImages
        )

    private fun createImageTranscoderFactory(): ImageTranscoderFactory =
        if (config.imageTranscoderFactory == null &&
            config.imageTranscoderType == null &&
            config.experiments.isNativeCodeDisabled
        ) {
            SimpleImageTranscoderFactory(config.experiments.maxBitmapSize)
        } else {
            MultiImageTranscoderFactory(
                config.experiments.maxBitmapSize,
                config.experiments.useDownsamplingRatioForResizing,
                config.imageTranscoderFactory,
                config.imageTranscoderType,
                config.experiments.isEnsureTranscoderLibraryLoaded
            )
        }
}