package ru.tensor.sbis.common.listener;

import androidx.annotation.NonNull;

public interface ResultListener<T> {

    void onResultOk(@NonNull T result);

    void onResultCancel();

}
