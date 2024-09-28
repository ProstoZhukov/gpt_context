package ru.tensor.sbis.recipient_selection.profile.ui.resultmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionResultDataContract;
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionResultManagerContract;
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager;
import ru.tensor.sbis.persons.ContactVM;

/**
 * Менеджер, отвечающий за обработку данных об изменении списка получателей.
 */
public class RecipientSelectionResultManager extends MultiSelectionResultManager<RecipientSelectionResultData> implements RecipientSelectionResultManagerContract {

    private static final RecipientSelectionResultData EMPTY_RESULT_DATA = new RecipientSelectionResultData(RecipientSelectionResultData.RESULT_CLEARED, new ArrayList<>());

    private boolean mIncompleteRecipients;

    /** @SelfDocumented */
    public RecipientSelectionResultManager() {
        super();
        mSelectionSubject.onNext(EMPTY_RESULT_DATA);
    }

    /** @SelfDocumented */
    @Override
    public void clearSelectionResult() {
        mIncompleteRecipients = false;
        mSelectionSubject.onNext(EMPTY_RESULT_DATA);
    }

    /** @SelfDocumented */
    @Override
    public boolean isIncompleteRecipients() {
        return mIncompleteRecipients;
    }

    /** @SelfDocumented */
    @Override
    public void addRecipient(@NonNull UUID recipientUuid) {
        mIncompleteRecipients = true;
        ContactVM contactVM = new ContactVM();
        contactVM.setUUID(recipientUuid);
        List<ContactVM> contactModels = mSelectionSubject.getValue().getAllContacts();
        contactModels.add(contactVM);
        RecipientSelectionResultData selectionResult = new RecipientSelectionResultData();
        selectionResult.setContactResultList(contactModels);
        mSelectionSubject.onNext(selectionResult);
    }

    /** @SelfDocumented */
    @Override
    public void putNewDataAsUuidList(@Nullable List<UUID> recipientUuids) {
        mIncompleteRecipients = true;
        if (recipientUuids == null) {
            mSelectionSubject.onNext(EMPTY_RESULT_DATA);
        } else {
            final int size = recipientUuids.size();
            List<ContactVM> contactModels = new ArrayList<>(size);
            ContactVM contactVM;
            for (int i = 0; i < size; i++) {
                contactVM = new ContactVM();
                contactVM.setUUID(recipientUuids.get(i));
                contactModels.add(contactVM);
            }
            RecipientSelectionResultData selectionResult = new RecipientSelectionResultData();
            selectionResult.setContactResultList(contactModels);
            mSelectionSubject.onNext(selectionResult);
        }
    }

    /** @SelfDocumented */
    public void putNewDataAsContactList(@Nullable List<ContactVM> recipients) {
        if (recipients == null) {
            mSelectionSubject.onNext(EMPTY_RESULT_DATA);
        } else {
            RecipientSelectionResultData selectionResult = new RecipientSelectionResultData();
            selectionResult.setContactResultList(recipients);
            mSelectionSubject.onNext(selectionResult);
        }
    }

    /** @SelfDocumented */
    @Override
    public void  putResultCanceled() {
        mSelectionSubject.onNext(new RecipientSelectionResultData(RecipientSelectionResultData.RESULT_CANCELED, null));
    }

    /** @SelfDocumented */
    @NonNull
    @Override
    public RecipientSelectionResultDataContract selectionResult() {
        return super.getSelectionResult();
    }

    /** @SelfDocumented */
    @NonNull
    @Override
    public Observable<RecipientSelectionResultDataContract> selectionDoneObservable() {
        return super.getSelectionDoneObservable().map(recipientSelectionResultData -> recipientSelectionResultData);
    }
}
