package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import io.reactivex.Single;
import kotlin.Pair;
import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper;
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubs;
import ru.tensor.sbis.communicator.contacts_registry.ui.filters.ContactFilterConfiguration;
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrder;
import ru.tensor.sbis.communicator.declaration.model.EntitledItem;
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener;
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle;
import ru.tensor.sbis.mvp.search.SearchablePresenter;
import ru.tensor.sbis.mvp.search.SearchableView;
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewBehaviour;

/** @SelfDocumented */
public interface ContactListContract {

    interface View extends SearchableView<ContactRegistryModel>, EmptyViewBehaviour {

        void showAddContactPane(@NonNull ArrayList<AddContactOption> options);

        void showChat(@NonNull UUID personUuid);

        void onCheckStateChanged(boolean hasCheckedContacts);

        void attachCheckHelper(@NonNull CheckHelper<ContactRegistryModel> checkHelper);

        void attachSelectionHelper(@NonNull SelectionHelper<ContactRegistryModel> selectionHelper);

        void importContacts();

        boolean checkShouldRequestContactsPermissions();

        void openAddInternalEmployeesScreen(@Nullable UUID folderUuid);

        void openFindContactsScreen(@Nullable UUID folderUuid);

        void openEmployees();

        void showContactInAnotherFolderAlready(String errorMessage);

        void setSearchText(@Nullable String searchText);

        void showSearchPanel();

        void showDefaultLoadingError();

        void showProgress();

        void showSuccessPopup(int stringId, @Nullable String icon);

        void showErrorPopup(int stringId, @Nullable String icon);

        void showInformationPopup(int stringId, @Nullable String icon);

        void showPopupWithPlurals(int stringId, @Nullable String icon, int count, SbisPopupNotificationStyle typeOfPopup);

        void showCheckMode();

        void hideCheckMode();

        void showContactDetailsScreen(@NonNull UUID contactUuid);

        /**
         * Установить заголовок папки в шапку.
         * @param title опциональный заголовок, null скрывает заголовок.
         */
        void setFolderTitle(@Nullable String title);

        /**
         * Свернуть список папок.
         */
        void setFoldersCompact();

        /**
         * Показать выбор папки для контакта.
         * @param currentFolder - текущая папка, в которой находится контакт.
         */
        void showFolderSelection(@Nullable UUID currentFolder);

        void showAddNewContactsDisabledMessage(@StringRes int messageStringId);

        void showBlockContactsDialog(@PluralsRes int messageStringId, int count);

        /**
         * Показать заглушку по результатам контроллера.
         * Необходимо передать null чтобы спрятать заглушку.
         * @param stub заглушка
         */
        void showStub(@Nullable ContactsStubs stub);

        /**
         * Only for tablets.
         */
        void hideContactDetails();

        void cancelDismiss(UUID uuid);

        void closeAllOpenSwipeMenus();

        void closeAllSwipeItems();

        /**
         * Очистить состояние свайп-меню.
         */
        void clearSwipeMenuState();

        void enableHeaders(boolean enable);

        void notifyContactRemoved(@NonNull UUID uuid, int position);

        void showFilterSelection(@NonNull ContactFilterConfiguration currentConfiguration);

        void changeFilterByType(@NonNull EntitledItem entitledItem);

        void setFabVisible(boolean isVisible);

        /**
         * Показать экран подтверждения номера телефона..
         */
        void showPhoneVerification();

        /**
         * Получить первый полностью видимый элемент спика.
         */
        int firstCompletelyVisibleItemPosition();

        /**
         * Скрыть фильтр в строке поиска.
         */
        void setFilterVisible(boolean isVisible);
    }

    interface Presenter extends SearchablePresenter<View>,
        ContactHolder.ContactItemsClickHandler,
        FolderActionListener {

        boolean onBackButtonClicked();

        void onAddContactBtnClick();

        void onRequestAddContactOptionResult(int optionResult);

        void onCheckModeCancelClicked();

        void onDismissOrDeleteContactClick(@NonNull UUID uuid);

        void onDismissedWithoutMessage(String uuid);

        void onDeleteCheckedClicked();

        void onMoveBySwipeClicked(@NonNull ContactsModel contact);

        void onSendMessageClicked(@NonNull ContactsModel contact);

        void onMoveCheckedClicked();

        void onBlockCheckedClicked();

        void onBlockContactsGranted();

        void onOrderChanged(@NonNull ContactSortOrder order);

        void onScrollToTopPressed();

        void onRootFolderSelected();

        void onContactTypeSelected(ContactSortOrder item);

        // workaround :(
        ContactSortOrder getCurrentOrder();

        void onBranchTypeTabClick(String navxId);

        void onNavigationDrawerStateChanged();

        void onFilterClicked();

        void setFoldersEnabled(boolean enabled);

        void syncFolders();

        void moveContactToNewFolder();

        /**
         * Устанавливает максимальное количество ячеек контактов на экране для заполненеия фильтра.
         */
        void setContactItemsMaxCountOnScreen(int count);

        Single<Pair<Boolean, Boolean>> isCanImportContacts();

        /**
         * Подтверждения номера телефона
         */
        void onPhoneVerificationRequired();

        /**
         * Убрать выделение контакта
         */
        void resetSelection();

        /**
         * Видимость вью для пользователя изменилась.
         */
        void onViewVisibilityChanged(boolean isInvisible);

        /**
         * Отправить аналитику при вызове окна папок.
         */
        void sendAnalyticOpenedContactsFolders();

        /**
         * Сохранить последнее состояние доступности вкладки сотрудники.
         */
        void setEmployeesTabNavIxIsGranted(boolean isGranted);

        /** @SelfDocumented */
        boolean employeesTabNavXIsGranted();
    }
}
