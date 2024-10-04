package ru.tensor.sbis.design.list_utils.decoration.predicate;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.list_utils.decoration.Decoration;

import android.view.View;

/**
 * Реализация выбора элементов для декорирования на основе view type.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public abstract class ViewTypePredicate implements Decoration.Predicate {

    /**
     * Элемент, на который накладывается предикат.
     */
    public enum Target {
        PREVIOUS,
        CURRENT,
        NEXT
    }

    /**
     * Элемент, на который накладывается предикат.
     */
    private final Target mTarget;

    /**
     * Нужно ли декорировать элемент, если целевой элемент не найден.
     */
    private final boolean mDecorateIfTargetMissing;

    public ViewTypePredicate() {
        this(Target.CURRENT, false);
    }

    public ViewTypePredicate(@NonNull Target target, boolean decorateIfTargetMissing) {
        mTarget = target;
        mDecorateIfTargetMissing = decorateIfTargetMissing;
    }

    /**
     * Нужно ли декорировать view указанного типа.
     *
     * @param viewType - тип view
     * @return true - если элемент нужно декорировать, false - иначе
     */
    protected abstract boolean needToDecorate(int viewType);

    @SuppressWarnings("rawtypes")
    @Override
    public boolean needToDecorate(@NonNull View itemView,
                                  @NonNull RecyclerView parent,
                                  @NonNull RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        RecyclerView.ViewHolder childViewHolder = parent.getChildViewHolder(itemView);
        if (adapter == null || childViewHolder == null) return mDecorateIfTargetMissing;
        final int itemAdapterPosition;
        final int itemViewType;
        switch (mTarget) {
            case PREVIOUS:
                itemAdapterPosition = childViewHolder.getAdapterPosition();
                if (itemAdapterPosition > 0) {
                    itemViewType = adapter.getItemViewType(itemAdapterPosition - 1);
                } else {
                    return mDecorateIfTargetMissing;
                }
                break;
            case NEXT:
                itemAdapterPosition = childViewHolder.getAdapterPosition();
                if (itemAdapterPosition >= 0 && itemAdapterPosition < adapter.getItemCount() - 1) {
                    itemViewType = adapter.getItemViewType(itemAdapterPosition + 1);
                } else {
                    return mDecorateIfTargetMissing;
                }
                break;
            default:
                itemViewType = childViewHolder.getItemViewType();
                break;
        }
        return needToDecorate(itemViewType);
    }
}
