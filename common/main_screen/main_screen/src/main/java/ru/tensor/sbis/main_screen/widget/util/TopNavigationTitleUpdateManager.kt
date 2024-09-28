package ru.tensor.sbis.main_screen.widget.util

import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem

/**
 * Предназначен для обновления заголовка в шапке, согласно данным от сервиса навигации.
 *
 * @author us.bessonov
 */
internal class TopNavigationTitleUpdateManager {

    private val registeredViews = mutableListOf<SbisTopNavigationView>()
    private val availableItems = mutableListOf<NavigationServiceItem>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** @SelfDocumented */
    fun registerView(view: SbisTopNavigationView) = with(view) {
        registeredViews.add(this)
        val job = observeTitleChanges()
        doOnAttach { updateTitle() }
        doOnDetach {
            registeredViews.remove(it)
            job.cancel()
        }
    }

    /** @SelfDocumented */
    fun onAvailableNavigationUpdated(items: List<NavigationServiceItem>) {
        availableItems.clear()
        availableItems.addAll(items)
        updateTitle()
    }

    private fun updateTitle() {
        registeredViews.forEach {
            it.updateTitle()
        }
    }

    private fun SbisTopNavigationView.observeTitleChanges() = scope.launch {
        contentChanges.collect {
            updateTitle()
        }
    }

    private fun SbisTopNavigationView.updateTitle() {
        if (!updateLargeTitle(availableItems)) {
            updateSmallTitle(availableItems)
        }
    }

    private fun SbisTopNavigationView.updateLargeTitle(items: List<NavigationServiceItem>): Boolean {
        (content as? SbisTopNavigationContent.LargeTitle)?.let { largeTitle ->
            largeTitle.navxId?.let { navxId ->
                val title = items.find { it.navxId == navxId }?.title
                    ?: return false
                content = largeTitle.copy(title = PlatformSbisString.Value(title))
                return true
            }
        }
        return false
    }

    private fun SbisTopNavigationView.updateSmallTitle(items: List<NavigationServiceItem>) {
        (content as? SbisTopNavigationContent.SmallTitle)?.let { smallTitle ->
            smallTitle.navxId?.let { navxId ->
                val title = items.find { it.navxId == navxId }?.title
                    ?: return
                content = smallTitle.copy(title = PlatformSbisString.Value(title))
            }
        }
    }

}