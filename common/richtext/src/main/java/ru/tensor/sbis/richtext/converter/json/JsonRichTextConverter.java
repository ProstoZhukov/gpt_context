package ru.tensor.sbis.richtext.converter.json;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.jsonconverter.generated.RichTextParser;
import ru.tensor.sbis.richtext.converter.BaseRichTextConverter;
import ru.tensor.sbis.richtext.converter.TagStreamProcessor;
import ru.tensor.sbis.richtext.converter.cfg.Configuration;
import ru.tensor.sbis.richtext.converter.handler.resolver.SourceTextResolver;
import ru.tensor.sbis.richtext.util.HtmlTag;

/**
 * Конвертер json markup-model в spannable строку для рендера в {@link android.widget.TextView}.
 * https://wi.sbis.ru/doc/platform/developmentapl/interface-development/ws4/markup-model/
 *
 * @author am.boldinov
 */
public class JsonRichTextConverter extends BaseRichTextConverter {

    @NonNull
    private final JsonTagStreamProcessor mTagProcessor;
    @NonNull
    private final SourceTextResolver mTextResolver = new JsonEmptyTagResolver(HtmlTag.SPAN);

    public JsonRichTextConverter(@NonNull Context context) {
        this(context, null);
    }

    public JsonRichTextConverter(@NonNull Context context, @Nullable Configuration configuration) {
        super(context, configuration);
        mTagProcessor = new JsonTagStreamProcessor(mTagHandlerDelegate);
    }

    @Override
    protected void parse(@NonNull String source) {
        final RichTextParser parser = RichTextParser.create(mTagProcessor, true);
        for (String tag : SAFE_TAGS) {
            parser.addSafeTag(tag);
        }
        parser.parse(mTextResolver.resolve(source));
    }

    @NonNull
    @Override
    protected TagStreamProcessor getTagStreamProcessor() {
        return mTagProcessor;
    }
}
