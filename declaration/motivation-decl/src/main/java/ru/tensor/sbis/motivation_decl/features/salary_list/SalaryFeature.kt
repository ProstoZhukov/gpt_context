package ru.tensor.sbis.motivation_decl.features.salary_list

import androidx.fragment.app.Fragment
import ru.tensor.sbis.motivation_decl.features.common.FragmentOpenArgs
import ru.tensor.sbis.motivation_decl.features.common.ToolbarData
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фича зарплаты.
 *
 * @author ra.temnikov
 */
interface SalaryFeature : Feature {

    /**
     * Создать реестр зарплаты с базовой master-detail навигацией.
     *
     * @param personUUID - идентификатор пользователя для просмотра зарплаты.
     * @param toolbarData - данные для установки в шапку.
     * @param needShowPaymentBanner - true, если нужно отображать баннер досрочног аванса.
     * @param needShowPayoutsButton - true, если нужно показывть кнопку перехода на экран выплат.
     */
    fun createSalaryListScreenWithMasterDetailNavigation(
        personUUID: UUID,
        toolbarData: ToolbarData,
        needShowPaymentBanner: Boolean,
        needShowPayoutsButton: Boolean,
        openArgs: FragmentOpenArgs
    ): Fragment
}