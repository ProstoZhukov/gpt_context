package ru.tensor.sbis.richtext.span.view;

import android.text.style.ClickableSpan;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Базовая вью-модель атрибутов View компонента для вставки в текст
 *
 * @author am.boldinov
 */
public abstract class BaseAttributesVM {

    @NonNull
    private final String mTag;
    @Nullable
    private ClickableSpan mClickableSpan;
    private final int mViewType;

    public BaseAttributesVM(@NonNull String tag) {
        mTag = tag;
        mViewType = tag.concat(getClass().getName()).hashCode();
    }

    /**
     * Возвращает html тег, к которому привязывается View компонент
     */
    @NonNull
    public String getTag() {
        return mTag;
    }

    /**
     * Возвращает тип View для работы с адаптером и переиспользованием элементов
     */
    public final int getViewType() {
        return mViewType;
    }

    /**
     * Возвращает шаблон обтекания компонентов богатого текста.
     * По умолчания берется шаблон разделения текста на разных параграфы и View вставляется
     * без обтекания в этот промежуток между параграфами.
     */
    @NonNull
    public ViewTemplate getTemplate() {
        return ViewTemplate.CENTER;
    }

    /**
     * Создает фабрику по созданию вью-холдеров для представления текущей вью-модели
     */
    @NonNull
    public abstract RichViewLayout.ViewHolderFactory createViewHolderFactory();

    /**
     * Устанавливает спан для обработки клика по View извне.
     * Необходимо использовать в случаях когда клик необходимо установить в момент парсинга текста,
     * например при оборачивании текущей View кликабельным тегом.
     */
    public void setClickableSpan(@Nullable ClickableSpan span) {
        mClickableSpan = span;
    }

    /**
     * Возвращает спан для обработки клика по View
     */
    @Nullable
    public ClickableSpan getClickableSpan() {
        return mClickableSpan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAttributesVM that = (BaseAttributesVM) o;
        return mTag.equals(that.mTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTag);
    }
}
