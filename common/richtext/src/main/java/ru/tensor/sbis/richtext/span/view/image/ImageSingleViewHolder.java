package ru.tensor.sbis.richtext.span.view.image;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.span.view.ViewSizeType;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Вью-холдер одиночного изображения, которое рендерится внутри текста
 *
 * @author am.boldinov
 */
final class ImageSingleViewHolder extends RichViewLayout.ViewHolder<ImageAttributesVM> {

    private static final float MAX_WRAP_VIEW_WIDTH_FACTOR = 0.4f;
    private static final float MAX_VIEW_WIDTH_PERCENT = 25;

    @NonNull
    private final ImageViewHolder mImageViewHolder;

    ImageSingleViewHolder(@NonNull RichImageView view) {
        super(view);
        mImageViewHolder = new ImageViewHolder(view);
        view.setOnClickListener(v -> getOnItemClickListener().onItemClick(getAdapterPosition(), v));
    }

    @Override
    public void bind(@NonNull ImageAttributesVM attributesVM) {
        // биндинг на onPreMeasure
    }

    @Override
    protected void onPreMeasure(@NonNull ImageAttributesVM vm, int maxWidth, int maxHeight) {
        vm.applyStyleRatio(maxWidth, maxHeight);
        if (!vm.isSingleImageInText() && (vm.getTemplate() == ViewTemplate.INLINE || vm.getTemplate() == ViewTemplate.INLINE_SIZE
                || vm.getTemplate() == ViewTemplate.LEFT || vm.getTemplate() == ViewTemplate.RIGHT)) {
            final float widthFactor = (float) vm.getWidth() / maxWidth;
            // если изображение в тексте занимает слишком много места - ограничиваем максимальным коэффициентом
            if (widthFactor > MAX_WRAP_VIEW_WIDTH_FACTOR) {
                vm.applyWidthRatio(Math.round(maxWidth * MAX_WRAP_VIEW_WIDTH_FACTOR));
            }
        } else {
            final ImageStyle style = vm.getStyle();
            // если картинка является одиночной или размер в процентах и он >= 25% то растягиваем изображение
            if (vm.isSingleImageInText() || style != null && style.getWidthType() == ViewSizeType.PERCENT
                    && style.getWidth() >= MAX_VIEW_WIDTH_PERCENT) {
                int desiredWidth = Math.min(maxWidth, vm.getInitialWidth());
                if (desiredWidth < maxWidth) {
                    // увеличиваем изображение для попытки растянуть
                    desiredWidth = Math.min(maxWidth, Math.round(desiredWidth * view.getResources().getDisplayMetrics().density));
                }
                if (desiredWidth != vm.getWidth()) {
                    vm.applyWidthRatio(desiredWidth);
                    if (vm.getHeight() > maxHeight) {
                        vm.applyHeightRatio(maxHeight);
                    }
                }
            }
        }
        mImageViewHolder.bind(vm);
    }
}
