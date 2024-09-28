package ru.tensor.sbis.catalog_decl.catalog

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Публичный интерфейс модуля движения номенклатуры
 *
 * @author sp.lomakin
 */
interface WhReportFeature : Feature {

    /**
     *  Создать хост фрагмент
     *
     *  @param nomId id номенклатуры
     */
    fun createHostFragment(nomId: Long): Fragment

}