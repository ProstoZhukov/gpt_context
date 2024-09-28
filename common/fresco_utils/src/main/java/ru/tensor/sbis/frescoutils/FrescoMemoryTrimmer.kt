package ru.tensor.sbis.frescoutils

import android.content.ComponentCallbacks2.*
import com.facebook.common.memory.MemoryTrimType
import timber.log.Timber

/**
 * Шаблон предупреждения о низком уровне доступной памяти
 */
private const val SBIS_MEM_WARN = "Fresco.MemWarn: level - %s; App used memory : %s; trimmableCount : %d"

/**
 * Реализация очистки памяти
 */
class FrescoMemoryTrimmer(private val frescoMemoryRegistry: FrescoMemoryRegistry) {

    fun onTrimMemory(level: Int) {
        when (level) {
            //Приложение находится на переднем плане, системе начинает не хватать памяти
            //Пока не освобождаем память, ждём более критичного уровня
            TRIM_MEMORY_RUNNING_MODERATE -> Unit

            //Приложение находится на переднем плане, системе не хватает памяти
            TRIM_MEMORY_RUNNING_LOW      ->
                frescoMemoryRegistry.trim(MemoryTrimType.OnSystemLowMemoryWhileAppInForeground)

            //Приложение находится на переднем плане, системе критически не хватает памяти
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Timber.w(SBIS_MEM_WARN, level.toString(), getPrintUsedMemoryString(), frescoMemoryRegistry.trimmableItemCount)
                frescoMemoryRegistry.trim(MemoryTrimType.OnSystemMemoryCriticallyLowWhileAppInForeground)
            }

            //UI приложения не виден, память не освобождаем, т.к. при появлении UI изображения заново загружаются, моргание
            TRIM_MEMORY_UI_HIDDEN        -> Unit

            //Приложение находится на заднем плане, системе начинает не хватать памяти
            //Пока не освобождаем память, ждём более критичного уровня
            TRIM_MEMORY_BACKGROUND       -> Unit

            //Приложение находится на заднем плане, системе не хватает памяти
            TRIM_MEMORY_MODERATE,
            TRIM_MEMORY_COMPLETE         -> {
                Timber.w(SBIS_MEM_WARN, level.toString(), getPrintUsedMemoryString(), frescoMemoryRegistry.trimmableItemCount)
                frescoMemoryRegistry.trim(MemoryTrimType.OnSystemLowMemoryWhileAppInBackground)
            }
        }
    }

    private fun getPrintUsedMemoryString(): String {
        var freeSize: Long = 0
        var totalSize: Long = 0
        var usedSize: Long = 0
        try {
            val info = Runtime.getRuntime()
            freeSize = info.freeMemory()
            totalSize = info.totalMemory()
            usedSize = totalSize - freeSize
        } catch (e: Exception) {
            //ignored
        }
        return "freeSize = $freeSize , totalSize = $totalSize , usedSize = $usedSize"
    }
}