package ru.tensor.sbis.communication_decl.call_history

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдинг экрана "История звонков"
 *
 * @author av.efimov1
 */
interface CallHistoryProvider : Feature {

    /** @SelfDocumented */
    fun getCallHistoryFragment(): Fragment
}