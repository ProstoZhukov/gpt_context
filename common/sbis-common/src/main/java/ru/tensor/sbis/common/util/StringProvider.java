package ru.tensor.sbis.common.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * @author sa.nikitin
 */
public class StringProvider {

    @NonNull
    private final Context mContext;

    public StringProvider(@NonNull Context context) {
        mContext = context;
    }

    @NonNull
    public String getString(@StringRes int stringRes) {
        return mContext.getString(stringRes);
    }
}
