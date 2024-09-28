package ru.tensor.sbis.our_organisations.feature

import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams

/**
 * Фабрика дял создания окна выбора нашей организации.
 *
 * @author mv.ilin
 */
fun interface OurOrgSelectionWindowFactory {
    fun create(params: OurOrgParams): DialogFragment
}