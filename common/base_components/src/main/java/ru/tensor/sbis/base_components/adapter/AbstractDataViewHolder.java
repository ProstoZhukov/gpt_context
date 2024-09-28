package ru.tensor.sbis.base_components.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Базовый Holder с методом заполнения данными из модели
 *
 * @param <T> тип модели данных
 *
 * @author am.boldinov
 */
public abstract class AbstractDataViewHolder<T> extends RecyclerView.ViewHolder {

    public AbstractDataViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * Заполнение данными из модели
     *
     * @param model модель данных
     */
    public void bind(@NonNull T model) {

    }
}