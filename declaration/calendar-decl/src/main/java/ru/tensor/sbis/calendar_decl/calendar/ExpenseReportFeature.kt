package ru.tensor.sbis.calendar_decl.calendar

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.regulation.generated.Regulation
import java.util.UUID

/** Провайдер фрагмента карточки Авансвого отчёта */
interface ExpenseReportFeature : Feature {

    /** Метод получения фрагмента для создания авансового отчета */
    fun openExpenseReportEditFragment(
        regulation: Regulation?,
    ): Fragment? = null

    /** Открытие карточки Авансовый отчет для просмотра */
    fun openExpenseReportViewFragment(
        docUuid: UUID,
        docTypeString: String
    ): Fragment
}