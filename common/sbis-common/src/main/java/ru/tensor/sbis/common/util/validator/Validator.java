package ru.tensor.sbis.common.util.validator;

import androidx.annotation.NonNull;

/**
 * Created by am.boldinov on 13.11.15.
 */
interface Validator<E> {
    boolean validate(@NonNull E obj);
}
