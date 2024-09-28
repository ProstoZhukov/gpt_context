package ru.tensor.sbis.user_activity_track_watcher.reporter

import androidx.annotation.WorkerThread

/**
 * Отправитель активности пользователя
 *
 * @author kv.martyshenko
 */
interface ActivityReporter {

    /**
     * Метод для отправки активности пользователя
     *
     * @param metaInfo метаинформация
     *
     * @return была ли отправлена информация
     */
    @WorkerThread
    fun report(metaInfo: String): Boolean

    companion object

}