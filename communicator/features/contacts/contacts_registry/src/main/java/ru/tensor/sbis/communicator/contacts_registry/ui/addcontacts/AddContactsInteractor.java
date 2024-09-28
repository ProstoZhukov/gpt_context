package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

import io.reactivex.Observable;

/**
 * Интерактор добавления контактов
 *
 * @author da.zhukov
 */
public interface AddContactsInteractor {

    @NonNull
    Observable<AddContactResult> addContact(@NonNull UUID contactUuid, @Nullable UUID folderUuid);
}
