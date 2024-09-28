package ru.tensor.sbis.communication_decl.meeting

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдинг экрана "Реестра событий"
 *
 * @author av.efimov1
 */
interface EventsFeedProvider : Feature {

    /** @SelfDocumented */
    fun getEventsFeedFragment(bundle: Bundle? = null): Fragment
}