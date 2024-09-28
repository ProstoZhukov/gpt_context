package ru.tensor.sbis.richtext.span.view.image;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.richtext.span.view.CollectionAttributesVM;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.RichViewParent;

/**
 * Вью-модель атрибутов для биндинга несколько идущих подряд изображений
 *
 * @author am.boldinov
 */
public final class ImageListAttributesVM extends CollectionAttributesVM<ImageAttributesVM> {

    public ImageListAttributesVM(@NonNull String tag) {
        super(tag);
    }

    @NonNull
    @Override
    public RichViewLayout.ViewHolderFactory createViewHolderFactory() {
        return new RichViewLayout.ViewHolderFactory() {

            @NonNull
            private final RecyclerView.RecycledViewPool mViewPool = new RecyclerView.RecycledViewPool();

            @SuppressWarnings("rawtypes")
            @NonNull
            @Override
            public RichViewLayout.ViewHolder createViewHolder(@NonNull RichViewParent parent) {
                final RecyclerView recyclerView = new RecyclerView(parent.getContext());
                recyclerView.setLayoutParams(new RichViewLayout.LayoutParams(RichViewLayout.LayoutParams.MATCH_PARENT, RichViewLayout.LayoutParams.WRAP_CONTENT));
                recyclerView.setHorizontalScrollBarEnabled(true);
                return new ImageListViewHolder(recyclerView, mViewPool);
            }
        };
    }
}
