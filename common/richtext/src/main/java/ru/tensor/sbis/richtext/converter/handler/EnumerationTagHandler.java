package ru.tensor.sbis.richtext.converter.handler;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;

import java.util.Deque;
import java.util.LinkedList;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.cfg.NumberSpanConfiguration;
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler;
import ru.tensor.sbis.richtext.converter.handler.base.MultiTagWrapper;
import ru.tensor.sbis.richtext.span.BulletSpanStyle;
import ru.tensor.sbis.richtext.span.NumberSpanStyle;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.HtmlTag;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Обработчик тегов перечисления: ul, ol, li
 *
 * @author am.boldinov
 */
public class EnumerationTagHandler extends MarkedTagHandler implements MultiTagWrapper {

    @NonNull
    private final Context mContext;
    @Nullable
    private final NumberSpanConfiguration mNumberSpanConfiguration;
    @NonNull
    private final Deque<String> mParentStack = new LinkedList<>();
    @NonNull
    private final Deque<Integer> mOlCounterStack = new LinkedList<>();

    @Nullable
    private String mCurrentTag;

    public EnumerationTagHandler(@NonNull Context context, @Nullable NumberSpanConfiguration numberSpanConfiguration) {
        mContext = context;
        mNumberSpanConfiguration = numberSpanConfiguration;
    }

    public EnumerationTagHandler(@NonNull Context context) {
        this(context, null);
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        if (isTag(HtmlTag.UL)) {
            startUl();
        } else if (isTag(HtmlTag.OL)) {
            startOl();
        } else if (isTag(HtmlTag.LI)) {
            handleLi(stream, true, attributes);
        }
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        if (isTag(HtmlTag.LI)) {
            handleLi(stream, false, null);
        } else {
            if (isTag(HtmlTag.UL)) {
                finishUl();
            } else if (isTag(HtmlTag.OL)) {
                finishOl();
            }
            HtmlHelper.appendLineBreak(stream, 1);
        }
    }

    @Override
    public void recycle() {
        mParentStack.clear();
        mOlCounterStack.clear();
    }

    @Override
    public void setCurrentTag(@NonNull String tag) {
        mCurrentTag = tag;
    }

    private void startUl() {
        mParentStack.add(HtmlTag.UL);
    }

    private void startOl() {
        mParentStack.add(HtmlTag.OL);
        mOlCounterStack.add(0);
    }

    private void finishOl() {
        mParentStack.pollLast();
        mOlCounterStack.pollLast();
    }

    private void finishUl() {
        mParentStack.pollLast();
    }

    private void handleLi(@NonNull Editable stream, boolean opening, @Nullable TagAttributes attributes) {
        if (opening) {
            appendLineBreakForced(stream);
        }
        final String parentTag = mParentStack.peekLast();
        if (parentTag != null) {
            if (parentTag.equalsIgnoreCase(HtmlTag.UL)) {
                if (opening) {
                    mark(stream, new MarkSpan.Bullet(parseBulletStyle(attributes)));
                } else {
                    span(stream, MarkSpan.Bullet.class);
                }
            } else if (parentTag.equalsIgnoreCase(HtmlTag.OL)) {
                if (opening) {
                    Integer counter = mOlCounterStack.pollLast();
                    if (counter != null) {
                        final NumberSpanStyle numberStyle = parseNumberStyle(attributes);
                        if (numberStyle != NumberSpanStyle.NONE) {
                            counter++;
                        }
                        mOlCounterStack.add(counter);
                        mark(stream, new MarkSpan.Number(mContext, counter, mNumberSpanConfiguration, numberStyle));
                    }
                } else {
                    span(stream, MarkSpan.Number.class);
                }
            }
        }
    }

    private static void appendLineBreakForced(@NonNull Editable stream) {
        // если присутствует View в конце - необходим явный перенос без проверок
        final int offset = SpannableUtil.hasNextSpanTransition(stream, stream.length() - 1, ViewStubSpan.class) ? 0 : 1;
        HtmlHelper.appendLineBreak(stream, offset);
    }

    @NonNull
    private static BulletSpanStyle parseBulletStyle(@Nullable TagAttributes attributes) {
        if (attributes != null) {
            final String bulletStyle = HtmlHelper.parseCssStyleValue(attributes.getValue("style"), "list-style-type");
            if (bulletStyle != null && bulletStyle.equals("none")) {
                return BulletSpanStyle.NONE;
            }
        }
        return BulletSpanStyle.FILL_CIRCLE;
    }

    @NonNull
    private static NumberSpanStyle parseNumberStyle(@Nullable TagAttributes attributes) {
        if (attributes != null) {
            final String bulletStyle = HtmlHelper.parseCssStyleValue(attributes.getValue("style"), "list-style-type");
            if (bulletStyle != null && bulletStyle.equals("none")) {
                return NumberSpanStyle.NONE;
            }
        }
        return NumberSpanStyle.DEFAULT;
    }

    private boolean isTag(@NonNull String tag) {
        if (mCurrentTag == null) {
            throw new IllegalStateException("You must set current tag before processing");
        }
        return mCurrentTag.equalsIgnoreCase(tag);
    }
}
