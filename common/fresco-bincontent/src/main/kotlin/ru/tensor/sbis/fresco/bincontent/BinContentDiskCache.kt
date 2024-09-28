package ru.tensor.sbis.fresco.bincontent

import android.net.Uri
import bolts.Task
import com.facebook.cache.common.CacheKey
import com.facebook.cache.disk.FileCache
import com.facebook.common.memory.PooledByteBufferFactory
import com.facebook.common.memory.PooledByteStreams
import com.facebook.common.references.CloseableReference
import com.facebook.common.util.UriUtil
import com.facebook.imagepipeline.cache.BufferedDiskCache
import com.facebook.imagepipeline.cache.ImageCacheStatsTracker
import com.facebook.imagepipeline.image.EncodedImage
import ru.tensor.sbis.desktop.bincontent.generated.BinContentService
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Реализация [BufferedDiskCache], взаимодействующая с микросервисом бинарного контента [BinContentService]
 * Взаимодействие идёт по ключам, где [Uri] является ссылкой на веб-ресурс, т.е. схема http или https
 * Установки в кэш (см. [put]) для файлов по таким [Uri] не происходит, т.к. они были сохранены ранее внутри микросервиса
 *
 * @author sa.nikitin
 */
internal class BinContentDiskCache(
    private val binContentService: Lazy<BinContentService>,
    fileCache: FileCache,
    private val pooledByteBufferFactory: PooledByteBufferFactory,
    pooledByteStreams: PooledByteStreams,
    private val readExecutor: Executor,
    private val writeExecutor: Executor,
    imageCacheStatsTracker: ImageCacheStatsTracker
) : BufferedDiskCache(
    fileCache,
    pooledByteBufferFactory,
    pooledByteStreams,
    readExecutor,
    writeExecutor,
    imageCacheStatsTracker
) {

    override fun diskCheckSync(key: CacheKey): Boolean =
        key.asBinContentUrl()
            ?.let { getFileFromBinContentCache(it)?.exists() ?: false }
            ?: super.diskCheckSync(key)

    override fun get(key: CacheKey, isCancelled: AtomicBoolean): Task<EncodedImage?> =
        key.asBinContentUrl()?.let { getFromBinContentCache(it, isCancelled) } ?: super.get(key, isCancelled)

    override fun put(key: CacheKey, encodedImage: EncodedImage?) {
        if (!key.isBinContentKey()) {
            super.put(key, encodedImage)
        }
    }

    override fun remove(key: CacheKey): Task<Void?> =
        key.asBinContentUrl()?.let { removeFromBinContentCache(it) } ?: super.remove(key)

    private fun CacheKey.asBinContentUrl(): String? =
        if (isBinContentKey()) {
            uriString
        } else {
            null
        }

    private fun CacheKey.isBinContentKey(): Boolean =
        //Схема http и https может быть отрезана через CacheKeyFactory, поэтому условие такое
        !(uriString.startsWith(UriUtil.LOCAL_FILE_SCHEME) || uriString.startsWith(UriUtil.LOCAL_CONTENT_SCHEME))

    private fun getFromBinContentCache(url: String, isCancelled: AtomicBoolean): Task<EncodedImage?> =
        Task.call(
            {
                if (isCancelled.get()) throw CancellationException()

                val cacheFile = getFileFromBinContentCache(url)
                when {
                    cacheFile == null -> null
                    !cacheFile.exists() -> {
                        // Информация о файле в кэше неактуальна - удалить из сервиса
                        binContentService.value.removeLocalFile(url)
                        null
                    }
                    else -> {
                        try {
                            FileInputStream(cacheFile).use { stream ->
                                val size = cacheFile.length().toInt()
                                CloseableReference
                                    .of(pooledByteBufferFactory.newByteBuffer(stream, size))
                                    .use(::EncodedImage)
                            }
                        } catch (e: Throwable) {
                            Timber.e(e)
                            binContentService.value.removeLocalFile(url)
                            null
                        }
                    }
                }
            },
            readExecutor
        )

    private fun getFileFromBinContentCache(url: String): File? {
        return try {
            val path = binContentService.value.getFilePathByUrl(url)
            path?.let(::File)
        } catch (e: Exception) {
            Timber.w(e)
            null
        }
    }

    private fun removeFromBinContentCache(url: String): Task<Void?> =
        Task.call(
            {
                binContentService.value.removeLocalFile(url)
                null
            },
            writeExecutor
        )
}