package ru.tensor.sbis.communication_decl.selection.universal

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик компонента универсального выбора.
 *
 * @author vv.chekurda
 */
interface UniversalSelectionProvider : UniversalSelectionResultManager.Provider, Feature {

    /**
     * Получить фрагмент универсального выбора.
     *
     * @param config конфигурация компонента универсального выбора.
     */
    fun getUniversalSelectionFragment(config: UniversalSelectionConfig): Fragment

    /**
     * Получить intent для открытия активити компонента универсального выбора.
     *
     * @param config конфигурация компонента универсального выбора.
     */
    fun getUniversalSelectionIntent(context: Context, config: UniversalSelectionConfig): Intent
}