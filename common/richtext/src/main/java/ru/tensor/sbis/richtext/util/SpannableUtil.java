package ru.tensor.sbis.richtext.util;

import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import androidx.core.util.Consumer;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.span.view.CollectionAttributesVM;
import ru.tensor.sbis.richtext.span.view.ContentAttributesVM;
import ru.tensor.sbis.richtext.span.view.image.ImageAttributesVM;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Утилита для работы со Spannable
 *
 * @author am.boldinov
 */
public class SpannableUtil {

    /**
     * Смещение относительно начала абзаца
     */
    public static final int LEADING_MARGIN_OFFSET_X = 2;
    /**
     * Идентификатор для отключения Parcelable у системных Span
     */
    public static final int UNPARCEL_SPAN_TYPE_ID = 0;

    /**
     * Пустая реализация {@link ClickableSpan}
     */
    @NonNull
    public static final ClickableSpan EMPTY_CLICK_SPAN = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {

        }
    };

    /**
     * Возвращает последний с конца span переданного класса
     */
    @Nullable
    public static <T> T getLast(@NonNull Spanned text, @NonNull Class<T> kind) {
        T[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    /**
     * Возвращает последний с конца span переданного класса в случае если он является маркированным (открытым)
     */
    @Nullable
    public static <T> T getLastOpened(@NonNull Spanned text, @NonNull Class<T> kind) {
        final T last = getLast(text, kind);
        if (last != null && (text.getSpanFlags(last) & Spanned.SPAN_INCLUSIVE_EXCLUSIVE) == Spanned.SPAN_INCLUSIVE_EXCLUSIVE) {
            return last;
        }
        return null;
    }

    /**
     * Находит первое вхождение отмеченного span, начиная от позиции вхождения и до конца текста применяет переданные spans
     */
    public static boolean setSpanFromMark(@NonNull Editable text, @NonNull MarkSpan mark, @NonNull Object... spans) {
        final int where = text.getSpanStart(mark);
        text.removeSpan(mark);
        int len = text.length();
        if (where == len) {
            if (mark.isSupportEmptyContent()) {
                text.append(HtmlHelper.EMPTY_CONTENT_SYMBOL);
                len++;
            } else {
                return false;
            }
        }
        for (Object span : spans) {
            if (span instanceof ForegroundColorSpan) { // fix overlay colors
                setForegroundColorSpan(text, (ForegroundColorSpan) span, where, len);
            } else {
                text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return true;
    }

    /**
     * Устанавливает span для отметки цвета текста
     */
    @SuppressWarnings("WeakerAccess")
    public static void setForegroundColorSpan(@NonNull Spannable text, @NonNull ForegroundColorSpan span, int where, int len) {
        int diff = len - where;
        boolean internal = false;
        if (diff > 1) {
            internal = setForegroundColorSpanInternal(text, span, where, len);
        }
        if (!internal) {
            ForegroundColorSpan[] colorSpans = text.getSpans(where, len, span.getClass());
            if (colorSpans.length == 0) {
                text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static boolean setForegroundColorSpanInternal(@NonNull Spannable text, @NonNull ForegroundColorSpan span, int where, int len) {
        boolean success = false;
        ForegroundColorSpan[] colorSpans = text.getSpans(where, len, span.getClass());
        if (colorSpans.length > 0) {
            int minStartPosition = len;
            int maxEndPosition = where;
            for (ForegroundColorSpan colorSpan : colorSpans) {
                final int startPosition = text.getSpanStart(colorSpan);
                final int endPosition = text.getSpanEnd(colorSpan);
                if (startPosition < minStartPosition) {
                    minStartPosition = startPosition;
                } else if (maxEndPosition < startPosition) {
                    ForegroundColorSpan[] targetSpans = text.getSpans(maxEndPosition, startPosition, ForegroundColorSpan.class);
                    if (targetSpans.length == 0) {
                        success = true;
                        text.setSpan(new ForegroundColorSpan(span.getForegroundColor()), maxEndPosition, startPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                if (endPosition > maxEndPosition) {
                    maxEndPosition = endPosition;
                }
            }
            if (where < minStartPosition) {
                success = true;
                text.setSpan(new ForegroundColorSpan(span.getForegroundColor()), where, minStartPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (len > maxEndPosition) {
                success = true;
                text.setSpan(new ForegroundColorSpan(span.getForegroundColor()), maxEndPosition, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return success;
    }

    /**
     * Копирует содержимое метрики шрифта
     */
    public static void copyMetrics(@NonNull Paint.FontMetricsInt from, @NonNull Paint.FontMetricsInt to) {
        to.top = from.top;
        to.ascent = from.ascent;
        to.descent = from.descent;
        to.bottom = from.bottom;
        to.leading = from.leading;
    }

    /**
     * Зануляет высоту текущей строки
     */
    public static void invalidateSpanHeight(@Nullable Paint.FontMetricsInt fontMetrics) {
        if (fontMetrics != null) {
            fontMetrics.descent = 0;
            fontMetrics.ascent = 0;
            fontMetrics.bottom = 0;
            fontMetrics.top = 0;
        }
    }

    /**
     * Устанавливает высоту текущей строки
     */
    public static void setSpanHeight(int height, int padding, @Nullable Paint.FontMetricsInt fontMetrics) {
        if (fontMetrics != null) {
            final int offsetAbove = getOffsetAboveBaseline(height, fontMetrics) - padding;
            final int offsetBelow = height + offsetAbove + padding * 2;
            fontMetrics.ascent = offsetAbove;
            fontMetrics.top = offsetAbove;
            fontMetrics.descent = offsetBelow;
            fontMetrics.bottom = offsetBelow;
        }
    }

    private static int getOffsetAboveBaseline(int height, @NonNull Paint.FontMetricsInt fontMetrics) {
        int textHeight = fontMetrics.descent - fontMetrics.ascent;
        int offset = (textHeight - height) / 2;
        return fontMetrics.ascent + offset;
    }

    /**
     * Определяет является ли строка пустой и не имеющей спанов
     *
     * @param text текст
     * @return true если длина текста 0 и отсутствуют mark спаны, false иначе
     */
    public static boolean isEmptySpannable(@NonNull Spanned text) {
        return text.length() == 0 && !hasNextSpanTransition(text, -1, null);
    }

    /**
     * Проверяет наличие Span в тексте.
     * {@link #hasNextSpanTransition(Spanned, int, int, Class)}
     */
    @SuppressWarnings("rawtypes")
    public static boolean hasNextSpanTransition(@NonNull Spanned text, int start, @Nullable Class type) {
        return hasNextSpanTransition(text, start, text.length() + 1, type);
    }

    /**
     * Проверяет наличие Span в тексте.
     *
     * @param text  текст для проверки
     * @param start позиция начала для проверки
     * @param limit граница для поиска
     * @param type  Span класс
     * @return true если Span присутствует в запрашиваемых границах, false иначе
     */
    @SuppressWarnings("rawtypes")
    public static boolean hasNextSpanTransition(@NonNull Spanned text, int start, int limit, @Nullable Class type) {
        return text.nextSpanTransition(start, limit, type) != limit;
    }

    /**
     * Извлекает данные для рендера кастомной вью из стилизованного текста
     *
     * @param spanned исходный текст
     * @param tag     тег соответствующий вью
     * @param filter  фильтр возвращаемых данных
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T extends BaseAttributesVM> List<T> getAllTagAttributes(@NonNull Spanned spanned, @NonNull String tag,
                                                                           @Nullable Predicate<T> filter) {
        final ViewStubSpan[] spans = spanned.getSpans(0, spanned.length(), ViewStubSpan.class);
        final List<T> attributes = new ArrayList<>();
        for (ViewStubSpan span : spans) {
            final BaseAttributesVM vm = span.getAttributes();
            if (vm.getTag().equals(tag)) {
                if (vm instanceof CollectionAttributesVM) {
                    final CollectionAttributesVM<BaseAttributesVM> collectionVM = (CollectionAttributesVM<BaseAttributesVM>) vm;
                    for (BaseAttributesVM cvm : collectionVM.getAttributesList()) {
                        if (cvm instanceof ContentAttributesVM) {
                            addContentAttributes((ContentAttributesVM) cvm, tag, attributes, filter);
                        } else {
                            if (filter == null || filter.apply((T) cvm)) {
                                attributes.add((T) cvm);
                            }
                        }
                    }
                } else {
                    if (filter == null || filter.apply((T) vm)) {
                        attributes.add((T) vm);
                    }
                }
            } else if (vm instanceof ContentAttributesVM) {
                addContentAttributes((ContentAttributesVM) vm, tag, attributes, filter);
            }
        }
        return attributes;
    }

    /**
     * {@link #getAllTagAttributes(Spanned, String, Predicate)}
     */
    @NonNull
    public static <T extends BaseAttributesVM> List<T> getAllTagAttributes(@NonNull Spanned spanned,
                                                                           @NonNull String tag) {
        return getAllTagAttributes(spanned, tag, null);
    }

    /**
     * Извлекает данные для рендера изображений из стилизованного текста
     */
    @NonNull
    public static List<ImageAttributesVM> getAllImageAttributes(@NonNull Spanned spanned) {
        return getAllTagAttributes(spanned, HtmlTag.IMG, imageAttributesVM ->
                imageAttributesVM.getTemplate() != ViewTemplate.INLINE_SIZE); // исключаются иконки
    }

    /**
     * Пробегает по всем вложенным контейнерам (кастомным View с данными), которые реализуют
     * {@link ContentAttributesVM}.
     * Будет полезно для дополнительного наложения стилей на текст либо для склеивания текста целиком.
     *
     * @param root     исходный текст
     * @param consumer операция над контентом
     */
    public static void forEachContent(@NonNull Spannable root, @NonNull Consumer<Spannable> consumer) {
        consumer.accept(root);
        final ViewStubSpan[] spans = root.getSpans(0, root.length(), ViewStubSpan.class);
        for (ViewStubSpan span : spans) {
            final BaseAttributesVM vm = span.getAttributes();
            if (vm instanceof ContentAttributesVM) {
                final Iterator<Spannable> iterator = ((ContentAttributesVM) vm).contentIterator();
                while (iterator.hasNext()) {
                    forEachContent(iterator.next(), consumer);
                }
            }
        }
    }

    private static <T extends BaseAttributesVM> void addContentAttributes(@NonNull ContentAttributesVM vm, @NonNull String tag,
                                                                          @NonNull List<T> attributes, @Nullable Predicate<T> filter) {
        final Iterator<Spannable> iterator = vm.contentIterator();
        while (iterator.hasNext()) {
            attributes.addAll(getAllTagAttributes(iterator.next(), tag, filter));
        }
    }
}
