package ru.tensor.sbis.mvp.presenter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "rawtypes"})
public class PresenterLoader<PRESENTER extends BasePresenter> extends Loader<PRESENTER> {

    private PRESENTER mPresenter;

    public PresenterLoader(@NonNull Context context, @NonNull PRESENTER presenter) {
        super(context);
        mPresenter = presenter;
    }

    @Override
    protected void onStartLoading() {
        deliverResult(mPresenter);
    }

    @Override
    protected void onReset() {
        mPresenter.onDestroy();
        mPresenter = null;
    }

    /**
     * @SelfDocumented
     */
    @NonNull
    public PRESENTER getPresenter() {
        return mPresenter;
    }
}