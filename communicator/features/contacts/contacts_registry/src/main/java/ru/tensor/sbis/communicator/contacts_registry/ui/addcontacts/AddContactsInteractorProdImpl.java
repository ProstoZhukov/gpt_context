package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import ru.tensor.sbis.common.generated.CommandStatus;
import ru.tensor.sbis.common.generated.ErrorCode;
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.mapper.AddContactModelMapper;
import ru.tensor.sbis.mvp.interactor.BaseInteractor;

/**
 * Базовая реализация интерактора добавления новых контактов в реестр контактов
 * @see AddContactsInteractor
 *
 * @author da.zhukov
 */
public abstract class AddContactsInteractorProdImpl extends BaseInteractor implements AddContactsInteractor {

    @NonNull
    protected final ContactsControllerWrapper mContactsControllerWrapper;

    @NonNull
    protected final AddContactModelMapper mModelMapper;

    protected AddContactsInteractorProdImpl(
            @NonNull ContactsControllerWrapper contactsControllerWrapper,
            @NonNull AddContactModelMapper addContactModelMapper
    ) {
        super();
        mContactsControllerWrapper = contactsControllerWrapper;
        mModelMapper = addContactModelMapper;
    }

    @NonNull
    @Override
    public Observable<AddContactResult> addContact(
            @NonNull UUID contactUuid,
            @Nullable UUID folderUuid
    ) {
        return Observable.fromCallable(
                () -> {
                    CommandStatus status = mContactsControllerWrapper.addContact(contactUuid, folderUuid);
                    return new AddContactResult(status.getErrorCode() == ErrorCode.SUCCESS, status.getErrorMessage());
                }
        ).compose(getObservableBackgroundSchedulers());
    }

}
