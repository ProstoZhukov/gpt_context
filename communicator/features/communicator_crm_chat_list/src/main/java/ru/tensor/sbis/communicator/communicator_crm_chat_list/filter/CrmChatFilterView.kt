package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import java.util.UUID

/**
 * @author av.efimov1
 */
internal interface CrmChatFilterView :
    MviView<CrmChatFilterView.Model, CrmChatFilterView.Event> {

    /**@SelfDocumented*/
    sealed interface Event {

        /**@SelfDocumented*/
        object ResetClick : Event

        /**@SelfDocumented*/
        object ApplyClick : Event

        object BackClick : Event

        /**@SelfDocumented*/
        data class ContentItemIsSelected(val resultUuids: ArrayList<UUID>, val resultTitles: ArrayList<String>) : Event
    }

    /**
     * Модель для отображения.
     *
     * @property contentItems все ячейки в фильтре.
     * @property isChanged показываем кнопку "Сбросить или нет".
     */
    data class Model(
        val contentItems: List<Any>,
        val isChanged: Boolean,
        val contentIsOpen: Boolean,
        val headerTitle: Int
    )
}