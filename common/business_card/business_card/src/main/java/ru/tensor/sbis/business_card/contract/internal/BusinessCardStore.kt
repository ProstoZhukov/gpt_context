package ru.tensor.sbis.business_card.contract.internal

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.business_card.store.BusinessCardStoreFactory
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard

/** Store визитки */
internal interface BusinessCardStore :
    Store<BusinessCardStore.Intent, BusinessCardStore.State, BusinessCardStore.Label> {

    /** Действия пользователя*/
    sealed interface Intent {
        /**@SelfDocumented*/
        fun handle(executor: BusinessCardStoreFactory.ExecutorImpl)

        /**@SelfDocumented*/
        object OnToolbarBackClicked : Intent {
            override fun handle(executor: BusinessCardStoreFactory.ExecutorImpl) = executor.back()
        }

        /**@SelfDocumented*/
        data class OnLinkButtonClicked(val params: BusinessCard) : Intent {
            override fun handle(executor: BusinessCardStoreFactory.ExecutorImpl) = executor.toShareLink(params)
        }
    }

    /** События, обрабатывабщиеся вне экрана*/
    sealed interface Label {
        /**@SelfDocumented*/
        fun handle(router: BusinessCardRouter)

        /**@SelfDocumented*/
        object NavigateBack : Label {
            override fun handle(router: BusinessCardRouter) = router.back()
        }

        /**@SelfDocumented*/
        data class NavigateToShareLink(val params: BusinessCard) : Label {
            override fun handle(router: BusinessCardRouter) {
                router.toLinkShare(params.links)
            }
        }
    }

    /** Cостояние экрана*/
    @Parcelize
    object State : Parcelable
}