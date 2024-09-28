package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactResult
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.mvp.search.SearchablePresenter
import ru.tensor.sbis.mvp.search.SearchableView
import java.util.UUID

/**
 * Контракт mvp добавления сотрудников внутри компании в реестр контактов.
 *
 * @author da.zhukov
 */
internal interface AddInternalEmployeesContract {
    /**
     * View контракт.
     */
    interface View : SearchableView<AddContactModel> {
        /**
         * Обработать результат выбора контакта.
         *
         * @param result модель выбранного контакта.
         */
        fun onAddContactResult(result: AddContactResult)

        /**
         * Показать ошибку.
         *
         * @param errorTextResId идентификатор ресурса сообщения с ошибкой.
         */
        fun showError(@StringRes errorTextResId: Int)

        /**
         * Открыть карточку сотрудника.
         *
         * @param contactUuid идентификатор сотрудника
         */
        fun openProfile(contactUuid: UUID)

        /**
         * Сообщить о закрытии выбора
         */
        fun showSelectionCancel()
    }

    /**
     * Контракт презентера.
     */
    interface Presenter : SearchablePresenter<View> {
        /**
         * Контакт из списка выбран.
         *
         * @param contact модель выбранного контакта.
         */
        fun onContactSelected(contact: AddContactModel)

        /**
         * Обработка клика по фотографии контакта.
         *
         * @param contact модель контакта.
         */
        fun onContactPhotoClicked(contact: AddContactModel)

        /**
         * Выбор закрывается.
         */
        fun onSelectionCancel()

        /**
         * Показывается ли заглушка.
         */
        fun stubIsShowing(): Boolean
    }
}