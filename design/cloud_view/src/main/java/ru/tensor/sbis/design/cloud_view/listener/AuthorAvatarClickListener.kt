package ru.tensor.sbis.design.cloud_view.listener

import ru.tensor.sbis.design.cloud_view.model.PersonModel

/**
 * Класс обработчик нажатия на фото автора сообщения.
 *
 * @author vv.chekurda
 */
interface AuthorAvatarClickListener {

    /**
     * Обработчики нажатия на фото автора.
     */
    fun onAvatarClicked(model: PersonModel)
}