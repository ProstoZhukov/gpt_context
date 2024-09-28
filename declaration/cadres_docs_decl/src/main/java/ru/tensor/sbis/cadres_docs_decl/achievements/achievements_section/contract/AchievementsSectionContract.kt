package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract

import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsChangeScreenStateListener
import java.util.*

/**
 * Контракт секции ПиВ.
 */
interface AchievementsSectionContract {

    /** Контракт view (ListSection) */
    interface View :
        AndroidComponent,
        AchievementsChangeScreenStateListener,
        EditableSection,
        AddRecipientListener {

        /** Уникальный ключ секции ПиВ */
        val uniqueSectionKey: String

        /**
         * Сконфигурировать секцию на показ документа по UUID.
         * Отображение данных не начнется, пока не будет вызван данный метод.
         */
        fun configureDocument(uuid: UUID)

        /** @SelfDocumented */
        fun onRefresh(fromCache: Boolean = false)

        /** Начать процесс добавления вложений в документ */
        fun startAddAttachmentsProcess()

        /** Открыть экран изменения настроек публикации */
        fun startEditPublicationScreen(type: PublicationOpenType)

        /** Обновить состояние важности документа */
        fun changeImportance()
    }


    /** Контракт viewModel (ListController) */
    interface ViewModel : ListController
}