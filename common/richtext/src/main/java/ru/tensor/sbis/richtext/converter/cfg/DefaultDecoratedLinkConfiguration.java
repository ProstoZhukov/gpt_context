package ru.tensor.sbis.richtext.converter.cfg;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.richtext.RichTextPlugin;
import ru.tensor.sbis.richtext.contract.DecoratedLinkOpenDependency;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkRepository;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkRepositoryImpl;
import ru.tensor.sbis.richtext.span.decoratedlink.DefaultDecoratedLinkOpener;

/**
 * Используемая по умолчанию конфигурация декорированных ссылок {@link ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkSpan}
 *
 * @author am.boldinov
 */
public class DefaultDecoratedLinkConfiguration implements DecoratedLinkConfiguration {

    @Nullable
    private final DecoratedLinkOpenDependency mLinkOpenDependency;

    public DefaultDecoratedLinkConfiguration(@Nullable DecoratedLinkOpenDependency linkOpenDependency) {
        mLinkOpenDependency = linkOpenDependency;
    }

    @NonNull
    @Override
    public DecoratedLinkRepository provideRepository(@NonNull Context context) {
        return new DecoratedLinkRepositoryImpl(context, RichTextPlugin.component.getDecoratedLinkServiceRepository());
    }

    @NonNull
    @Override
    public DecoratedLinkOpener provideLinkOpener() {
        return new DefaultDecoratedLinkOpener(mLinkOpenDependency);
    }
}
