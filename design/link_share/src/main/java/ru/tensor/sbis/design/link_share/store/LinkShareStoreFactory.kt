package ru.tensor.sbis.design.link_share.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.link_share.R
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareStore
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareStore.Intent
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareStore.Label
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareStore.State
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareLink
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.COPY
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.OPEN_IN_BROWSER
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.QR
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.SEND
import javax.inject.Inject

/**@SelfDocumented*/
internal class LinkShareStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val resourceProvider: ResourceProvider
) {

    /** @SelfDocumented */
    fun create(): LinkShareStore =
        object :
            LinkShareStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "LinkShareStore",
                initialState = State,
                executorFactory = { ExecutorImpl(resourceProvider) }
            ) {}

    internal class ExecutorImpl(
        private val resourceProvider: ResourceProvider
    ) : CoroutineExecutor<Intent, Unit, State, Unit, Label>() {
        private var selectedLinkTab: Int = 0
        private val actionHandlers = mapOf(
            COPY to {
                publish(
                    Label.ShowCopyLinkAlertDialog(
                        resourceProvider.getString(R.string.link_share_alert_link_copied),
                        selectedLinkTab
                    )
                )
            },
            OPEN_IN_BROWSER to { publish(Label.OpenLinkInBrowser(selectedLinkTab)) },
            QR to { publish(Label.ShowQRDialogFragment(selectedLinkTab)) },
            SEND to { publish(Label.ShareLink(selectedLinkTab)) }
        )

        override fun executeIntent(intent: Intent, getState: () -> State) {
            intent.handle(this)
        }

        internal fun processSelectedLinkOption(view: SbisLinkShareMenuItem) {
            actionHandlers[view]?.invoke()
        }

        // От выбранного таба зависит, какую ссылку использовать для операций
        internal fun processSelectedLinkTab(
            tabs: List<SbisTabViewItemContent>,
            links: List<SbisLinkShareLink>
        ) {
            val selectedTitle = tabs.filterIsInstance<SbisTabViewItemContent.Text>()
                .firstOrNull()?.text?.getString(resourceProvider.mContext)
            if (!selectedTitle.isNullOrBlank()) {
                selectedLinkTab = links.indexOfFirst { it.caption == selectedTitle }
            }
        }
    }
}