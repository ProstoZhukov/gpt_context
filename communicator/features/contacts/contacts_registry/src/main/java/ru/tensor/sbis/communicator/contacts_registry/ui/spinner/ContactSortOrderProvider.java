package ru.tensor.sbis.communicator.contacts_registry.ui.spinner;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by aa.mironychev on 19.06.17.
 */

public class ContactSortOrderProvider  {

    private static final String PREFERENCE_NAME = "CONTACT_LIST_SORT_ORDER.PREFERENCE";
    private static final String SELECTED_ORDER_KEY = "SELECTED_ORDER";

    private static final ContactSortOrder DEFAULT_ORDER = ContactSortOrder.BY_LAST_MESSAGE_DATE;

    private final Context mContext;

    public ContactSortOrderProvider(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    public void set(@Nullable ContactSortOrder order) {
        getSharedPreferences(mContext)
                .edit()
                .putString(SELECTED_ORDER_KEY, order != null ? order.name() : null)
                .apply();
    }

    @NonNull
    public ContactSortOrder get()  {
        ContactSortOrder order = getPersisted(mContext);
        return order != null ? order : DEFAULT_ORDER;
    }

    @Nullable
    private static ContactSortOrder getPersisted(@NonNull Context context) {
        String persistedValue = getSharedPreferences(context)
                .getString(SELECTED_ORDER_KEY, null);
        try {
            return ContactSortOrder.valueOf(persistedValue);
        } catch (Exception e) {
            return null;
        }
    }

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

}
