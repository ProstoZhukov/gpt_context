package ru.tensor.sbis.design.cloud_view.listener

import ru.tensor.sbis.design.cloud_view.model.PersonModel

/**
 * Класс обработчик нажатия на имя автора сообщения.
 *
 * @author vv.chekurda
 */
interface AuthorNameClickListener {

    /**
     * Обработчики нажатия на имя автора.
     */
    fun onNameClicked(model: PersonModel)
}