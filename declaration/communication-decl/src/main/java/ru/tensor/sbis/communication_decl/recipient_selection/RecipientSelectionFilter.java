package ru.tensor.sbis.communication_decl.recipient_selection;

import static androidx.core.content.res.ResourcesCompat.ID_NULL;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.ALWAYS_ADD_MODE;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.CONTAINS_WORKING_GROUPS;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.CONVERSATION_TYPE;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.DIALOG_UUID;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.DOCUMENT_UUID;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.EXCLUDE_PARTICIPANTS;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.IS_NEW_CONVERSATION;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.IS_SINGLE_CHOICE;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.IS_SWIPE_BACK_ENABLED;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.NEED_CLOSE_BUTTON;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.NEED_CLOSE_ON_COMPLETE;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.NEW_GROUP_CLICK_LISTER;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.ONLY_PARTICIPANTS;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.REQUEST_CODE;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.RESULT_CAN_BE_EMPTY;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.RESULT_MAX_COUNT;
import static ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.THEME_RES;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import java.io.Serializable;
import java.util.UUID;

import ru.tensor.sbis.communication_decl.model.ConversationType;

public class RecipientSelectionFilter {

    @NonNull
    private Bundle mBundle = new Bundle();

    /**
     * @param isSingleChoice        pass true to show selection with ability to choose only one contact, and pass false for multiply choose
     * @param resultCanBeEmpty      pass true if there is need to enable the select done button when no contact is selected
     * @param containsWorkingGroups pass true if recipient selection result should't contains working groups
     * @param onlyParticipants      pass true to show only dialog participants for selection
     * @param isNewConversation     pass true if dialog is new
     * @param resultMaxCount        be sure to specify this option to limit the number of selected contacts
     * @param documentUuid          be sure to specify this parameter to form the correct sagest in places where there is a document (tasks, disk, conversation on the document, etc.), pass null if there is no document
     * @param dialogUuid            specify this parameter it if there is old conversation, in other cases pass null
     */
    public RecipientSelectionFilter(
        boolean isSingleChoice,
        boolean resultCanBeEmpty,
        boolean containsWorkingGroups,
        boolean onlyParticipants,
        boolean excludeParticipants,
        boolean isNewConversation,
        int resultMaxCount,
        @Nullable String documentUuid,
        @Nullable UUID dialogUuid
    ) {
        with(IS_SINGLE_CHOICE, isSingleChoice);
        with(RESULT_CAN_BE_EMPTY, resultCanBeEmpty);
        with(CONTAINS_WORKING_GROUPS, containsWorkingGroups);
        with(ONLY_PARTICIPANTS, onlyParticipants);
        with(EXCLUDE_PARTICIPANTS, excludeParticipants);
        with(IS_NEW_CONVERSATION, isNewConversation);
        with(RESULT_MAX_COUNT, resultMaxCount);
        with(DOCUMENT_UUID, documentUuid);
        with(DIALOG_UUID, dialogUuid);
        mBundle.putSerializable(NEW_GROUP_CLICK_LISTER.key(), null);
    }

    public RecipientSelectionFilter(
            boolean isSingleChoice,
            boolean resultCanBeEmpty,
            boolean containsWorkingGroups,
            boolean onlyParticipants,
            boolean excludeParticipants,
            boolean isNewConversation,
            int resultMaxCount,
            @Nullable String documentUuid,
            @Nullable UUID dialogUuid,
            ConversationType conversationType
    ) {
        this(
                isSingleChoice,
                resultCanBeEmpty,
                containsWorkingGroups,
                onlyParticipants,
                excludeParticipants,
                isNewConversation,
                resultMaxCount,
                documentUuid,
                dialogUuid
        );
        with(CONVERSATION_TYPE, conversationType);
    }

    public RecipientSelectionFilter(
        boolean isSingleChoice,
        boolean resultCanBeEmpty,
        boolean containsWorkingGroups,
        boolean onlyParticipants,
        boolean excludeParticipants,
        boolean isNewConversation,
        int resultMaxCount,
        @Nullable String documentUuid,
        @Nullable UUID dialogUuid,
        boolean needCloseOnComplete,
        boolean needCloseButton,
        boolean isSwipeBackEnabled,
        @StyleRes int themeRes
    ) {
        this(isSingleChoice, resultCanBeEmpty, containsWorkingGroups, onlyParticipants,
            excludeParticipants, isNewConversation, resultMaxCount, documentUuid, dialogUuid);

        with(NEED_CLOSE_ON_COMPLETE, needCloseOnComplete);
        with(NEED_CLOSE_BUTTON, needCloseButton);
        with(IS_SWIPE_BACK_ENABLED, isSwipeBackEnabled);
        with(THEME_RES, themeRes);
    }

    public RecipientSelectionFilter(@NonNull Bundle bundle) {
        mBundle = bundle;
    }

    @NonNull
    public Bundle getBundle() {
        return mBundle;
    }

    public boolean isSingleChoice() {
        return getBoolean(IS_SINGLE_CHOICE);
    }

    public boolean canResultBeEmpty() {
        return getBoolean(RESULT_CAN_BE_EMPTY);
    }

    public boolean containsWorkingGroups() {
        return getBoolean(CONTAINS_WORKING_GROUPS);
    }

    public boolean isOnlyParticipants() {
        return getBoolean(ONLY_PARTICIPANTS);
    }

    public boolean isExcludeParticipants() {
        return getBoolean(EXCLUDE_PARTICIPANTS);
    }

    public boolean isNewConversation() {
        return getBoolean(IS_NEW_CONVERSATION);
    }

    public int getResultMaxCount() {
        return getInt(RESULT_MAX_COUNT);
    }

    @Nullable
    public ConversationType getConversationType() {
        return (ConversationType) getSerializable(CONVERSATION_TYPE);
    }

    @Nullable
    public String getDocumentUuid() {
        return getString(DOCUMENT_UUID);
    }

    @Nullable
    public UUID getDialogUuid() {
        return (UUID) getSerializable(DIALOG_UUID);
    }

    public boolean needCloseOnComplete() {
        return mBundle.getBoolean(NEED_CLOSE_ON_COMPLETE.key(), true);
    }

    public boolean needCloseButton() {
        return mBundle.getBoolean(NEED_CLOSE_BUTTON.key(), true);
    }

    public boolean isSwipeBackEnabled() {
        return mBundle.getBoolean(IS_SWIPE_BACK_ENABLED.key(), false);
    }

    public int getRequestCode() {
        return mBundle.getInt(REQUEST_CODE.key(), -1);
    }

    @StringRes
    public int getThemeRes() {
        return mBundle.getInt(THEME_RES.key(), ID_NULL);
    }

    public RecipientSelectionFilter setAlwaysAddMode(boolean alwaysAdd) {
        mBundle.putBoolean(ALWAYS_ADD_MODE.key(), alwaysAdd);
        return this;
    }

    public RecipientSelectionFilter setRequestCode(int requestCode) {
        mBundle.putInt(REQUEST_CODE.key(), requestCode);
        return this;
    }

    private void with(FilterKey key, Serializable value) {
        mBundle.putSerializable(key.key(), value);
    }

    private void with(FilterKey key, Boolean value) {
        mBundle.putBoolean(key.key(), value);
    }

    private void with(FilterKey key, int value) {
        mBundle.putInt(key.key(), value);
    }

    private void with(FilterKey key, String value) {
        mBundle.putString(key.key(), value);
    }

    private Serializable getSerializable(FilterKey key) {
        return (Serializable) mBundle.get(key.key());
    }

    private boolean getBoolean(FilterKey key) {
        return mBundle.getBoolean(key.key());
    }

    private int getInt(FilterKey key) {
        return mBundle.getInt(key.key());
    }

    private String getString(FilterKey key) {
        return mBundle.getString(key.key());
    }
}
