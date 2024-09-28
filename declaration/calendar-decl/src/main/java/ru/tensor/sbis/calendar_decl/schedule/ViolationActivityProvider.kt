package ru.tensor.sbis.calendar_decl.schedule

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** Провайдер активностей экранов нарушений */
interface ViolationActivityProvider : Feature {

    /**
     * Получить [Intent] карточки нарушения
     * @param documentUuid UUID документа
     * @param violationEventUuid UUID события нарушения
     */
    fun getViolationDetailsIntent(documentUuid: UUID, violationEventUuid: UUID?): Intent

    /**
     * Получить [Intent] экрана списка нарушений за выбранный период
     * @param startDate начало периода
     * @param endDate конец периода
     * @param violationType тип нарушения
     * @param violationListTitle заголовок экрана
     * @param violationEventUuid UUID события нарушения
     */
    fun getViolationsListIntent(
        startDate: String,
        endDate: String,
        violationType: String,
        violationListTitle: String?,
        violationEventUuid: UUID? = null
    ): Intent

    /**
     * Создание фрагмента картчоки нарушения
     * @param args Bundle с аргументами для создания картчоки нарушения
     */
    fun getViolationDetailsFragment(args: Bundle): Fragment
}
