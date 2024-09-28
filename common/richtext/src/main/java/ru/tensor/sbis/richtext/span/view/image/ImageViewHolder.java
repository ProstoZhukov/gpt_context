package ru.tensor.sbis.richtext.span.view.image;

import android.view.ViewGroup;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Вью-холдер изображения
 *
 * @author am.boldinov
 */
final class ImageViewHolder extends RecyclerView.ViewHolder {

    private static final double IMAGE_RESIZE_FACTOR = 1.5;

    @NonNull
    private final RichImageView mImageView;
    @Nullable
    private ImageAttributesVM mAttributesVM;

    ImageViewHolder(@NonNull RichImageView view) {
        super(view);
        mImageView = view;
    }

    public void bind(@NonNull ImageAttributesVM vm) {
        final ImageAttributesVM oldVm = mAttributesVM;
        mAttributesVM = vm;
        final ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        final int oldWidth = layoutParams.width;
        final int oldHeight = layoutParams.height;
        layoutParams.width = vm.getWidth();
        layoutParams.height = vm.getHeight();
        if (oldWidth != layoutParams.width || oldHeight != layoutParams.height) {
            mImageView.setLayoutParams(layoutParams);
        }
        if (Objects.equals(oldVm, vm)) {
            return;
        }
        final int originPx = vm.getInitialWidth() * vm.getInitialHeight();
        final int viewPx = layoutParams.width * layoutParams.height;
        int resizeWidth = 0;
        int resizeHeight = 0;
        if (originPx > 0 && viewPx > 0) {
            final double resizeFactor = (double) originPx / viewPx;
            if (resizeFactor > IMAGE_RESIZE_FACTOR) { // ускорение рендеринга за счет уменьшения нагрузки на процессор
                resizeWidth = layoutParams.width;
                resizeHeight = layoutParams.height;
            }
        }
        mImageView.setImageUrl(vm.getPreviewUrl(), resizeWidth, resizeHeight);
    }
}
