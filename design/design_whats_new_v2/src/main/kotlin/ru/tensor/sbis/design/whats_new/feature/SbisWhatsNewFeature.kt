package ru.tensor.sbis.design.whats_new.feature

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Контракт компонента "Что нового".
 *
 * @author ps.smirnyh
 */
interface SbisWhatsNewFeature : Feature {

    /**
     * Показать "Что нового", если это необходимо.
     *
     * @param isPopBackStackEnable будет ли транзакция добавлена в backStack при открытии и вызван popBackStack при закрытии.
     *
     * @return был ли открыт экран "Что нового".
     */
    fun openIfNeeded(fragmentManager: FragmentManager, containerId: Int, isPopBackStackEnable: Boolean): Boolean

    /** Нужно ли показать "Что нового". */
    fun isNeedShow(): Boolean

    companion object {

        /** Ключ для получения события закрытия экрана через Fragment Result API. */
        const val SBIS_WHATS_NEW_FRAGMENT_RESULT_KEY = "SBIS_WHATS_NEW_FRAGMENT_RESULT_KEY"

        /** @SelfDocumented */
        const val WHATS_NEW_FRAGMENT_TAG = "WHATS_NEW_FRAGMENT_TAG"
    }
}