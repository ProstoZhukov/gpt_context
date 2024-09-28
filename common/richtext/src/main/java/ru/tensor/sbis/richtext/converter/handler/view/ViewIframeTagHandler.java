package ru.tensor.sbis.richtext.converter.handler.view;

import android.content.Context;

import androidx.annotation.NonNull;

import android.text.Editable;

import java.util.Deque;
import java.util.LinkedList;

import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.handler.base.TagHandler;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.span.view.youtube.YoutubeAttributesVM;
import ru.tensor.sbis.richtext.span.view.youtube.YouTubeUtil;

/**
 * Обработчик тега iframe для рендера превью видео с ютуб
 */
public class ViewIframeTagHandler extends BaseViewTagHandler {

    @NonNull
    private final TagHandler mDefaultHandler;
    @NonNull
    private final Deque<FrameType> mFrameStack = new LinkedList<>();

    public ViewIframeTagHandler(@NonNull Context context, @NonNull TagHandler defaultHandler) {
        super(context);
        mDefaultHandler = defaultHandler;
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        final String src = attributes.getValue("src");
        final FrameType frame;
        if (src != null) {
            if (YouTubeUtil.isYouTubeVideo(src)) {
                frame = FrameType.VIEW;
                super.onStartTag(stream, attributes);
            } else {
                frame = FrameType.DEFAULT;
                mDefaultHandler.onStartTag(stream, attributes);
            }
        } else {
            frame = FrameType.UNKNOWN;
        }
        mFrameStack.add(frame);
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        final FrameType type = mFrameStack.pollLast();
        if (type == FrameType.DEFAULT) {
            mDefaultHandler.onEndTag(stream);
        } else if (type == FrameType.VIEW) {
            super.onEndTag(stream);
        }
    }

    @Override
    public void recycle() {
        mFrameStack.clear();
        mDefaultHandler.recycle();
        super.recycle();
    }

    @Nullable
    @Override
    protected BaseAttributesVM createAttributesVM(@NonNull TagAttributes attributes) {
        final String videoId = YouTubeUtil.pickVideoId(attributes.getValue("src"));
        return new YoutubeAttributesVM(attributes.getTag(), videoId);
    }

    private enum FrameType {
        DEFAULT,
        VIEW,
        UNKNOWN
    }
}
