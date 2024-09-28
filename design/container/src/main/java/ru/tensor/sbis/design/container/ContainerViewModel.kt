package ru.tensor.sbis.design.container

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для взаимодействия контейнера и прикладного кода
 * @author ma.kolpakov
 */
interface ContainerViewModel {
    /**
     * Поток, отправляющий события закрытия контейнера по нажатию вне контейнера.
     */
    val onCancelContainer: Flow<Unit>

    /**
     * Поток, отправляющий события закрытия контейнера любым способом
     * (по кнопкам, нажатие снаружи, вызов [closeContainer]).
     * Не отправляет событие при закрытии системой (при повороте
     * экрана и т.п.).
     */
    val onDismissContainer: Flow<Unit>

    /**
     * Закрыть контейнер
     */
    fun closeContainer()

    /**
     * Обновить креэйтор внутри контейнера с пересозданием контейнера с новым контентом.
     */
    fun showNewContent(contentCreator: ContentCreator<Content>)

    /**
     * Обновить креэйтор внутри контейнера без пересоздания текущего контейнера.
     */
    fun setNewContent(contentCreator: ContentCreator<Content>)
}