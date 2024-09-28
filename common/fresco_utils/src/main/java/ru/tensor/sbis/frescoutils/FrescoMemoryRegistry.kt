package ru.tensor.sbis.frescoutils

import com.facebook.common.memory.MemoryTrimType
import com.facebook.common.memory.MemoryTrimmable
import com.facebook.common.memory.MemoryTrimmableRegistry
import java.util.*

/**
 * Класс, обеспечивающий уведомление подписчиков типа [MemoryTrimmable] о том, что им необходимо
 * уменьшить использование памяти
 */
class FrescoMemoryRegistry : MemoryTrimmableRegistry {

    private val trimmableSet = Collections.synchronizedSet(LinkedHashSet<MemoryTrimmable>())

    val trimmableItemCount get() = trimmableSet.size

    override fun registerMemoryTrimmable(trimmable: MemoryTrimmable?) {
        trimmable ?: return
        trimmableSet.add(trimmable)
    }

    override fun unregisterMemoryTrimmable(trimmable: MemoryTrimmable?) {
        trimmable ?: return
        trimmableSet.remove(trimmable)
    }

    /**
     * Уменьшает использование памяти
     */
    fun trim(level: MemoryTrimType) {
        synchronized(trimmableSet) {
            for (trimmable in trimmableSet) {
                trimmable.trim(level)
            }
        }
    }
}