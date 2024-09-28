package ru.tensor.sbis.objectpool.impl;

import android.view.View;

import androidx.annotation.NonNull;
import ru.tensor.sbis.design.view_factory.ViewFactory;
import ru.tensor.sbis.objectpool.base.InflatableConcurrentObjectPool;

/**
 * Реализация пула view с использованием view factory.
 * @param <V> - тип view
 *
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ViewFactoryConcurrentObjectPool<V extends View> extends InflatableConcurrentObjectPool<V> {

    @NonNull
    private final ViewFactory<V> mFactory;

    public ViewFactoryConcurrentObjectPool(@NonNull ViewFactory<V> factory) {
        mFactory = factory;
    }

    public ViewFactoryConcurrentObjectPool(@NonNull ViewFactory<V> factory, int capacity) {
        super(capacity);
        mFactory = factory;
    }

    /** @SelfDocumented  */
    @NonNull
    @Override
    protected V createInstance() {
        return mFactory.createView();
    }

}
