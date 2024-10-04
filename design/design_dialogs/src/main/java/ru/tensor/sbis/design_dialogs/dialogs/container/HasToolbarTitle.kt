package ru.tensor.sbis.design_dialogs.dialogs.container

/**
 * Интерфейс, декларирующий возможность изменять заголовок тулбара
 *
 * @author sa.nikitin
 */
interface HasToolbarTitle {

    /**
     * Изменить заголовок тулбара
     *
     * @param title Заголовок тулбара
     */
    fun changeToolbarTitle(title: String?)
}