package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactResult
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView
import java.util.UUID

/**
 * Контракт mvp для экрана добавления контакта в реестр контактов.
 *
 * @author da.zhukov
 */
internal interface AddNewContactsContract {
    /**
     * View контракт.
     */
    interface View : BaseTwoWayPaginationView<AddContactModel> {
        /**
         * Обработка результата выбранного контакта.
         *
         * @param result модель результата выбранного контакта.
         */
        fun onAddContactResult(result: AddContactResult)

        /**
         * Показать ошибку.
         *
         * @param errorTextResId идентификатор ресурса сообщения об ошибке.
         */
        fun showError(@StringRes errorTextResId: Int)

        /**
         * Показать карточку сотрудника.
         *
         * @param profileUuid идентификатор профиля сотрудника.
         */
        fun openProfile(profileUuid: UUID)

        /**
         * Сообщить о закрытии выбора.
         */
        fun showSelectionCancel()

        /**
         * Очистить строку поиска по имени.
         */
        fun clearSearchFieldName()

        /**
         * Очистить строку поиска по номеру телефона.
         */
        fun clearSearchFieldPhone()

        /**
         * Очистить строку поиска по e-mail.
         */
        fun clearSearchFieldEmail()

        /**
         * Опустить клавиатуру и очистить фокус.
         */
        fun hideKeyboardAndClearFocus()

        /**
         * Показать информационное окно с предложением ограничения поискового запроса.
         */
        fun showSearchRestrictionAlert()

        /**
         * Установить цвет фона для поиска по ФИО красным.
         * @param isErrorColor подсветить красным true/false.
         */
        fun setSearchNameBackgroundColor(isErrorColor: Boolean)

        /**
         * Установка состояния нажатой кнопки поиска.
         */
        fun setWasSearchButtonClickedFlag()
    }

    /**
     * Контракт презентера.
     */
    interface Presenter : BaseTwoWayPaginationPresenter<View> {
        /**
         * Обработать клик по кнопке поиска.
         */
        fun onSearchButtonClicked()

        /**
         * Обработать изменение строки поиска по имени.
         *
         * @param nameQuery имя в поисковой строке.
         */
        fun onSearchFieldNameQueryChanged(nameQuery: String)

        /**
         * Обработать изменение поисковой строки с номером телефона.
         *
         * @param phoneQuery номер в поисковой строке.
         */
        fun onSearchFieldPhoneQueryChanged(phoneQuery: String)

        /**
         * Обработать изменение поисковой строки с e-mail.
         *
         * @param emailQuery e-mail в поисковой строке.
         */
        fun onSearchFieldEmailQueryChanged(emailQuery: String)

        /**
         * Очиститка поисковой строки с именем.
         */
        fun onSearchFieldNameClearButtonClicked()

        /**
         * Очестка поисковой строки с телефоном.
         */
        fun onSearchFieldPhoneClearButtonClicked()

        /**
         * Очистка поисковой строки с e-mail.
         */
        fun onSearchFieldEmailClearButtonClicked()

        /**
         * Контакт из списка выбран.
         *
         * @param contact модель контакта.
         */
        fun onContactSelected(contact: AddContactModel)

        /**
         * Обработка нажатия на фотографию контакта.
         *
         * @param contact модель контакта.
         */
        fun onContactPhotoClicked(contact: AddContactModel)

        /**
         * Обработка закрытия выбора.
         */
        fun onSelectionCancel()
    }
}