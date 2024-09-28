package ru.tensor.sbis.business_card_list.contract.internal.list

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.business_card_list.store.BusinessCardListStoreFactory
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard

/** Store визитки */
internal interface BusinessCardListStore :
    Store<BusinessCardListStore.Intent, BusinessCardListStore.State, BusinessCardListStore.Label> {

    /** Действия пользователя*/
    sealed interface Intent {
        /**@SelfDocumented*/
        fun handle(executor: BusinessCardListStoreFactory.ExecutorImpl)

        /** Событие нажатия кнопки "назад" в шапке */
        object OnToolbarBackClicked : Intent {
            override fun handle(executor: BusinessCardListStoreFactory.ExecutorImpl) = executor.back()
        }
    }

    /** События, обрабатывабщиеся вне экрана*/
    sealed interface Label {
        /**@SelfDocumented*/
        fun handle(router: BusinessCardListRouter)

        /**@SelfDocumented*/
        object NavigateBack : Label {
            override fun handle(router: BusinessCardListRouter) = router.back()
        }

        /**@SelfDocumented*/
        data class ToLinkShare(val params: BusinessCard) : Label {
            override fun handle(router: BusinessCardListRouter) {
                router.toLinkShare(params.links)
            }
        }

        /** Перейти на экран визитки */
        data class NavigateToSingleCard(val data: BusinessCard) : Label {
            override fun handle(router: BusinessCardListRouter) = router.toBusinessCardItem(data)
        }

        /**
         * Ошибка загрузки
         */
        data class ShowPinError(val errorMessage: String) : Label {
            override fun handle(router: BusinessCardListRouter) = router.showPinError(errorMessage)
        }
    }

    /** Cостояние экрана*/
    @Parcelize
    data class State(
        val needToInitList: Boolean = true
    ) : Parcelable
}