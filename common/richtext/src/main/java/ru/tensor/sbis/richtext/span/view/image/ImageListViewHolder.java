package ru.tensor.sbis.richtext.span.view.image;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.base_components.adapter.AbstractListAdapter;
import ru.tensor.sbis.design.list_utils.decoration.LinearSpaceItemDecoration;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * Вью-холдер коллекции несколько идущих подряд изображений, которые рендерятся внутри текста одним списком
 *
 * @author am.boldinov
 */
final class ImageListViewHolder extends RichViewLayout.ViewHolder<ImageListAttributesVM> {

    private static final float LIST_SCALING_FACTOR = 0.25f;

    @Px
    private final int mMinHeight;
    @NonNull
    private final Adapter mAdapter;
    private final int mDecorationSize;

    @NonNull
    private final GravityImageItemDecoration mGravityDecoration = new GravityImageItemDecoration();

    ImageListViewHolder(@NonNull RecyclerView view, @Nullable RecyclerView.RecycledViewPool viewPool) {
        super(view);
        mMinHeight = view.getContext().getResources().getDimensionPixelSize(R.dimen.richtext_image_gallery_min_height);
        mDecorationSize = view.getResources().getDimensionPixelSize(R.dimen.richtext_image_list_decoration_size);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        view.setLayoutManager(layoutManager);
        view.addItemDecoration(mGravityDecoration);
        view.addItemDecoration(new LinearSpaceItemDecoration(mDecorationSize));
        mAdapter = new Adapter(new RichViewLayout.ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(int position, @NonNull View view) {
                getOnItemClickListener().onListItemClick(getAdapterPosition(), position, view);
            }

            @Override
            public void onListItemClick(int position, int serialIndex, @NonNull View view) {

            }
        });
        view.setAdapter(mAdapter);
        if (viewPool != null) {
            view.setRecycledViewPool(viewPool);
        }
    }

    @Override
    public void bind(@NonNull ImageListAttributesVM attributesVM) {
        // биндинг на onPreMeasure
    }

    @Override
    protected void onPreMeasure(@NonNull ImageListAttributesVM attributesVM, int maxWidth, int maxHeight) {
        final List<ImageAttributesVM> attributes = attributesVM.getAttributesList();
        int itemViewHeight = Integer.MAX_VALUE;
        boolean fitToHeight = true;
        for (int i = 0; i < attributes.size(); i++) {
            final ImageAttributesVM vm = attributes.get(i);
            vm.applyStyleRatio(maxWidth, maxHeight);
            itemViewHeight = Math.min(itemViewHeight, vm.getHeight());
            if (fitToHeight) {
                final ImageAttributesVM next = i < attributes.size() - 1 ? attributes.get(i + 1) : null;
                if (next != null && next.getTemplate() != vm.getTemplate()) {
                    // Изображения с разными шаблонами не уменьшаются
                    fitToHeight = false;
                }
            }
        }
        mGravityDecoration.setGravity(fitToHeight ? GravityImageItemDecoration.CENTER : GravityImageItemDecoration.TOP);
        itemViewHeight = Math.max(itemViewHeight, mMinHeight);
        int contentHeight = 0;
        int contentWidth = attributes.size() * mDecorationSize - mDecorationSize; // декорация рисуется между элементами
        for (ImageAttributesVM vm : attributes) {
            // если изображения имеют меньшую высоту, значит они меньше чем minHeight - ничего с ними не делаем
            // в противном случае уменьшаем изображения до минимальной высоты одного из них
            if (fitToHeight && vm.getHeight() > itemViewHeight) {
                vm.applyHeightRatio(itemViewHeight);
            }
            contentHeight = Math.max(contentHeight, vm.getHeight());
            contentWidth += vm.getWidth();
        }
        // вычисляем возможность масштабирования галереи
        final int freeWidth = maxWidth - contentWidth;
        if (freeWidth > 0) {
            final float factor = (float) freeWidth / maxWidth;
            if (factor <= LIST_SCALING_FACTOR) {
                final int additionalWidth = freeWidth / attributes.size();
                if (additionalWidth > 0 && ensureImageListScaling(attributes, additionalWidth, maxHeight)) {
                    for (ImageAttributesVM vm : attributes) {
                        vm.applyWidthRatio(vm.getWidth() + additionalWidth);
                        contentHeight = Math.max(contentHeight, vm.getHeight());
                    }
                }
            }
        }
        view.getLayoutParams().height = contentHeight;
        mAdapter.setContent(attributes);
    }

    private boolean ensureImageListScaling(@NonNull List<ImageAttributesVM> attributes, int additionalWidth, int maxHeight) {
        for (ImageAttributesVM vm : attributes) {
            final int desiredWidth = vm.getWidth() + additionalWidth;
            // если не не приводит к потере качества
            if (desiredWidth <= vm.getInitialWidth() * view.getResources().getDisplayMetrics().density) {
                final float factor = (float) desiredWidth / vm.getWidth();
                // если после увеличения ширины высота не больше чем максимально доступная
                if (Math.round(vm.getHeight() * factor) <= maxHeight) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    private static final class Adapter extends AbstractListAdapter<ImageAttributesVM, ImageViewHolder> {

        @NonNull
        private final RichViewLayout.ViewHolder.ItemClickListener mItemClickLister;

        Adapter(@NonNull RichViewLayout.ViewHolder.ItemClickListener itemClickLister) {
            mItemClickLister = itemClickLister;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final RichImageView view = new RichImageView(parent.getContext());
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            final ImageViewHolder holder = new ImageViewHolder(view);
            holder.itemView.setOnClickListener(v -> mItemClickLister.onItemClick(holder.getBindingAdapterPosition(), v));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.bind(getItem(position));
        }
    }

    private static final class GravityImageItemDecoration extends RecyclerView.ItemDecoration {

        static final int TOP = 0;
        static final int CENTER = 1;

        private int mGravity = CENTER;

        void setGravity(int gravity) {
            mGravity = gravity;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            final int viewHeight = view.getLayoutParams().height;
            final int parentHeight = parent.getLayoutParams().height;
            if (mGravity == CENTER && viewHeight > 0 && parentHeight > 0 && viewHeight < parentHeight) {
                outRect.top = (parentHeight - viewHeight) / 2;
            } else {
                outRect.top = 0;
            }
        }
    }
}
