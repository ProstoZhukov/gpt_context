package ru.tensor.sbis.richtext.converter.handler.view;

import android.content.Context;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;

import android.text.Editable;
import android.util.DisplayMetrics;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.span.view.image.ImageAttributesVM;
import ru.tensor.sbis.richtext.span.view.image.ImageListAttributesVM;
import ru.tensor.sbis.richtext.span.view.ViewSizeType;
import ru.tensor.sbis.richtext.span.view.image.ImageStyle;
import ru.tensor.sbis.richtext.util.FileUtil;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.SpannableUtil;
import ru.tensor.sbis.richtext.view.ViewTemplate;
import timber.log.Timber;

/**
 * Обработчик тегов для изображений [img]
 * <p>
 * Содержит внутри себя логику объединения идущих подряд изображений с одинаковым форматированием
 * в одну коллекцию.
 *
 * @author am.boldinov
 */
public final class ImageTagHandler extends BaseViewTagHandler implements SpanPostprocessor {

    private static final String UUID_PATTERN = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

    public ImageTagHandler(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    protected BaseAttributesVM createAttributesVM(@NonNull TagAttributes attributes) {
        final ImageAttributesVM vm = new ImageAttributesVM(
                attributes.getTag(),
                getImageSize(attributes, R.dimen.richtext_image_default_width, "img_width", "data-natural-width"),
                getImageSize(attributes, R.dimen.richtext_image_default_height, "img_height", "data-natural-height"),
                getImageTemplate(attributes)
        );
        vm.setStyle(getImageStyle(attributes));
        // если изображение имеет нулевой размер - не выводим его
        if (vm.getWidth() <= 0 || vm.getHeight() <= 0 || (vm.getStyle() != null && vm.getStyle().getWidth() <= 0)) {
            return null;
        }
        String uuid = getImageUuid(attributes, "alt");
        if (uuid == null) {
            uuid = getImageUuid(attributes, "data-img-uuid");
        }
        vm.setUuid(uuid);
        vm.setPreviewUrl(getPreviewUrl(attributes));
        if (vm.getTemplate() == ViewTemplate.INLINE_SIZE) {
            vm.setClickableSpan(SpannableUtil.EMPTY_CLICK_SPAN); // клик по иконкам недоступен
        }
        return vm;
    }

    @Override
    protected boolean validateSpanCandidate(@NonNull Editable stream, @NonNull BaseAttributesVM vm) {
        if (vm instanceof ImageAttributesVM) {
            return !isCollectionViewAttributes(stream, (ImageAttributesVM) vm);
        }
        return super.validateSpanCandidate(stream, vm);
    }

    @Nullable
    private ImageStyle getImageStyle(@NonNull TagAttributes attributes) {
        final String widthStyle = HtmlHelper.parseCssStyleValue(attributes.getValue("style"), "width");
        if (widthStyle != null) {
            final ViewSizeType sizeType = ViewSizeType.detect(widthStyle);
            if (sizeType != null) {
                try {
                    int width = Integer.parseInt(widthStyle.replace(sizeType.getType(), ""));
                    if (sizeType == ViewSizeType.PIXEL) {
                        width = Math.round(width * getDisplayMetrics().density);
                    }
                    return new ImageStyle(width, sizeType);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        return null;
    }

    @Nullable
    private String getImageUuid(@NonNull TagAttributes attributes, @NonNull String key) {
        final String id = attributes.getValue(key);
        if (id != null) {
            final String[] ids = id.split("_");
            if (ids.length > 0) {
                return validateUuid(ids[0]);
            }
        }
        return null;
    }

    private int getImageSize(@NonNull TagAttributes attributes, @DimenRes int defValueRes, @NonNull String... keys) {
        for (String key : keys) {
            final String rawSize = attributes.getValue(key);
            if (rawSize != null) {
                try {
                    return Integer.parseInt(rawSize);
                } catch (NumberFormatException e) {
                    Timber.d(e);
                }
            }
        }
        return mContext.getResources().getDimensionPixelSize(defValueRes);
    }

    @Nullable
    private ViewTemplate getImageTemplate(@NonNull TagAttributes attributes) {
        final String className = attributes.getValue("class");
        if (className != null) {
            if (className.contains("left")) {
                return ViewTemplate.LEFT;
            } else if (className.contains("right")) {
                return ViewTemplate.RIGHT;
            } else if (className.contains("center")) {
                return ViewTemplate.CENTER;
            } else if (className.contains("asText") || className.contains("inline")) {
                return ViewTemplate.INLINE;
            } else if (className.contains("image-icon")) {
                return ViewTemplate.INLINE_SIZE;
            }
        }
        return null;
    }

    @Nullable
    private String getPreviewUrl(@NonNull TagAttributes attributes) {
        final String url = attributes.getValue("src");
        if (url != null) {
            if (url.startsWith(FileUtil.getRootCachePath(mContext))) { // файл из локального кеша
                return FileUtil.buildFileSchemePath(url);
            }
            if (FileUtil.isBase64Image(url)) {
                return url;
            }
            return HtmlHelper.formatImageUrl(mContext, url);
        }
        return null;
    }

    @NonNull
    private DisplayMetrics getDisplayMetrics() {
        return mContext.getResources().getDisplayMetrics();
    }

    @Nullable
    private static String validateUuid(@Nullable String uuid) {
        if (uuid != null && uuid.matches(UUID_PATTERN)) {
            return uuid;
        }
        return null;
    }

    private boolean isCollectionViewAttributes(@NonNull Editable stream, @NonNull ImageAttributesVM vm) {
        if (!vm.isCollectionCandidate()) {
            return false;
        }
        final ViewStubSpan previousSpan = findPreviousImageSpan(stream);
        if (previousSpan != null) {
            final BaseAttributesVM prevVm = previousSpan.getAttributes();
            if (prevVm instanceof ImageAttributesVM) {
                final ImageAttributesVM singlePrevVm = (ImageAttributesVM) prevVm;
                // если предыдущий элемент является одиночным и поддерживает добавление в коллекцию
                if (singlePrevVm.isCollectionCandidate() && singlePrevVm.isMatchToCollection(vm)) {
                    final ImageListAttributesVM collectionVM = new ImageListAttributesVM(vm.getTag());
                    collectionVM.addAttributes(singlePrevVm);
                    collectionVM.addAttributes(vm);
                    previousSpan.setAttributes(collectionVM);
                    return true;
                }
            } else if (prevVm instanceof ImageListAttributesVM) {
                final ImageListAttributesVM prevListVm = (ImageListAttributesVM) prevVm;
                if (prevListVm.getAttributesAt(prevListVm.getSize() - 1).isMatchToCollection(vm)) {
                    prevListVm.addAttributes(vm);
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private static ViewStubSpan findPreviousImageSpan(@NonNull Editable stream) {
        final ViewStubSpan last = SpannableUtil.getLast(stream, ViewStubSpan.class);
        // только если спаны идут подряд друг за другом
        if (last != null && (last.getAttributes() instanceof ImageAttributesVM || last.getAttributes() instanceof ImageListAttributesVM)) {
            final int end = stream.getSpanEnd(last);
            if (HtmlHelper.nextCharacter(stream, end - 1, true, HtmlHelper.IGNORE_SPACE_PREDICATE) == null) {
                if (end < stream.length()) {
                    stream.replace(end, stream.length(), StringUtils.EMPTY); // удаляем лишние пробелы
                }
                return last;
            }
        }
        return null;
    }

    @Override
    public void process(@NonNull Editable text) {
        final ViewStubSpan[] spans = text.getSpans(0, text.length(), ViewStubSpan.class);
        if (spans.length == 1) {
            final BaseAttributesVM vm = spans[0].getAttributes();
            if (vm instanceof ImageAttributesVM && text.toString().trim().isEmpty()) {
                ((ImageAttributesVM) vm).setSingleImageInText(true);
            }
        }
    }
}
