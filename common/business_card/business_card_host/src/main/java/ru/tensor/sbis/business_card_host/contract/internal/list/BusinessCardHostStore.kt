package ru.tensor.sbis.business_card_host.contract.internal.list

import com.arkivanov.mvikotlin.core.store.Store
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import java.util.UUID
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostStore.*

/** Store визитки */
internal interface BusinessCardHostStore :
    Store<Intent, State, Label> {

    /** Действия пользователя*/
    sealed interface Intent

    /** События, обрабатывабщиеся вне экрана*/
    sealed interface Label {
        /**@SelfDocumented*/
        fun handle(router: BusinessCardHostRouter)

        /** Перейти на экран единичной визитки */
        data class ToBusinessCard(val params: BusinessCard) : Label {
            override fun handle(router: BusinessCardHostRouter) {
                router.showBusinessCardFragment(params)
            }
        }

        /** Перейти на экран реестра визиток */
        data class ToBusinessCardList(val personUUID: UUID) : Label {
            override fun handle(router: BusinessCardHostRouter) = router.showBusinessCardListFragment(personUUID)
        }
    }

    /** Cостояние экрана*/
    object State
}