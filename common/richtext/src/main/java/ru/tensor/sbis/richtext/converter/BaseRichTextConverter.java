package ru.tensor.sbis.richtext.converter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Spannable;
import android.text.SpannableString;

import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext;
import ru.tensor.sbis.richtext.RichTextPlugin;
import ru.tensor.sbis.richtext.converter.cfg.Configuration;
import ru.tensor.sbis.richtext.converter.handler.base.TagHandler;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;
import ru.tensor.sbis.richtext.util.HtmlTag;

/**
 * Базовый класс для упрощения реализаций конвертеров
 * <p>
 *
 * @author am.boldinov
 */
public abstract class BaseRichTextConverter implements RichTextConverter {

    protected static final String[] SAFE_TAGS = new String[]{
            HtmlTag.IFRAME, HtmlTag.BLOCKQUOTE_SENDER, HtmlTag.DECORATED_LINK
    };

    @NonNull
    protected final SbisThemedContext mContext;
    @NonNull
    protected final TagHandlerDelegate mTagHandlerDelegate;

    public BaseRichTextConverter(@NonNull Context context) {
        this(context, null);
    }

    public BaseRichTextConverter(@NonNull Context context, @Nullable Configuration configuration) {
        mContext = RichTextPlugin.themedContext(context);
        mTagHandlerDelegate = new TagHandlerDelegate(mContext);
        setConfiguration(configuration);
    }

    @NonNull
    @Override
    public synchronized Spannable convert(@NonNull String source) {
        if (source.isEmpty()) {
            return new SpannableString(source);
        }
        try {
            getTagStreamProcessor().onDocumentStart();
            parse(source);
            getTagStreamProcessor().onDocumentEnd();
        } catch (Exception e) {
            CommonUtils.handleException(e);
        }
        return getTagStreamProcessor().buildResult();
    }

    @Override
    public void setConfiguration(@Nullable Configuration configuration) {
        if (configuration == null) {
            mTagHandlerDelegate.registerHandlers(Configuration.getDefault());
        } else {
            mTagHandlerDelegate.registerHandlers(configuration);
        }
    }

    @Override
    public void setCustomTagHandler(@NonNull String tag, @Nullable TagHandler tagHandler) {
        if (tag.equalsIgnoreCase(HtmlTag.DIV)) {
            throw new IllegalArgumentException("Div tag is not supported here, use setCssClassTagHandler method");
        }
        mTagHandlerDelegate.registerHandler(tag, tagHandler);
    }

    @Override
    public void setCssClassTagHandler(@NonNull String tag, @NonNull String className, @Nullable TagHandler tagHandler) {
        mTagHandlerDelegate.registerCssClassHandler(tag, className, tagHandler);
    }

    @Override
    public void addPostprocessor(@NonNull SpanPostprocessor postprocessor) {
        mTagHandlerDelegate.registerPostprocessor(postprocessor);
    }

    protected abstract void parse(@NonNull String source);

    @NonNull
    protected abstract TagStreamProcessor getTagStreamProcessor();
}
