package ru.tensor.sbis.business_card_list.presentation.view

import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import java.util.UUID

/**@SelfDocumented*/
internal class ClicksWrapper {

    /** Клики по кнопкам "поделиться ссылкой" */
    val linkShareClicks = MutableSharedFlow<BusinessCard>()

    /** Клики по визиткам */
    val businessCardClicks = MutableSharedFlow<BusinessCard>()

    /** Клики по "закрепить ссылку" */
    val businessCardPinnedClicks = MutableSharedFlow<Pair<UUID, Boolean>>()

}