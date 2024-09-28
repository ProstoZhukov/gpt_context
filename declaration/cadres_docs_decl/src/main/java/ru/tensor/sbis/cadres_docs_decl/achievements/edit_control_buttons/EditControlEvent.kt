package ru.tensor.sbis.cadres_docs_decl.achievements.edit_control_buttons

import androidx.fragment.app.FragmentManager

/** Событие испускаемое управляющей кнопкой секции ПиВ при взаимодействии с ней */
sealed class EditControlEvent {
    /**
     * Запрос изменения фазы документа
     * @property callback - коллбэк с вызовом компонента ДЗЗ для смены фазы.
     */
    class PhaseChangeCall(val callback: (manager: FragmentManager) -> Unit) : EditControlEvent()

    /**  Событие с результатом изменения фазы. */
    class PhaseChangeResult(val isSuccess: Boolean) : EditControlEvent()

    /** Событие клика по кнопке сообщения */
    object OpenMessages : EditControlEvent()

    /** Событие клика по кнопке добавления вложений */
    object AddAttachments : EditControlEvent()

    /** Событие клика на кнопку изменения настроек публикации */
    object EditPublication: EditControlEvent()

    /** Событие клика по кнопке добавления сотрудников в ПиВ */
    object AddEmployees : EditControlEvent()
}