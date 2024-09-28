package ru.tensor.sbis.design.link_share.contract.internal

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.link_share.store.LinkShareStoreFactory.ExecutorImpl
import ru.tensor.sbis.design.link_share.utils.LinkShareURLProvider
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareLink
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem

/**@SelfDocumented*/
internal interface LinkShareStore :
    Store<LinkShareStore.Intent, LinkShareStore.State, LinkShareStore.Label> {

    /** @SelfDocumented */
    interface Intent {

        /** Метод для обработки различных типов Intent */
        fun handle(executor: ExecutorImpl)

        /**  Нажатие на один из пунктов меню ссылки */
        class MenuItemClicked(val selectedLinkOption: SbisLinkShareMenuItem) : Intent {
            override fun handle(executor: ExecutorImpl) {
                executor.processSelectedLinkOption(selectedLinkOption)
            }
        }

        /** Выбор ссылки при нажатии на таб */
        class TabLinkSelected(
            val links: List<SbisLinkShareLink>,
            val tabs: List<SbisTabViewItemContent>
        ) : Intent {
            override fun handle(executor: ExecutorImpl) {
                executor.processSelectedLinkTab(tabs, links)
            }
        }
    }

    /** @SelfDocumented */
    interface Label {

        /** Метод для обработки различных типов Label */
        fun handle(router: LinkShareRouter, linkDataProvider: LinkShareURLProvider)

        /** Событие скопировать ссылку в буфер обмена и показать алерт об этом */
        class ShowCopyLinkAlertDialog(val alertDialogMessage: String, val selectedLinkTab: Int) : Label {
            override fun handle(router: LinkShareRouter, linkDataProvider: LinkShareURLProvider) {
                val url = linkDataProvider.getURLForSelectedTab(selectedLinkTab)
                router.showCopyLinkAlertDialog(alertDialogMessage, url)
            }
        }

        /** Событие открыть ссылку в браузере */
        class OpenLinkInBrowser(private val selectedLinkTab: Int) : Label {
            override fun handle(router: LinkShareRouter, linkDataProvider: LinkShareURLProvider) {
                val url = linkDataProvider.getURLForSelectedTab(selectedLinkTab)
                router.openLinkInBrowser(url)
            }
        }

        /** Событие поделиться ссылкой */
        class ShareLink(val selectedLinkTab: Int) : Label {
            override fun handle(router: LinkShareRouter, linkDataProvider: LinkShareURLProvider) {
                val url = linkDataProvider.getURLForSelectedTab(selectedLinkTab)
                router.shareLink(url)
            }
        }

        /** Событие открыть QR код */
        class ShowQRDialogFragment(val selectedLinkTab: Int) : Label {
            override fun handle(router: LinkShareRouter, linkDataProvider: LinkShareURLProvider) {
                val url = linkDataProvider.getURLForSelectedTab(selectedLinkTab)
                router.showQRCodeDialogFragment(url)
            }
        }
    }

    /** @SelfDocumented */
    @Parcelize
    object State : Parcelable
}