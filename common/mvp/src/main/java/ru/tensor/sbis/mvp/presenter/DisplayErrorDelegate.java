package ru.tensor.sbis.mvp.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Делегат для отображения ошибок.
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface DisplayErrorDelegate {

    /**
     * @SelfDocumented
     */
    void showLoadingError(@StringRes int errorTextResId);

    /**
     * @SelfDocumented
     */
    void showLoadingError(@NonNull String errorText);

}
