package ru.tensor.sbis.our_organisations.feature

import androidx.fragment.app.Fragment
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams

/**
 * Фабрика дял создания фрагментов выбора нашей организации.
 *
 * @author mv.ilin
 */
fun interface OurOrgFragmentFactory {
    fun create(params: OurOrgParams): Fragment
}