package ru.tensor.sbis.design.list_utils.decoration.predicate.position;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.tensor.sbis.design.list_utils.decoration.predicate.PositionPredicate;

/**
 * Реализация выбора элементов декорирования на основе фиксированной позиций в списке.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class SinglePositionPredicate extends PositionPredicate {

    /**
     * Позиция, которую необходимо декорировать.
     */
    private final int mPosition;

    public SinglePositionPredicate(int position) {
        mPosition = position;
    }

    @Override
    protected boolean needToDecorateItem(int adapterPosition, @NonNull RecyclerView.State state) {
        return adapterPosition == mPosition;
    }

}
