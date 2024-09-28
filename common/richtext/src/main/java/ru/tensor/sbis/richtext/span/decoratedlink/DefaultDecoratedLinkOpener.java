package ru.tensor.sbis.richtext.span.decoratedlink;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewData;
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.contract.DecoratedLinkOpenDependency;
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType;
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype;

/**
 * Используемая по умолчанию реализации интерфейса для открытия декорированных ссылок
 *
 * @author am.boldinov
 */
public class DefaultDecoratedLinkOpener implements DecoratedLinkOpener {

    /**
     * Зависимость для открытия внешних сущностей и модулей приложения
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    protected final DecoratedLinkOpenDependency mLinkOpenDependency;

    /**
     * Создает реализацию по умолчанию интерфейса {@link DecoratedLinkOpener}
     *
     * @param linkOpenDependency зависимость для открытия внешних сущностей и модулей приложения
     */
    public DefaultDecoratedLinkOpener(@Nullable DecoratedLinkOpenDependency linkOpenDependency) {
        mLinkOpenDependency = linkOpenDependency;
    }

    @Override
    public void open(@NonNull Context context, @NonNull LinkPreview linkPreview, @Nullable String title) {
        if (linkPreview.getHref().isEmpty()) {
            return;
        }
        switch (linkPreview.getDocType()) {
            case TRADES:
            case GROUP_DISCUSSION_TOPIC:
            case GROUP_DISCUSSION_QUESTION:
            case GROUP_SUGGESTIONS:
                if (mLinkOpenDependency != null) {
                    mLinkOpenDependency.showDocumentLink(context, title, linkPreview.getHref());
                }
                break;

            default:
                openLink(context, new LinkPreviewData(linkPreview));
                break;
        }
    }

    /**
     * Открывает ссылку
     *
     * @param context     контекст
     * @param linkPreviewData модель ссылки
     */
    @SuppressWarnings("WeakerAccess")
    protected void openLink(
        @NonNull Context context,
        @NonNull LinkPreviewData linkPreviewData
    ) {
        final LinkPreview linkPreview = linkPreviewData.getModel();
        if (mLinkOpenDependency != null) {
            if (linkPreview.getDocType() == DocType.UNKNOWN &&
                    linkPreview.getDocSubtype() == LinkDocSubtype.UNKNOWN &&
                    linkPreview.getUrlType() == UrlType.DEFAULT.getValue()) {
                if (!mLinkOpenDependency.getOpenLinkController().processAndForget(linkPreview.getHref())) {
                    openLinkInBrowser(context, linkPreview.getHref());
                }
            } else {
                mLinkOpenDependency.getOpenLinkController().processAndForget(linkPreviewData);
            }
        } else {
            openLinkInBrowser(context, linkPreview.getHref());
        }
    }

    private void openLinkInBrowser(@NonNull Context context, @NonNull String url) {
        CommonUtils.openLinkInExternalApp(context, url, R.string.richtext_decorated_link_open_error);
    }
}
