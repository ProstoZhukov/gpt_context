package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsActionSettings
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsOpenFrom
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsScreenState
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsType
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** Провайдер фрагментов ПиВ для просмотра/редактирования */
interface AchievementsFragmentProvider : Feature {

    /**
     * @param documentUUID - selfDocumented
     * @param docType - тип документа ПиВ.
     * @param openFrom - место откуда происходит открытие фрагмента из списка возможных.
     * @param screenState - в каком состоянии необходимо открыть фрагмент с данными.
     * @param actionSettings - настройка допустимых действий над документом, если есть на них
     *                         разрешение с онлайна.
     * @param needAddDefaultTopPadding - флаг добавления отступа под статус-бар
     */
    fun getFragment(
        documentUUID: UUID,
        docType: AchievementsType,
        openFrom: AchievementsOpenFrom = AchievementsOpenFrom.OTHER,
        screenState: AchievementsScreenState = AchievementsScreenState.SHOWING,
        actionSettings: AchievementsActionSettings = AchievementsActionSettings(),
        needAddDefaultTopPadding: Boolean = false
    ): Fragment

    /**
     * Получить фрагмент создания ПиВ с типом документа [docType],
     * типом ПиВ'а [pivType] и получателем [recipientEmployee].
     *
     * @param baseDocUUID - uuid документа-основания по которому создаётся ПиВ.
     */
    fun getAchievementCreationFragment(
        pivType: Long,
        recipientEmployee: UUID,
        docType: AchievementsType,
        baseDocUUID: UUID?
    ): Fragment
}