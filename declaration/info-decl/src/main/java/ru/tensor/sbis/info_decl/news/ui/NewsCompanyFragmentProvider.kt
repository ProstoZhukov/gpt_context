package ru.tensor.sbis.info_decl.news.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Интерфейс для открытия компании, от имени которой опубликована новость.
 *
 * @author am.boldinov
 */
interface NewsCompanyFragmentProvider : Feature {

    /**
     * Возвращает фрагмент для открытия карточки компании.
     *
     * @param companyUuid идентификатор компании новости
     */
    fun getCompanyDetailFragment(companyUuid: UUID): Fragment?
}