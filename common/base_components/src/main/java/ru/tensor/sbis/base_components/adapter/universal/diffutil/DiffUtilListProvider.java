package ru.tensor.sbis.base_components.adapter.universal.diffutil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
public interface DiffUtilListProvider<T> {

    @NonNull
    List<T> provideList();
}
