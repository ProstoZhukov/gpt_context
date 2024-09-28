package ru.tensor.sbis.person_decl.employee.person_card.achievements

import android.content.Context
import android.content.Intent
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Интерфейс обертка над фичёй AchievementsFeature для инвертирования зависимости
 * и возможности обойти подключение репозитория мотивации к приложениям, где есть
 * карточка сотрудника, но нет мотивации.
 */
interface AchievementsFeaturePersonCardWrapper : Feature {

    /** Получать фрагмент с детальной информацией по поощрению/взысканию. */
    fun getFragment(documentUUID: UUID, docType: String, editState: Boolean): Fragment

    /**
     * @param personUUID - selfDocumented
     * @param personId - числовой идентификатор пользователя на облаке
     */
    fun createPivSelectorIntent(
        context: Context,
        personUUID: UUID,
        personId: Long
    ): Intent

    /**
     * @param kindId - тип ПиВ-а.
     * @param receiverId - идентификатор получателя результата
     */
    fun createFundSelectorIntent(
        context: Context,
        kindId: Long,
        receiverId: String = ""
    ): Intent

    /**
     * Проверка доступности функционала создания/редактирования ПИВ
     */
    @WorkerThread
    fun checkEditStateFeature(recipientUUID: UUID): Flow<Boolean> = flowOf(false)
}