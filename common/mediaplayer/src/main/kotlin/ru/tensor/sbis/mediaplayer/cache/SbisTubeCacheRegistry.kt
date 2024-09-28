package ru.tensor.sbis.mediaplayer.cache

import android.content.Context
import android.os.Looper
import androidx.annotation.MainThread
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import ru.tensor.sbis.mediaplayer.BuildConfig
import timber.log.Timber
import java.io.File

/**
 * Максимальный размер кэша медиафайлов (500 Мб)
 */
private const val MAX_MEDIA_CACHE_SIZE_IN_BYTES: Long = 500 * 1024 * 1024

@UnstableApi
/**
 * Реестр кешей медиаплеера
 * Нужен, чтобы гарантировать, что одна директория не будет использоваться двумя кешами. См. описание [SimpleCache]
 * Во время жизни приложения должен быть один экземпляр этого класса
 *
 * @author sa.nikitin
 */
class SbisTubeCacheRegistry private constructor() {

    companion object {

        val INSTANCE = SbisTubeCacheRegistry()
    }

    private val cacheRegistry: MutableList<SbisTubeCache> = mutableListOf()
    private var databaseProvider: DatabaseProvider? = null

    @MainThread
    private fun requireDatabaseProvider(context: Context): DatabaseProvider =
        databaseProvider.let { databaseProvider ->
            ensureMainThread()
            if (databaseProvider == null) {
                val newDatabaseProvider = StandaloneDatabaseProvider(context)
                this.databaseProvider = newDatabaseProvider
                newDatabaseProvider
            } else {
                databaseProvider
            }
        }

    private fun ensureMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val ensureMainThreadException = RuntimeException("This method should be called on main thread")
            if (BuildConfig.DEBUG) {
                throw ensureMainThreadException
            } else {
                Timber.e(ensureMainThreadException)
            }
        }
    }

    @UnstableApi
    /**
     * Кеш медиафайлов со счётчиком его пользователей
     *
     * @property cacheDir       Директория кеша
     * @property maxSize        Максимальный размер кеша
     * @property cache          Используемый кеш
     * @property usersCount     Количество пользователей кеша
     */
    inner class SbisTubeCache @MainThread constructor(context: Context, val cacheDir: File, val maxSize: Long) {

        private val cache: Cache =
            SimpleCache(
                cacheDir,
                LeastRecentlyUsedCacheEvictor(maxSize),
                requireDatabaseProvider(context)
            )
        private var usersCount: Int = 0

        init {
            cacheRegistry.add(this)
        }

        fun connect(): Facade {
            usersCount++
            return Facade()
        }

        private fun release() {
            usersCount--
            if (usersCount == 0) {
                cache.release()
                cacheRegistry.remove(this)
            }
        }

        /**
         * Фасад используемого кеша
         */
        inner class Facade {

            private var released: Boolean = false

            /**
             * Предоставить максимальный размер кеша
             */
            fun maxSize(): Long = maxSize

            /**
             * Предоставить кеш
             */
            @Throws(IllegalStateException::class)
            fun cache(): Cache =
                if (released)
                    throw IllegalStateException("This cache already released")
                else
                    this@SbisTubeCache.cache

            /**
             * Освободить кеш
             */
            fun release() {
                if (!released) {
                    released = true
                    this@SbisTubeCache.release()
                }
            }
        }
    }

    /**
     * Предоставить кеш
     *
     * @param cacheDir Директория кеша
     */
    @MainThread
    fun getCache(context: Context, cacheDir: File): SbisTubeCache.Facade =
        (findCache(cacheDir) ?: newCache(context, cacheDir)).connect()

    private fun findCache(cacheDir: File): SbisTubeCache? =
        cacheRegistry.find { sbisTubeCache -> sbisTubeCache.cacheDir == cacheDir }

    private fun newCache(context: Context, cacheDir: File): SbisTubeCache =
        SbisTubeCache(
            context,
            cacheDir,
            MAX_MEDIA_CACHE_SIZE_IN_BYTES
        )
}