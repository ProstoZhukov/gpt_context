package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фича модуля ПиВ с завимостями от модуля задач.
 *
 *  Содержит функционал ПиВ, который зависит от задач и который было принято решение
 *  не выносить в контроллерную часть, чтобы не раздувать контроллер, так как ПиВ на ui
 *  могут работать в приложениях, где нет задач, отображаясь как карточки, а не документы.
 */
interface AchievementsDependencyOnTasksFeature: Feature {

    /**
     * Изменить прочитанность документа ПиВ.
     */
    suspend fun setReadStatus(docUuid: UUID, isRead: Boolean, ownerFace: UUID)
}