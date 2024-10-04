package ru.tensor.sbis.design.collection_view.adapter;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.design.collection_view.CollectionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Базовая реализация {@link CollectionView.Adapter} для вью, в которых коллекция представляется ввиде списка элементов.
 *
 * @param <T>   - тип элементов списка данных
 * @param <VH>  - тип вью-холдеров дочерних элементов
 *
 * @author am.boldinov
 */
@SuppressWarnings("JavaDoc")
public abstract class AbstractListViewAdapter<T, VH extends CollectionView.ViewHolder> extends CollectionView.Adapter<VH> {

    /**
     * Коллекция данных. Предполагается, что нет нулевых элементов.
     */
    private List<T> mCollection = Collections.emptyList();

    /** @SelfDocumented */
    public AbstractListViewAdapter() { }

    @SuppressWarnings("unused")
    @Override
    public int getItemCount() {
        return mCollection.size();
    }

    /**
     * Получить элемент на указанной позиции в коллекции.
     *
     * @param position - позиция элемента в коллекции
     * @return элемент списка
     */
    @SuppressWarnings("unused")
    @NonNull
    public T getItem(int position) {
        if (position < 0 || position >= mCollection.size()) {
            throw new IndexOutOfBoundsException("Attempt to get item for position " + position + " while collection size is " + mCollection.size());
        }
        T item = mCollection.get(position);
        if (item == null) {
            throw new IllegalStateException("Item on " + position + " position is null.");
        }
        return item;
    }

    /**
     * Получить список элементов
     *
     * @return список элементов
     */
    public List<T> getItems() {
        return new ArrayList<>(mCollection);
    }

    /**
     * Задать данные для отображения в виде коллекции.
     *
     * @param data - данные
     */
    @SuppressWarnings("unused")
    @MainThread
    public void setData(@Nullable List<T> data) {
        if (data == null) {
            mCollection = Collections.emptyList();
        } else {
            mCollection = data;
        }
        notifyDataChanged();
    }

}
