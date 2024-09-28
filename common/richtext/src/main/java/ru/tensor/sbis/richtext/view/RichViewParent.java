package ru.tensor.sbis.richtext.view;

import android.content.Context;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import ru.tensor.sbis.design.collection_view.CollectionView;

/**
 * Интерфейс, описывающий методы и поведение родительской ViewGroup, в которую добавляется
 * дочерний {@link RichViewLayout.ViewHolder}.
 *
 * @author am.boldinov
 */
public interface RichViewParent extends ViewParent {

    /**
     * Возвращает контекст родительской View.
     */
    @NonNull
    Context getContext();

    /**
     * Возвращает пул для переиспользования дочерних вью-холдеров.
     */
    @NonNull
    CollectionView.RecycledViewPool getRecycledViewPool();

    /**
     * Возвращает интерфейс для клонирования уже добавленного в иерархию {@link RichViewLayout}
     * со всеми установленными свойствами.
     */
    @NonNull
    CloneableRichViewFactory getRichViewFactory();
}
