package ru.tensor.sbis.onboarding.ui.utils

import android.content.SharedPreferences
import androidx.annotation.WorkerThread

/**
 * Интерфейс, выполняющий сохранение и восстановление данных модуля "Приветственного Экрана" посредством [SharedPreferences]
 *
 * @author ps.smirnyh
 */
interface OnboardingPreferenceManager {

    /**
     * Восстанавливает [Boolean] значение состояния входа и отображения экрана приветствия для указанного аккаунта.
     *
     * @return true если для текущего аккаунта ранее был показан экран приветствия, иначе false
     */
    @WorkerThread
    fun restoreEntrance(): Boolean

    /**
     * Восстанавливает [Boolean] значение состояния входа и отображения экрана приветствия для любого пользователя.
     * Используется только для миграции на новый компонент онбординга.
     *
     * @return true если для любого аккаунта ранее был показан экран приветствия, иначе false
     */
    @WorkerThread
    fun restoreAnyEntrance(): Boolean = restoreEntrance()

    /** Сохраняет факт входа на экран приветствия для указанного аккаунта */
    @WorkerThread
    fun saveEntrance()

    /**
     * Восстанавливает [Boolean] значение состояния обработки экрана приветствия для указанного аккаунта
     *
     * @return true если для текущего аккаунта ранее был обработан экран приветствия, иначе false
     */
    @WorkerThread
    fun restoreProcessed(): Boolean

    /** Сохраняет состояние завершенности отображения экрана приветствия для указанного аккаунта */
    @WorkerThread
    fun saveProcessed()
}