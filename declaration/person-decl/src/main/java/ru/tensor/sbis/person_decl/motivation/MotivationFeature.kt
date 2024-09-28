package ru.tensor.sbis.person_decl.motivation

import ru.tensor.sbis.person_decl.motivation.money_provider.MotivationMoneyProvider
import ru.tensor.sbis.person_decl.motivation.ui.MotivationFragmentsProvider
import ru.tensor.sbis.person_decl.motivation.ui.MotivationStartTabs

/**
 * Контракт модуля мотивации.
 *
 * @author ra.temnikov
 */
interface MotivationFeature :
    MotivationFragmentsProvider,
    MotivationMoneyProvider,
    MotivationPermissionFeature {
    /**
     * Метод для получения информации по правам на Мотивацию.
     *
     * @return true если есть права на отображение
     */
    suspend fun isMotivationAvailable(): Boolean

    companion object {
        /** Конкретный таб, который необходимо открыть при старте модуля "Мотивация". [MotivationStartTabs] */
        const val ARG_MOTIVATION_TAB_ID = "motivation_tab_id"
    }
}