package ru.tensor.sbis.design.view_factory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Базовая реализация интерфейса {@link ViewFactory}
 *
 * @author am.boldinov
 */
public abstract class AbstractViewFactory<V extends View> implements ViewFactory<V>{

    @NonNull
    private final Context mContext;
    @Nullable
    private final ViewGroup mParent;

    /** @SelfDocumented */
    public AbstractViewFactory(@NonNull Context context) {
        mContext = context;
        mParent = createParent(context);
    }

    @NonNull
    protected Context getContext() {
        return mContext;
    }

    @Nullable
    protected ViewGroup getParent() {
        return mParent;
    }

    @Nullable
    protected ViewGroup createParent(@NonNull Context context) {
        return new ViewGroup(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // do nothing
            }
        };
    }

    @NonNull
    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

}
