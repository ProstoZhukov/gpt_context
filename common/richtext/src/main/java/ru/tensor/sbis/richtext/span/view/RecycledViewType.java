package ru.tensor.sbis.richtext.span.view;

import ru.tensor.sbis.richtext.util.HtmlTag;

/**
 * Набор статичных типов ViewHolder, используемых {@link ru.tensor.sbis.design.collection_view.CollectionView.Adapter}
 * в кастомных View с богатым текстом, для хранения в глобальном {@link ru.tensor.sbis.design.collection_view.CollectionView.RecycledViewPool}
 *
 * @author am.boldinov
 */
public interface RecycledViewType {
    int TABLE_CELL = HtmlTag.TABLE_CELL.hashCode();
}
