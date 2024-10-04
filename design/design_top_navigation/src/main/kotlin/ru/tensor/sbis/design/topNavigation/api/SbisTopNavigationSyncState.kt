package ru.tensor.sbis.design.topNavigation.api

import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView

/**
 * Состояние индикаторов синхронизации [SbisTopNavigationView.syncState].
 *
 * @author da.zolotarev
 */
sealed interface SbisTopNavigationSyncState {

    /** Синхронизация не запущена, индикаторы скрыты. */
    object NotRunning : SbisTopNavigationSyncState {
        override fun applyState(loadingIndicator: View?, noNetworkIcon: View?) {
            loadingIndicator?.isVisible = false
            noNetworkIcon?.isVisible = false
        }
    }

    /** Запущена синхронизация, отображается индикатор загрузки. */
    object Running : SbisTopNavigationSyncState {
        override fun applyState(loadingIndicator: View?, noNetworkIcon: View?) {
            loadingIndicator?.isVisible = true
            noNetworkIcon?.isVisible = false
        }
    }

    /** Нет интернета, отображается иконка отсутствия сети. */
    object NoInternet : SbisTopNavigationSyncState {
        override fun applyState(loadingIndicator: View?, noNetworkIcon: View?) {
            loadingIndicator?.isVisible = false
            noNetworkIcon?.isVisible = true
        }
    }

    /**
     * Применить параметры выбранного типа к индикаторам синхронизации.
     *
     * [loadingIndicator] - индикатор загрузки.
     * [noNetworkIcon] - индикатор отсутствия интернета/сети.
     */
    fun applyState(loadingIndicator: View?, noNetworkIcon: View?)
}
