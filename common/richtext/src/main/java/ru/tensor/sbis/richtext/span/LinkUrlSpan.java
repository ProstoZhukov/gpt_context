package ru.tensor.sbis.richtext.span;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.design.utils.DebounceActionHandler;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener;
import ru.tensor.sbis.richtext.util.RichTextAndroidUtil;
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl;

/**
 * Span для подсветки ссылок и обработки нажатий на них
 *
 * @author am.boldinov
 */
public final class LinkUrlSpan extends URLSpan implements PrioritySpan, LongClickSpan {

    @Nullable
    private DecoratedLinkOpener mLinkOpener;
    @Nullable
    private DebounceActionHandler mActionHandler;

    public LinkUrlSpan(@NonNull String url, @Nullable DecoratedLinkOpener linkOpener) {
        super(url);
        mLinkOpener = linkOpener;
    }

    public LinkUrlSpan(@NonNull Parcel src) {
        super(src);
    }

    @Override
    public void onClick(View widget) {
        if (getURL().isEmpty()) {
            return;
        }
        if (mActionHandler == null) {
            mActionHandler = new DebounceActionHandler();
        }
        if (mActionHandler.enqueue()) {
            if (mLinkOpener != null) {
                final LinkPreviewImpl linkPreview = new LinkPreviewImpl();
                linkPreview.setHref(getURL());
                mLinkOpener.open(widget.getContext(), linkPreview, null);
            } else {
                super.onClick(widget);
            }
        }
    }

    @Override
    public void onLongClick(@NonNull View widget) {
        RichTextAndroidUtil.copyToClipboard(widget.getContext(), getURL());
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(ds.linkColor);
    }

    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }
}
