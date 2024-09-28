package ru.tensor.sbis.logging.screen_tracker.log_providers

import android.content.Context
import ru.tensor.sbis.common.util.DeviceUtils

/**
 * Формирует лог с информацией о свободном месте на диске
 *
 * @property context см. [Context]
 *
 * @author av.krymov
 */
internal class MemoryLogProvider(private val context: Context) : ScreenTrackerLogProvider {

    override fun getLogMessage(screen: Any, action: String): String {
        return "(available disk space for app folder is ${DeviceUtils.getAvailableDiskSpaceForAppFolderInMb(context)} Mb)"
    }
}