package ru.tensor.sbis.version_checker_decl

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик [Intent] экрана принудительного обновления
 *
 * @author as.chadov
 */
interface VersioningIntentProvider : Feature {

    /**
     * Получить [Intent] для открытия экрана принудительного обновления приложения
     *
     * @param ifObsolete true если требуется отдать [Intent] только если текущее МП устарело
     */
    fun getForcedUpdateAppActivityIntent(ifObsolete: Boolean = true): Intent?
}