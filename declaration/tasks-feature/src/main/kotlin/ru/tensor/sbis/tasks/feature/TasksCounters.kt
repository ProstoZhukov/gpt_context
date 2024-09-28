package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Описывает счётчики задач.
 * @property mainCounter основной счётчик.
 * @property additionalCounter дополнительный счётчик ("НаМне"  - непрочитанные, "ОтМеня" - просроченные).
 *
 * @author aa.sviridov
 */
@Parcelize
data class TasksCounters(
    val mainCounter: Long,
    val additionalCounter: Long
) : Parcelable {

    companion object {

        /**
         * Пустые счётчики задач.
         */
        val EMPTY = TasksCounters(0, 0)
    }
}