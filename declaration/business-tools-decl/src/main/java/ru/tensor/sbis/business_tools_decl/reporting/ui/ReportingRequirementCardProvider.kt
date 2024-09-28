package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Интерфейс провайдер детальная карточка требований
 *
 * @author ae.noskov
 */
interface ReportingRequirementCardProvider : Feature {

    /**
     * Возвращает [Intent] детальной карточки требования [requirementUuid]
     */
    fun getRequirementCardIntent(requirementUuid: UUID): Intent

}