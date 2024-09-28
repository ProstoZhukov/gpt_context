package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations;

import android.content.SharedPreferences;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** @SelfDocumented */
public class ConversationSettings {

    private static final String DIALOGS_FILTER_STATE_PREFERENCE_KEY = ConversationSettings.class.getSimpleName() + ".DIALOGS_FILTER_STATE";
    private static final String CHATS_FILTER_STATE_PREFERENCE_KEY = ConversationSettings.class.getSimpleName() + ".CHATS_FILTER_STATE";
    private static final String PERSON_ID_PREFERENCE_KEY = ConversationSettings.class.getSimpleName() + ".PERSON_ID";

    public static final int DIALOGS_FILTER_ALL = 0;
    public static final int DIALOGS_FILTER_INCOME = 1;
    public static final int DIALOGS_FILTER_NOT_READ = 2;
    public static final int DIALOGS_FILTER_NO_ANSWER = 3;
    public static final int DIALOGS_FILTER_ARCHIVE = 4;
    public static final int DIALOGS_FILTER_DELETED = 5;

    public static final int CHATS_FILTER_ALL = 0;
    public static final int CHATS_FILTER_NOT_READ = 1;
    public static final int CHATS_FILTER_HIDDEN = 2;

    @NonNull
    private final SharedPreferences mSharedPreferences;

    public ConversationSettings(@NonNull SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    //region Dialogs
    public void saveDialogsFilterState(@DialogsFilterState int state, @Nullable String personId) {
        mSharedPreferences.edit()
                .putInt(DIALOGS_FILTER_STATE_PREFERENCE_KEY, state)
                .putString(PERSON_ID_PREFERENCE_KEY, personId)
                .apply();
    }

    @DialogsFilterState
    public int getDialogsFilterState(@Nullable String personId) {
        if (!sameAccount(personId)) return DIALOGS_FILTER_ALL;
        return mSharedPreferences.getInt(DIALOGS_FILTER_STATE_PREFERENCE_KEY, DIALOGS_FILTER_ALL);
    }
    //endregion

    //region Chats
    public void saveChatsFilterState(@ChatsFilterState int state, @Nullable String personId) {
        mSharedPreferences.edit()
                .putInt(CHATS_FILTER_STATE_PREFERENCE_KEY, state)
                .putString(PERSON_ID_PREFERENCE_KEY, personId)
                .apply();
    }

    @ChatsFilterState
    public int getChatsFilterState(@Nullable String personId) {
        if (!sameAccount(personId)) return CHATS_FILTER_ALL;
        return mSharedPreferences.getInt(CHATS_FILTER_STATE_PREFERENCE_KEY, CHATS_FILTER_ALL);
    }
    //endregion

    private boolean sameAccount(@Nullable String personId) {
        return mSharedPreferences.getString(PERSON_ID_PREFERENCE_KEY, StringUtils.EMPTY)
                .equals(personId);
    }

    //region Annotations
    @IntDef(
            {
                    DIALOGS_FILTER_ALL,
                    DIALOGS_FILTER_INCOME,
                    DIALOGS_FILTER_NOT_READ,
                    DIALOGS_FILTER_NO_ANSWER,
                    DIALOGS_FILTER_ARCHIVE,
                    DIALOGS_FILTER_DELETED
            }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogsFilterState {

    }

    @IntDef(
            {
                    CHATS_FILTER_ALL,
                    CHATS_FILTER_NOT_READ,
                    CHATS_FILTER_HIDDEN
            }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatsFilterState {

    }
    //endregion

}
