package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.helpers

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.communicator.generated.LinkViewModel

/**
 * Обработчик лонг кликов по ссылкам из списка.
 *
 * @author dv.baranov
 */
internal class LinkItemLongClickHandler(val scope: LifecycleCoroutineScope) {

    /** @SelfDocumented **/
    val onLongItemClick: MutableSharedFlow<Pair<LinkViewModel, View>> = MutableSharedFlow()

    /** @SelfDocumented **/
    fun onLongItemClick(item: LinkViewModel, view: View) {
        scope.launch { onLongItemClick.emit(Pair(item, view)) }
    }
}