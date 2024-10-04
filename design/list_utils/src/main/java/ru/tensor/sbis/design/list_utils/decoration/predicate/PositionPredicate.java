package ru.tensor.sbis.design.list_utils.decoration.predicate;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.list_utils.decoration.Decoration;

import android.view.View;

/**
 * Реализация выбора элементов для декорирования на основе их позиции в списке.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public abstract class PositionPredicate implements Decoration.Predicate {

    /**
     * Нужно ли декорировать view на указанной позиции.
     *
     * @param adapterPosition - позиция элемента в списке
     * @param state           - состояние recycler view
     * @return true - если элемент нужно декорировать, false - иначе
     */
    protected abstract boolean needToDecorateItem(int adapterPosition,
                                                  @NonNull RecyclerView.State state);

    @Override
    public final boolean needToDecorate(@NonNull View itemView,
                                        @NonNull RecyclerView parent,
                                        @NonNull RecyclerView.State state) {
        return needToDecorateItem(parent.getChildAdapterPosition(itemView), state);
    }

}
