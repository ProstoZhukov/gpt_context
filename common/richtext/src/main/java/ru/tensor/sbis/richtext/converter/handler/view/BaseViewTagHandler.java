package ru.tensor.sbis.richtext.converter.handler.view;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import java.util.Deque;
import java.util.LinkedList;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.attributes.PairTagAttributes;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.util.SpannableUtil;
import ru.tensor.sbis.richtext.view.ViewTemplate;
import ru.tensor.sbis.richtext.view.strategy.bounds.EndSpanBoundsTransformer;
import ru.tensor.sbis.richtext.view.strategy.bounds.StartSpanBoundsTransformer;
import timber.log.Timber;

/**
 * Базовый обработчик тегов для встраивания кастомных View в текст.
 * Вью модель для биндинга View создается в методе onStartTag.
 * Если View была обернута в ссылку (тег [a]), то эта ссылка будет открываться автоматически при
 * клике на View.
 *
 * @author am.boldinov
 */
public abstract class BaseViewTagHandler extends MarkedTagHandler {

    @NonNull
    protected final Context mContext;
    @NonNull
    private final Deque<ViewStubSpan> mSpanStack = new LinkedList<>();

    public BaseViewTagHandler(@NonNull Context context) {
        mContext = context;
    }

    @CallSuper
    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        final BaseAttributesVM vm;
        try {
            final String className = attributes.getValue("class");
            if (className == null) { // class может приходить у родительского тега
                if (attributes.getParent() != null) {
                    attributes = new PairTagAttributes(attributes, attributes.getParent());
                }
            }
            vm = createAttributesVM(attributes);
            if (vm == null) {
                return;
            }
        } catch (Exception e) {
            Timber.e(e);
            return;
        }
        final MarkSpan.Url parentUrlSpan = SpannableUtil.getLastOpened(stream, MarkSpan.Url.class);
        // если родительским элементом является ссылка, то она должна открываться при клике на вью
        if (parentUrlSpan != null && !parentUrlSpan.getHref().isEmpty()) {
            parentUrlSpan.markAsHandled();
            vm.setClickableSpan(parentUrlSpan.getRealSpan());
        }
        markParagraphAsHandled(stream, vm);
        if (validateSpanCandidate(stream, vm)) {
            final ViewStubSpan viewSpan = new ViewStubSpan(vm);
            applyParentLeadingMargin(stream, viewSpan);
            applyParentFontSize(stream, viewSpan);
            mark(stream, viewSpan);
            mSpanStack.add(viewSpan);
        }
    }

    @CallSuper
    @Override
    public void onEndTag(@NonNull Editable stream) {
        mSpanStack.pollLast();
    }

    @CallSuper
    @Override
    public void recycle() {
        mSpanStack.clear();
    }

    /**
     * Вызывается перед установкой вью модели в текст.
     * В данном методе можно проверить вью модель на валидность, объединить вью модель
     * с другими вью моделями в тексте, тем самым, например, образовав скроллящийся список.
     * <p>
     * Во всех случаях, когда вью модель не должна быть установлена в текст, необходимо вернуть false.
     */
    protected boolean validateSpanCandidate(@NonNull Editable stream, @NonNull BaseAttributesVM vm) {
        return true;
    }

    /**
     * Создает новый экземпляр вью модели для биндинга во View
     *
     * @param attributes атрибуты тега, на который был установлен текущий обработчик
     */
    @Nullable
    protected abstract BaseAttributesVM createAttributesVM(@NonNull TagAttributes attributes);

    /**
     * Возвращает текущую вью модель, которая была создана в потоке обработки тегов.
     * При выполнении всех условий вью модель создается на onStartTag и очищается в onEndTag,
     * это необходимо учитывать при наследовании и переопределении этих методов.
     */
    @Nullable
    protected final BaseAttributesVM getCurrentVM() {
        final ViewStubSpan span = mSpanStack.peekLast();
        if (span != null) {
            return span.getAttributes();
        }
        return null;
    }

    private static void markParagraphAsHandled(@NonNull Editable stream, @NonNull BaseAttributesVM vm) {
        if (vm.getTemplate() != ViewTemplate.INLINE && vm.getTemplate() != ViewTemplate.INLINE_SIZE) {
            // все блочные компоненты занимают место по ширине на весь экран, перенос не требуется
            final MarkSpan.Paragraph parentParagraphSpan = SpannableUtil.getLastOpened(stream, MarkSpan.Paragraph.class);
            if (parentParagraphSpan != null && stream.getSpanEnd(parentParagraphSpan) == stream.length()) {
                parentParagraphSpan.markAsHandled();
            }
        }
    }

    private static void applyParentLeadingMargin(@NonNull Editable stream, @NonNull ViewStubSpan viewSpan) {
        final MarkSpan.LeadingMargin[] leadingSpans = stream.getSpans(0, stream.length(), MarkSpan.LeadingMargin.class);
        if (leadingSpans.length == 0) {
            return;
        }
        int leadingOffset = 0;
        for (int i = leadingSpans.length - 1; i >= 0; i--) {
            final MarkSpan.LeadingMargin span = leadingSpans[i];
            if ((stream.getSpanFlags(span) & Spanned.SPAN_INCLUSIVE_EXCLUSIVE) == Spanned.SPAN_INCLUSIVE_EXCLUSIVE) {
                if (i == leadingSpans.length - 1) { // накладываем трансформацию только на последний родительский span
                    if (stream.getSpanStart(span) == stream.length()) {
                        viewSpan.setSpanBoundsTransformer(new StartSpanBoundsTransformer(LeadingMarginSpan.class));
                    } else {
                        viewSpan.setSpanBoundsTransformer(new EndSpanBoundsTransformer(LeadingMarginSpan.class));
                    }
                }
                final Object realSpan = span.getRealSpan();
                if (realSpan instanceof LeadingMarginSpan) {
                    leadingOffset += ((LeadingMarginSpan) realSpan).getLeadingMargin(true);
                }
            }
        }
        viewSpan.setLeadingOffset(leadingOffset);
    }

    private static void applyParentFontSize(@NonNull Editable stream, @NonNull ViewStubSpan viewSpan) {
        if (viewSpan.getAttributes().getTemplate() == ViewTemplate.INLINE_SIZE) {
            final MarkSpan.FontSize sizeSpan = SpannableUtil.getLastOpened(stream, MarkSpan.FontSize.class);
            if (sizeSpan != null) {
                viewSpan.setMaxHeight(sizeSpan.getSize());
            }
        }
    }
}
