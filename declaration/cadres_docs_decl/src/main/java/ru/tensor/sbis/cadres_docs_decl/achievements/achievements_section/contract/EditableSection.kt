package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section.contract

import androidx.annotation.MainThread
import androidx.annotation.StringRes

/** Контракт для режима редактирования секции */
interface EditableSection {

    /** true, если есть права на редактирование ПиВ */
    val canEdit: Boolean

    /** true, если есть права на удаление ПиВ */
    val canDelete: Boolean

    /** Сохранение изменений секции в режиме редактирования */
    fun save(sectionEventHandler: SectionEventHandler)

    /** Удаление документа, по которому проинициализирована секция */
    fun delete(sectionEventHandler: SectionEventHandler)

    /**
     * Интерфейс обработчика рзультатов действий при взаимодействии с секцией ПиВ.
     */
    interface SectionEventHandler {

        /**
         * Действие было успешно завершено
         *
         * @param event - тип действия, результат которого был получен.
         */
        @MainThread
        fun onSuccess(event: SectionEvent)

        /**
         * Действие завершилось с ошибкой
         * @param event - тип действия, результат которого был получен.
         */
        @MainThread
        fun onError(event: SectionEvent, @StringRes message: Int? = null, strMsg: String? = null)
    }

    /** События при которых может быть возвращен результат работы секции ПиВ */
    sealed class SectionEvent {
        /**
         * Событие о результате сохранения.
         *
         * @property noValidState - true, если документ оказался в невалидном состоянии
         * при попытке сохранить.
         */
        class Save(val noValidState: Boolean = false): SectionEvent()

        /**
         * Событие возникает, если при редактировании документа и нажатии на кнопку сохранения
         * никаких изменений не произошло
         */
        object NothingChanged: SectionEvent()

        /** @SelfDocumented */
        object Delete: SectionEvent()
    }
}