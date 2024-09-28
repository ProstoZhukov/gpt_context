package ru.tensor.sbis.base_app_components.error_check

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.widget.Toast
import androidx.annotation.WorkerThread
import timber.log.Timber
import ru.tensor.sbis.app_init.R as RA

/**
 * Проверка доступности физ. памяти на устройстве.
 *
 * @author du.bykov
 */
object NoMemoryOperator {

    /**
     * @param showToast отображать ли тост-сообщение, если нет памяти. Нужно, когда памяти
     * недостаточно даже для отображения [NoMemoryActivity], тогда пользователь увидит хотя бы тост.
     * @return true, если свободной памяти достаточно для запуска приложения
     */
    @WorkerThread
    fun isInternalMemoryNotSufficient(context: Context, showToast: Boolean = false): Boolean = try {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val sizeMb = 1024 * 1024

        val availableSpace = stat.blockSizeLong * stat.availableBlocksLong
        val availableSpaceMb = availableSpace / sizeMb

        if (availableSpace == 0L && showToast) showToast(context)

        val isNotSufficient = availableSpaceMb < MEMORY_THRESHOLD_MB
        if (isNotSufficient) logNotEnoughMemory(availableSpace, availableSpaceMb)
        isNotSufficient
    } catch (e: Exception) {
        Timber.e(e)
        false
    }

    private fun showToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(RA.string.app_init_no_memory_toast_msg),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun logNotEnoughMemory(availableSpace: Long, availableSpaceMb: Long) {
        Timber.e(
            "Количество доступной памяти $availableSpaceMb МБ ($availableSpace байт) меньше минимально необходимой $MEMORY_THRESHOLD_MB МБ"
        )
    }
}

private const val MEMORY_THRESHOLD_MB = 1