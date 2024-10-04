package ru.tensor.sbis.design.confirmation_dialog

import ru.tensor.sbis.design.container.SbisContainerImpl

/**
 * Интерфейс обрабтки результатов нажатия на кнопку в диалоге подтврждения. Необходимо реализовать данный
 * интерфейс в вызывающем фрагменте или актиавити.
 *
 * @author ma.kolpakov
 */
interface ConfirmationButtonHandler {

    /**
     * Вызывается при нажатии на любую из функциональных кнопок.
     *
     * @param tag идетификатор диалога
     * @param id идетификатор нажатой кнопки
     * @param sbisContainer viewModel контейнера, с ее помощью можно например закрыть контейнер при нажатии.
     */
    fun onButtonClick(tag: String?, id: String, sbisContainer: SbisContainerImpl)
}