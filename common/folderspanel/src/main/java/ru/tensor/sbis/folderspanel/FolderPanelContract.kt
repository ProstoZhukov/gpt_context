package ru.tensor.sbis.folderspanel

import ru.tensor.sbis.mvp.fragment.selection.SelectionWindowContract

/**
 * Контракт, определяющий поведение View и Presenter для компонента папок
 */
interface FolderPanelContract {

    interface View : SelectionWindowContract.View {

        /**
         * Отображение списка папок
         * @param folders список папок (ViewModel-ей)
         */
        fun showFolders(folders: List<FolderViewModel>)

        /**
         * Отображение диалогового окна для ввода имени при создании/переименовании папки
         * @param folderName текущее имя папки (или пустая строка)
         */
        fun showPickNameDialog(folderName: String)

        /**
         * Отображение диалогового окна для выбора целевой папки при перемещении
         * @param folders список целевых папок для перемещения
         */
        fun showFolderPickDialog(folders: List<FolderViewModel>)

        /**
         * Отображение диалогового окна удаления папки
         * @param title - заголовок диалога
         * @param message - сообщение или null, если не требуется
         * @param acceptButtonText - текст кнопки подтверждения
         * @param cancelButtonText - текст кнопки отмены
         */
        fun showDeletionAcceptDialog(title: String, message: String?, acceptButtonText: String, cancelButtonText: String)

        /**
         * Отображение сообщения об ошибке
         * @param errorText текст ошибки
         */
        fun showError(errorText: CharSequence)

    }

    interface Presenter<V : View> : SelectionWindowContract.Presenter<V> {

        /**
         * Обработчик нажатия на кнопку создания папки
         */
        fun onNewFolderClick()

        /**
         * Установка текущей папки при инициализации
         * @param folder uuid папки
         */
        fun setInitFolder(folder: String)

        /**
         * Установка выбранной папки при смене папки
         * @param folder uuid папки
         */
        fun setFolderToSelect(folder: String)

        /**
         * Callback подтверждения переименования папки
         * @param name введенное имя папки
         */
        fun onNameAcceptedFromPickNameDialog(name: String)

        /**
         * Callback подтверждения удаления папки
         */
        fun onFolderDeletionAccepted()

        /**
         * Событие отмены удаления папки
         */
        fun onCancelFolderDeletion()

        /**
         * Callback подтверждения перемещения папки в другую папку
         * @param pickedFolder uuid выбранной (целевой) папки
         */
        fun onFolderPicked(pickedFolder: String)

    }
}