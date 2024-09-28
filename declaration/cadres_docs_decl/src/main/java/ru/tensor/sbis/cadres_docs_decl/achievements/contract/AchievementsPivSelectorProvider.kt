package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** Провайдер фрагментов ПиВ для просмотра/редактирования */
interface AchievementsPivSelectorProvider : Feature {

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
     * @param receiverId - идентификатор получателя результата.
     */
    fun createFundSelectorIntent(
        context: Context,
        kindId: Long,
        receiverId: String
    ): Intent
}