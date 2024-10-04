package ru.tensor.sbis.design.view_factory.collection;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Делегат для макета, отображающего коллекцию элементов с счетчиком оставшихся.
 * @param <L>   - тип макета
 * @param <T>   - тип view - элемента коллекции
 *
 * @author am.boldinov
 */
public abstract class CollectionLayoutDelegate<L extends ViewGroup & CollectionLayout<T>, T extends View> extends BindingLayoutDelegate<L> {

    /**
     * Кеш элементов коллекции.
     */
    @NonNull
    private final List<T> mItemViews = new ArrayList<>();

    /**
     * Кеш text view со счетчиком.
     */
    @Nullable
    private TextView mDiffView;

    /**
     * Конструктор для создания объекта {@link CollectionLayoutDelegate}.
     *
     * @param view view, который необходимо построить.
     * */
    public CollectionLayoutDelegate(@NonNull L view) {
        super(view);
    }

    // region Item views
    @NonNull
    protected List<T> getItemViews() {
        return mItemViews;
    }

    /**
     * Получить item view для указанной позиции.
     */
    @NonNull
    protected T getItemView(int position) {
        if (position < mItemViews.size()) {
            // Возвращаем item view из кеша
            return mItemViews.get(position);
        } else if (position == mItemViews.size()) {
            // Получаем item view от поставщика
            T itemView = takeItemView();
            // Добавляем в кеш
            mItemViews.add(itemView);
            // Вставляем в макет
            if (itemView.getParent() != layout) {
                layout.addChildInLayout(itemView, position, itemView.getLayoutParams(), true);
            }
            return itemView;
        }
        throw new IllegalArgumentException("Incorrect position for item view (" + position + ").");
    }

    /**
     * Запросить экземпляр item view.
     */
    @NonNull
    protected T takeItemView() {
        return layout.takeItemView();
    }

    /**
     * Удалить лишние item view.
     */
    protected void removeRedundantItemViews(int required) {
        // Удаляем все item view сверх требующихся
        if (required < mItemViews.size()) {
            for (int i = mItemViews.size() - 1; i > required - 1; i--) {
                T redundant = mItemViews.remove(i);
                layout.removeViewInLayout(redundant);
                layout.releaseItemView(redundant);
            }
        }
    }
    // endregion

    // region Diff view
    /**
     * Получить diff view (из кеша или из менеджера).
     */
    @NonNull
    protected TextView getDiffView() {
        if (mDiffView == null) {
            mDiffView = layout.takeDiffView();
            if (mDiffView.getParent() != layout) {
                layout.addChildInLayout(mDiffView, -1, mDiffView.getLayoutParams(), true);
            }
        }
        return mDiffView;
    }

    /**
     * Удалить diff view (из макета и из кеша).
     */
    protected void removeDiffView() {
        if (mDiffView != null) {
            layout.removeViewInLayout(mDiffView);
            layout.releaseDiffView(mDiffView);
            mDiffView = null;
        }
    }
    // endregion

    /**
     * Высвободить все item view и diff view.
     */
    public void releaseViews() {
        // Удаляем diff view
        removeDiffView();
        // Возвращаем все person view
        for (T itemView : mItemViews) {
            layout.removeView(itemView);
            layout.releaseItemView(itemView);
        }
        mItemViews.clear();
        // Запрашиваем биндинг на следующий onMeasure
        requestBinding();
    }

}
