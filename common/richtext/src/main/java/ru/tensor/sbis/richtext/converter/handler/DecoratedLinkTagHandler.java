package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.CharacterStyle;

import org.apache.commons.lang3.StringUtils;

import ru.tensor.sbis.common.util.UrlUtils;
import kotlin.Pair;
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.cfg.DecoratedLinkConfiguration;
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkData;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkRepository;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkSpan;
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedLinkStyle;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkType;
import ru.tensor.sbis.richtext.span.decoratedlink.InlineDecoratedLinkSpan;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Спецификация декорирования
 * http://axure.tensor.ru/standarts/v7/%D0%B2%D0%B8%D0%B7%D0%B8%D1%82%D0%BA%D0%B8_%D0%B8_%D0%B4%D0%B5%D0%BA%D0%BE%D1%80%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D1%8B%D0%B5_%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B8.html
 * <p>
 * @author am.boldinov
 */
public class DecoratedLinkTagHandler extends MarkedTagHandler implements SpanPostprocessor {

    @NonNull
    private final SbisThemedContext mContext;

    @NonNull
    private final DecoratedLinkRepository mRepository;
    @NonNull
    private final DecoratedLinkOpener mLinkOpener;
    @NonNull
    private final DecoratedLinkStyle mLinkStyle;

    public DecoratedLinkTagHandler(@NonNull SbisThemedContext context, @NonNull DecoratedLinkConfiguration configuration) {
        mContext = context;
        mRepository = configuration.provideRepository(context);
        mLinkOpener = configuration.provideLinkOpener();
        mLinkStyle = new DecoratedLinkStyle(context);
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        final String sourceUrl = StringUtils.trimToEmpty(
                UrlUtils.checkAndFormatUrlWithHost(attributes.getValue("href"))
        );
        final DecoratedLinkType type = DecoratedLinkType.fromValue(
                attributes.getValue("decoration-type")
        );
        final String decorationJson = attributes.getValue("decoration-data");
        mark(stream, new DecoratedLinkMarkSpan(sourceUrl, mLinkOpener, type, decorationJson));
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        final DecoratedLinkMarkSpan markSpan = SpannableUtil.getLast(stream, DecoratedLinkMarkSpan.class);
        if (markSpan != null) {
            final int position = stream.getSpanStart(markSpan);
            stream.removeSpan(markSpan);
            final String href = markSpan.getHref();
            if (!href.isEmpty()) {
                if (position < stream.length()) { // если за промежуток между началом тега и концом был добавлен текст
                    if (isLinkDecorateCandidate(href)) {
                        final CharSequence linkText = stream.subSequence(position, stream.length());
                        if (href.equals(linkText.toString())) { // если текст совпадает со ссылкой, то декорируем
                            markSpan.setState(MarkState.DECORATE_CANDIDATE);
                        }
                    }
                    setSpan(stream, markSpan, position, stream.length());
                } else if (!markSpan.isHandled()) {
                    if (isLinkDecorateCandidate(href)) {
                        markSpan.setState(MarkState.DECORATE_CANDIDATE);
                    }
                    stream.append(href);
                    setSpan(stream, markSpan, position, stream.length());
                }
            }
        }
    }

    @Override
    public void recycle() {
        // ignore
    }

    @Override
    public void process(@NonNull Editable text) {
        final DecoratedLinkMarkSpan[] spans = text.getSpans(0, text.length(), DecoratedLinkMarkSpan.class);
        for (int i = 0; i < spans.length; i++) {
            final DecoratedLinkMarkSpan markSpan = spans[i];
            if (markSpan.getState() == MarkState.DECORATE_CANDIDATE) {
                if (markSpan.getType() != null) {
                    markSpan.setState(MarkState.DECORATE);
                    continue;
                }
                final int start = text.getSpanStart(markSpan);
                final int end = text.getSpanEnd(markSpan);
                if (text.getSpans(start, end, CharacterStyle.class).length != 0) { // если присутствуют стили текста
                    markSpan.setState(MarkState.LINK_URL);
                } else {
                    final Pair<Character, Integer> nextCh = HtmlHelper.nextCharacter(text, end - 1, true, HtmlHelper.IGNORE_SPACE_PREDICATE);
                    if (nextCh == null || nextCh.getFirst() == '\n') { // находится в конце строки
                        markSpan.setState(MarkState.DECORATE);
                    } else {
                        if (i < spans.length - 1) {
                            final DecoratedLinkMarkSpan nextMark = spans[i + 1];
                            if (text.getSpanStart(nextMark) == nextCh.getSecond()) {
                                nextMark.setParent(markSpan);
                                continue;
                            }
                        }
                        markSpan.setState(MarkState.LINK_URL);
                    }
                }
            }
        }
        for (DecoratedLinkMarkSpan markSpan : spans) {
            int start = text.getSpanStart(markSpan);
            int end = text.getSpanEnd(markSpan);
            text.removeSpan(markSpan);
            if (markSpan.getState() == MarkState.DECORATE && markSpan.getType() != DecoratedLinkType.SMALL) { // small инлайнится, перенос не требуется
                // добавляем перенос строки перед ссылкой если он отсутствует
                final Pair<Character, Integer> prevCh = HtmlHelper.nextCharacter(text, start, false, HtmlHelper.IGNORE_SPACE_PREDICATE);
                if (prevCh != null) {
                    if (prevCh.getFirst() != '\n') {
                        text.insert(start, "\n");
                        start++;
                        end++;
                    } else {
                        final int spaceCount = start - prevCh.getSecond() - 1;
                        if (spaceCount > 0) { // удаляем лишние пробелы
                            text.replace(start - spaceCount, start, StringUtils.EMPTY);
                            start -= spaceCount;
                            end -= spaceCount;
                        }
                    }
                }
            }
            setSpan(text, markSpan, start, end);
        }
    }

    private void setSpan(@NonNull Editable text, @NonNull DecoratedLinkMarkSpan markSpan, int start, int end) {
        switch (markSpan.getState()) {
            case DECORATE_CANDIDATE:
                text.setSpan(markSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case DECORATE:
                if (markSpan.getType() == DecoratedLinkType.SMALL) {
                    final String decorationJson = markSpan.getDecorationJson();
                    if (decorationJson == null) {
                        text.setSpan(markSpan.getRealSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        final DecoratedLinkData linkData = mRepository.getInlineLinkData(markSpan.getHref(), decorationJson);
                        final InlineDecoratedLinkSpan span = new InlineDecoratedLinkSpan(mContext, linkData, mLinkStyle.getSmall(), mLinkOpener);
                        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        text.setSpan(span.getClickableUrlSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    final DecoratedLinkData linkData = mRepository.getLinkData(markSpan.getHref());
                    final DecoratedLinkSpan span = new DecoratedLinkSpan(mContext, linkData, mRepository, mLinkStyle.getMedium(), mLinkOpener);
                    text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(span.getClickableUrlSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case LINK_URL:
                text.setSpan(markSpan.getRealSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                throw new UnsupportedOperationException("Unknown span state");
        }
    }

    /**
     * Аналог метода на wasaby
     * https://github.com/saby/wasaby-controls/blob/rc-22.1000/Controls/_decorator/Markup/resources/linkDecorateUtils.ts#L122
     */
    private static boolean isLinkDecorateCandidate(@NonNull String href) {
        return href.toLowerCase().startsWith("http"); // декорируются только http/https ссылки
    }

    private static final class DecoratedLinkMarkSpan extends MarkSpan.Url {

        @NonNull
        private MarkState mState = MarkState.LINK_URL;
        @Nullable
        private DecoratedLinkMarkSpan mParent;
        @Nullable
        private final DecoratedLinkType mType;
        @Nullable
        private final String mDecorationJson;

        public DecoratedLinkMarkSpan(@NonNull String href, @NonNull DecoratedLinkOpener linkOpener,
                                     @Nullable DecoratedLinkType type, @Nullable String decorationJson) {
            super(href, linkOpener);
            mType = type;
            mDecorationJson = decorationJson;
        }

        private void setState(@NonNull MarkState state) {
            mState = state;
            if (mParent != null) {
                mParent.setState(state);
            }
        }

        private void setParent(@Nullable DecoratedLinkMarkSpan parent) {
            mParent = parent;
        }

        @NonNull
        private MarkState getState() {
            return mState;
        }

        @Nullable
        private DecoratedLinkType getType() {
            return mType;
        }

        @Nullable
        private String getDecorationJson() {
            return mDecorationJson;
        }
    }

    private enum MarkState {
        DECORATE_CANDIDATE,
        DECORATE,
        LINK_URL
    }
}
