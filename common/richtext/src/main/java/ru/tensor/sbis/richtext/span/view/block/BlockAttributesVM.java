package ru.tensor.sbis.richtext.span.view.block;

import android.graphics.drawable.Drawable;
import android.text.Spannable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.span.view.ContentAttributesVM;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * Вью-модель атрибутов для биндинга блока богатого текста с иконкой
 *
 * @author am.boldinov
 */
public final class BlockAttributesVM extends ContentAttributesVM {

    private Spannable mContent;
    @Nullable
    private final Drawable mIcon;

    public BlockAttributesVM(@NonNull String tag, @Nullable Drawable icon) {
        super(tag);
        mIcon = icon;
    }

    @Override
    protected int size() {
        return 1;
    }

    /**
     * Устанавливает стилизованный контент блока
     */
    public void setContent(@NonNull Spannable content) {
        mContent = content;
    }

    @NonNull
    @Override
    protected Spannable getContent(int index) {
        return mContent;
    }

    @NonNull
    @Override
    public RichViewLayout.ViewHolderFactory createViewHolderFactory() {
        return (parent) -> {
            final BlockViewLayout layout = new BlockViewLayout(parent.getContext(), parent.getRichViewFactory());
            layout.setLayoutParams(new RichViewLayout.LayoutParams(RichViewLayout.LayoutParams.MATCH_PARENT, RichViewLayout.LayoutParams.WRAP_CONTENT));
            return new BlockViewHolder(layout);
        };
    }

    private static final class BlockViewHolder extends RichViewLayout.ViewHolder<BlockAttributesVM> {

        @NonNull
        private final BlockViewLayout mBlockViewLayout;

        public BlockViewHolder(@NonNull BlockViewLayout view) {
            super(view);
            mBlockViewLayout = view;
        }

        @Override
        public void bind(@NonNull BlockAttributesVM attributesVM) {
            mBlockViewLayout.setContent(attributesVM.mIcon, attributesVM.mContent);
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            mBlockViewLayout.recycle();
        }
    }
}
