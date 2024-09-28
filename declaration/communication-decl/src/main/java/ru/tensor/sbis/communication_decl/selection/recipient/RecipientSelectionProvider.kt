package ru.tensor.sbis.communication_decl.selection.recipient

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик компонента выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionProvider : RecipientSelectionResultManager.Provider, Feature {

    /**
     * Получить фрагмент выбора получаетелей.
     *
     * @param config конфигурация компонента выбора.
     */
    fun getRecipientSelectionFragment(config: RecipientSelectionConfig): Fragment

    /**
     * Получить intent для открытия активити компонента выбора получателей.
     *
     * @param config конфигурация компонента выбора.
     */
    fun getRecipientSelectionIntent(context: Context, config: RecipientSelectionConfig): Intent
}