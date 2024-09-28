package ru.tensor.sbis.richtext.converter.json;

import androidx.annotation.NonNull;

import android.text.Spannable;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

import ru.tensor.sbis.jsonconverter.generated.RichTextHandler;
import ru.tensor.sbis.richtext.converter.TagHandlerDelegate;
import ru.tensor.sbis.richtext.converter.TagStreamProcessor;
import ru.tensor.sbis.richtext.util.SpannableStreamBuilder;

/**
 * Потоковый обработчик json markup-model
 *
 * @author am.boldinov
 */
public final class JsonTagStreamProcessor extends RichTextHandler implements TagStreamProcessor {

    @NonNull
    private final TagHandlerDelegate mHandlerDelegate;
    @NonNull
    private final SpannableStreamBuilder mSpannableStream = new SpannableStreamBuilder(new SpannableStreamBuilder.BuildListener() {
        @Override
        public void onBuild(@NonNull SpannableStreamBuilder source) {
            mHandlerDelegate.executePostprocessor(source);
        }
    });
    @NonNull
    private final Deque<JsonTagAttributes> mAttributesQueue = new LinkedList<>();

    private interface Type {
        String TAG = "tag";
        String TEXT = "text";
    }

    public JsonTagStreamProcessor(@NonNull TagHandlerDelegate handlerDelegate) {
        mHandlerDelegate = handlerDelegate;
    }

    @Override
    @NonNull
    public Spannable buildResult() {
        return mSpannableStream.build();
    }

    @Override
    public void onDocumentStart() {
        // ignore
    }

    @Override
    public void onDocumentEnd() {
        mHandlerDelegate.recycleHandlers();
        mAttributesQueue.clear();
    }

    @Override
    public boolean onElementBegin(@NonNull String s, @NonNull String s1, @NonNull HashMap<String, String> hashMap) {
        if (s.equalsIgnoreCase(Type.TAG)) {
            final JsonTagAttributes attributes = JsonTagAttributes.wrap(s1, hashMap);
            attributes.setParent(mAttributesQueue.peekLast());
            mAttributesQueue.add(attributes);
            mHandlerDelegate.handleStartTag(s1, mSpannableStream, attributes);
        } else if (s.equalsIgnoreCase(Type.TEXT)) {
            mHandlerDelegate.handleText(s1, mSpannableStream, mAttributesQueue.peekLast());
        }
        return true;
    }

    @Override
    public boolean onElementEnd(@NonNull String s, @NonNull String s1) {
        if (s.equalsIgnoreCase(Type.TAG)) {
            mHandlerDelegate.handleEndTag(s1, mSpannableStream);
            final JsonTagAttributes attributes = mAttributesQueue.pollLast();
            if (attributes != null) {
                attributes.setParent(null);
            }
        }
        return true;
    }

    @Override
    public boolean onAttribute(@NonNull String s, @NonNull String s1) {
        return false;
    }
}
