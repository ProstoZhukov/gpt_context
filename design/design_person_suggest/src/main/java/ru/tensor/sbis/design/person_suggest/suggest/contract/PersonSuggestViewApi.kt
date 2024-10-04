package ru.tensor.sbis.design.person_suggest.suggest.contract

import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView

/**
 * Описание API для управления панелью выбора персоны [PersonSuggestView].
 * @see PersonSuggestKeyboardBehavior
 *
 * @author vv.chekurda
 */
interface PersonSuggestViewApi : PersonSuggestKeyboardBehavior {

    /**
     * Установить/получить данные для отображения.
     */
    var data: List<PersonSuggestData>

    /**
     * Проинициализировать компонент.
     * Необходимо вызвать перед использованием компонента.
     *
     * @param listener слушатель модели выбранной персоны.
     */
    fun init(
        listener: PersonSelectionListener
    )

    /**
     * Освободить ресурсы.
     * Вызывать только при переиспользовании объекта компонента на разных фрагментах/активити/вью,
     * чтобы предотвратить утечку слушателя.
     */
    fun release()
}

/**
 * Слушатель выбранной персоны.
 */
typealias PersonSelectionListener = (PersonSuggestData) -> Unit