package ru.tensor.sbis.richtext.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.converter.cfg.Configuration;
import ru.tensor.sbis.richtext.converter.cfg.DecoratedLinkConfiguration;
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter;
import ru.tensor.sbis.richtext.converter.css.style.CssStyleSpanConverter;
import ru.tensor.sbis.richtext.converter.handler.BlockQuoteSenderTagHandler;
import ru.tensor.sbis.richtext.converter.handler.BlockQuoteTagHandler;
import ru.tensor.sbis.richtext.converter.handler.BoldTagHandler;
import ru.tensor.sbis.richtext.converter.handler.BrTagHandler;
import ru.tensor.sbis.richtext.converter.handler.DecoratedLinkTagHandler;
import ru.tensor.sbis.richtext.converter.handler.DivTagHandler;
import ru.tensor.sbis.richtext.converter.handler.EnumerationTagHandler;
import ru.tensor.sbis.richtext.converter.handler.HTagHandler;
import ru.tensor.sbis.richtext.converter.handler.IframeTagHandler;
import ru.tensor.sbis.richtext.converter.handler.InlineCodeTagHandler;
import ru.tensor.sbis.richtext.converter.handler.ItalicTagHandler;
import ru.tensor.sbis.richtext.converter.handler.ParagraphTagHandler;
import ru.tensor.sbis.richtext.converter.handler.SpanTagHandler;
import ru.tensor.sbis.richtext.converter.handler.StrikeTagHandler;
import ru.tensor.sbis.richtext.converter.handler.UnderlineTagHandler;
import ru.tensor.sbis.richtext.converter.handler.UrlTagHandler;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.ViewStrategyPostprocessor;
import ru.tensor.sbis.richtext.converter.handler.view.BlockCodeTagHandler;
import ru.tensor.sbis.richtext.converter.handler.view.ImageTagHandler;
import ru.tensor.sbis.richtext.converter.handler.base.ComposeTagHandler;
import ru.tensor.sbis.richtext.converter.handler.base.DelegateAttributesTagHandler;
import ru.tensor.sbis.richtext.converter.handler.base.MultiTagWrapper;
import ru.tensor.sbis.richtext.converter.handler.base.TagHandler;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.ParagraphPostprocessor;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;
import ru.tensor.sbis.richtext.converter.handler.view.ViewIframeTagHandler;
import ru.tensor.sbis.richtext.converter.handler.view.TableTagHandler;
import ru.tensor.sbis.richtext.util.HtmlCssClass;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.HtmlTag;
import ru.tensor.sbis.richtext.util.Predicate;

/**
 * Класс-делегат для обработки тегов.
 * Собирает в себе все обработчики и дергает нужные в зависимости от входящих значений (к примеру названия тега)
 *
 * @author am.boldinov
 */
public class TagHandlerDelegate {

    @NonNull
    private final SbisThemedContext mContext;
    @NonNull
    private final Map<String, TagHandler> mHandlerStore = new LinkedHashMap<>();
    @NonNull
    private final Map<String, DelegateAttributesTagHandler> mAttributesHandlerStore = new HashMap<>(1);
    @NonNull
    private final List<SpanPostprocessor> mPostProcessorQueue = new LinkedList<>();
    @NonNull
    private final ParagraphPostprocessor mParagraphPostprocessor = new ParagraphPostprocessor();

    TagHandlerDelegate(@NonNull SbisThemedContext context) {
        mContext = context;
        registerPostprocessor(new ViewStrategyPostprocessor(context));
        registerPostprocessor(mParagraphPostprocessor);
    }

    void registerHandlers(@NonNull Configuration configuration) {
        mHandlerStore.clear();
        mAttributesHandlerStore.clear();
        mParagraphPostprocessor.setParagraphSpacing(configuration.getRenderOptions().getParagraphLineSpacing());
        final CssStyleSpanConverter cssStyleConverter = configuration.getCssConfiguration().provideStyleConverter(mContext);
        final CssClassSpanConverter cssClassConverter = configuration.getCssConfiguration().provideClassConverter(mContext);
        registerHandler(HtmlTag.SPAN, new SpanTagHandler(mContext, cssStyleConverter, cssClassConverter));
        registerCssClassHandler(HtmlTag.SPAN, HtmlCssClass.INLINE_CODE, new InlineCodeTagHandler(mContext));
        registerHandler(HtmlTag.H1, new HTagHandler(R.style.RichTextTitleStyle_H1, cssClassConverter));
        registerHandler(HtmlTag.H2, new HTagHandler(R.style.RichTextTitleStyle_H2, cssClassConverter));
        registerHandler(HtmlTag.H3, new HTagHandler(R.style.RichTextTitleStyle_H3, cssClassConverter));
        registerHandler(HtmlTag.H4, new HTagHandler(R.style.RichTextTitleStyle_H4, cssClassConverter));
        registerHandler(HtmlTag.H5, new HTagHandler(R.style.RichTextTitleStyle_H5, cssClassConverter));
        registerHandler(HtmlTag.H6, new HTagHandler(R.style.RichTextTitleStyle_H6, cssClassConverter));
        final TagHandler divTagHandler = new ComposeTagHandler(new DivTagHandler(), new SpanTagHandler(mContext, cssStyleConverter, cssClassConverter));
        registerHandler(HtmlTag.DIV, divTagHandler);
        registerHandler(HtmlTag.PRE, divTagHandler);
        final BoldTagHandler boldTagHandler = new BoldTagHandler();
        registerHandler(HtmlTag.STRONG, boldTagHandler);
        registerHandler(HtmlTag.B, boldTagHandler);
        registerHandler(HtmlTag.EM, new ItalicTagHandler(mContext));
        registerHandler(HtmlTag.U, new UnderlineTagHandler());
        final TagHandler strikeTagHandler = new StrikeTagHandler();
        registerHandler(HtmlTag.STRIKE, strikeTagHandler);
        registerHandler(HtmlTag.DEL, strikeTagHandler);
        registerHandler(HtmlTag.S, strikeTagHandler);
        final EnumerationTagHandler enumerationTagHandler = new EnumerationTagHandler(mContext, configuration.getNumberSpanConfiguration());
        registerHandler(HtmlTag.UL, enumerationTagHandler);
        registerHandler(HtmlTag.OL, enumerationTagHandler);
        registerHandler(HtmlTag.LI, enumerationTagHandler);
        final TagHandler blockQuoteTagHandler = new BlockQuoteTagHandler(mContext, configuration.getBlockQuoteSpanConfiguration());
        registerHandler(HtmlTag.BLOCKQUOTE, blockQuoteTagHandler);
        registerCssClassHandler(HtmlTag.DIV, HtmlCssClass.BLOCKQUOTE, blockQuoteTagHandler);
        registerHandler(HtmlTag.BLOCKQUOTE_SENDER, new BlockQuoteSenderTagHandler(mContext, configuration.getBlockQuoteSpanConfiguration()));
        final TagHandler linkTagHandler;
        final TagHandler decoratedLinkTagHandler;
        final DecoratedLinkConfiguration linkConfiguration = configuration.getDecoratedLinkConfiguration();
        if (configuration.getRenderOptions().isDrawLinkAsDecorated()) {
            linkTagHandler = new DecoratedLinkTagHandler(mContext, linkConfiguration);
            decoratedLinkTagHandler = linkTagHandler;
        } else {
            linkTagHandler = new UrlTagHandler(false, linkConfiguration);
            decoratedLinkTagHandler = new UrlTagHandler(true, linkConfiguration);
        }
        registerHandler(HtmlTag.A, linkTagHandler);
        registerHandler(HtmlTag.DECORATED_LINK, decoratedLinkTagHandler);
        final TagHandler linkIframeTagHandler = new IframeTagHandler(linkTagHandler);
        if (configuration.getRenderOptions().isDrawWrappedImages()) {
            registerHandler(HtmlTag.IMG, new ImageTagHandler(mContext));
            final TagHandler tableTagHandler = new TableTagHandler(mContext, configuration.getTableConfiguration());
            registerHandler(HtmlTag.TABLE, tableTagHandler);
            registerHandler(HtmlTag.TABLE_ROW, tableTagHandler);
            registerHandler(HtmlTag.TABLE_CELL, tableTagHandler);
            registerHandler(HtmlTag.TABLE_HEADER, tableTagHandler);
            registerCssClassPredicateHandler(HtmlTag.PRE, attr -> attr.startsWith(HtmlCssClass.BLOCK_CODE_PREFIX),
                    new BlockCodeTagHandler(mContext));
            registerHandler(HtmlTag.IFRAME, new ViewIframeTagHandler(mContext, linkIframeTagHandler));
        } else {
            registerHandler(HtmlTag.IFRAME, linkIframeTagHandler);
        }
        registerHandler(HtmlTag.P, new ParagraphTagHandler(configuration.getBrConfiguration(), configuration.getRenderOptions()));
        registerHandler(HtmlTag.BR, new BrTagHandler(configuration.getBrConfiguration(), configuration.getRenderOptions()));
    }

    void registerHandler(@NonNull String tag, @Nullable TagHandler tagHandler) {
        mHandlerStore.put(tag, tagHandler);
    }

    void registerCssClassHandler(@NonNull String tag, @NonNull String className,
                                 @Nullable TagHandler tagHandler) {
        tryGetAttributesHandler(tag, "class").putValueHandler(className, tagHandler);
    }

    void registerCssClassPredicateHandler(@NonNull String tag, @NonNull Predicate<String> classNamePredicate,
                                          @NonNull TagHandler tagHandler) {
        tryGetAttributesHandler(tag, "class").addValueHandler(classNamePredicate, tagHandler);
    }

    void registerPostprocessor(@NonNull SpanPostprocessor postprocessor) {
        mPostProcessorQueue.add(postprocessor);
    }

    /**
     * Освобождает ресурсы всех обработчиков для повторного переиспользования
     */
    public void recycleHandlers() {
        for (TagHandler tagHandler : mHandlerStore.values()) {
            tagHandler.recycle();
        }
        for (TagHandler tagHandler : mAttributesHandlerStore.values()) {
            tagHandler.recycle();
        }
    }

    /**
     * Обрабатывает открытие тега
     */
    public void handleStartTag(@NonNull String tag, @NonNull Editable stream, @NonNull TagAttributes attributes) {
        final TagHandler tagHandler = getHandler(tag);
        if (tagHandler != null) {
            if (tagHandler instanceof MultiTagWrapper) {
                ((MultiTagWrapper) tagHandler).setCurrentTag(tag);
            }
            tagHandler.onStartTag(stream, attributes);
        }
        final TagHandler attributesTagHandler = getAttributesHandler(tag);
        if (attributesTagHandler != null) {
            attributesTagHandler.onStartTag(stream, attributes);
        }
    }

    /**
     * Обрабатывает закрытие тега
     */
    public void handleEndTag(@NonNull String tag, @NonNull Editable stream) {
        final TagHandler tagHandler = getHandler(tag);
        if (tagHandler != null) {
            if (tagHandler instanceof MultiTagWrapper) {
                ((MultiTagWrapper) tagHandler).setCurrentTag(tag);
            }
            tagHandler.onEndTag(stream);
        }
        final TagHandler attributesTagHandler = getAttributesHandler(tag);
        if (attributesTagHandler != null) {
            attributesTagHandler.onEndTag(stream);
        }
    }

    /**
     * Обрабатывает текст
     */
    public void handleText(@NonNull String text, @NonNull Editable stream,
                           @Nullable TagAttributes attributes) {
        if (text.length() == 1) {
            final char ch = text.charAt(0);
            // &nbsp; может прийти как отдельная строка
            if (ch == HtmlHelper.NBSP) {
                if (getHandler(HtmlTag.IMG) != null) {
                    text = StringUtils.SPACE;
                } else {
                    text = StringUtils.EMPTY;
                }
            } else if (ch == '\n') { // \n игнорируется согласно рендеру веба
                // для текста внутри блочного тега "pre" разрешаем вставку переноса,
                // например блок "вставка кода"
                if (attributes == null || !attributes.getTag().equals(HtmlTag.PRE)) {
                    text = StringUtils.EMPTY;
                }
            }
        }
        if (!text.isEmpty()) {
            stream.append(text);
        }
    }

    /**
     * Последовательно вызывает постобработчики текста
     */
    public void executePostprocessor(@NonNull Editable stream) {
        final Set<TagHandler> uniqueHandlers = new LinkedHashSet<>(mHandlerStore.values());
        for (TagHandler tagHandler : uniqueHandlers) {
            if (tagHandler instanceof SpanPostprocessor && !mPostProcessorQueue.contains(tagHandler)) {
                ((SpanPostprocessor) tagHandler).process(stream);
            }
        }
        for (SpanPostprocessor postprocessor : mPostProcessorQueue) {
            postprocessor.process(stream);
        }
    }

    @SuppressWarnings("SameParameterValue")
    @NonNull
    private DelegateAttributesTagHandler tryGetAttributesHandler(@NonNull String tag, @NonNull String attributeName) {
        DelegateAttributesTagHandler handler = getAttributesHandler(tag);
        if (handler == null) {
            handler = new DelegateAttributesTagHandler(attributeName);
            mAttributesHandlerStore.put(tag, handler);
        }
        return handler;
    }

    @Nullable
    private DelegateAttributesTagHandler getAttributesHandler(@NonNull String tag) {
        return mAttributesHandlerStore.get(tag);
    }

    @Nullable
    private TagHandler getHandler(@NonNull String tag) {
        return mHandlerStore.get(tag);
    }
}
