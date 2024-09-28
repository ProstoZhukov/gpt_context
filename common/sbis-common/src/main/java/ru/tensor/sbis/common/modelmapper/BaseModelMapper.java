package ru.tensor.sbis.common.modelmapper;

import android.content.Context;
import androidx.annotation.NonNull;

import io.reactivex.functions.Function;

/**
 * @author am.boldinov
 */
public abstract class BaseModelMapper<T, M> implements Function<T, M> {

    @NonNull
    protected final Context mContext;

    public BaseModelMapper(@NonNull Context context) {
        mContext = context;
    }

}
