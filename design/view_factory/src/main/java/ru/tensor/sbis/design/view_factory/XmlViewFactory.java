package ru.tensor.sbis.design.view_factory;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Базовая реализация {@link AbstractViewFactory} для создания {@link View}
 * с использованием .XML файлов разметки
 *
 * @author am.boldinov
 */
public abstract class XmlViewFactory<V extends View> extends AbstractViewFactory<V> {

    /** @SelfDocumented */
    public XmlViewFactory(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public V createView() {
        return (V) getLayoutInflater().inflate(getLayoutRes(), getParent(), false);
    }

    @LayoutRes
    protected abstract int getLayoutRes();
}
