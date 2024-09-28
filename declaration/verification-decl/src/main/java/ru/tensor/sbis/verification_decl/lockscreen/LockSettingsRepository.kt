package ru.tensor.sbis.verification_decl.lockscreen

import androidx.annotation.WorkerThread
import ru.tensor.sbis.verification_decl.lockscreen.data.BiometricType
import java.util.UUID

/**
 * Контракт репозитория настроек блокировки и биометрии.
 *
 * @author ar.leschev
 */
interface LockSettingsRepository {

    /**
     * Установить уровень блокировки [type].
     */
    suspend fun setLockType(type: BiometricType)

    /**
     * Получить тип блокировки пользователя.
     * [externalUserUuid] UUID внешнего пользователя
     */
    suspend fun getLockType(externalUserUuid: UUID? = null): BiometricType

    /**
     * Установить пин-код.
     */
    suspend fun setPin(pinCode: String)

    /**
     * Задан ли пин-код.
     * Вернет false, даже если пользователя нет в МП.
     */
    suspend fun hasPin(): Boolean

    // region before auth callback. Тут нет активного пользователя, id приходит извне.

    /**
     * Нужно ли устанавливать пин для [userId].
     */
    @WorkerThread
    fun isPinForced(userId: UUID): Boolean

    /**
     * Установить [pin] для пользователя с [userId].
     */
    @WorkerThread
    fun setPin(userId: UUID, pin: Long)

    /**
     * Установить что биометрия настроена для пользователя с [userId].
     */
    @WorkerThread
    fun setBiometry(userId: UUID)

    // endregion before auth callback
}