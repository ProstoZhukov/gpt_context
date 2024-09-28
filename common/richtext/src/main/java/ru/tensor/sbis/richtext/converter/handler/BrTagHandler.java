package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;

import android.text.Editable;

import org.apache.commons.lang3.StringUtils;

import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.cfg.BrConfiguration;
import ru.tensor.sbis.richtext.converter.cfg.RenderOptions;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleTagHandler;
import ru.tensor.sbis.richtext.util.HtmlHelper;

/**
 * Обработчик тегов переноса строк: br
 *
 * @author am.boldinov
 */
public class BrTagHandler extends SimpleTagHandler {

    @NonNull
    private final BrConfiguration mConfiguration;
    @NonNull
    private final RenderOptions mRenderOptions;

    public BrTagHandler(@NonNull BrConfiguration brConfiguration, @NonNull RenderOptions renderOptions) {
        mConfiguration = brConfiguration;
        mRenderOptions = renderOptions;
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {

    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        if (mRenderOptions.isDrawWrappedImages()) {
            stream.append(StringUtils.LF);
        } else {
            HtmlHelper.appendLineBreakIgnoreSpace(stream, mConfiguration.getMaxLineBreakCount());
        }
    }

}
