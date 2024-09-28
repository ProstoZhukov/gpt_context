package ru.tensor.sbis.mvp.multiselection;

import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;

/**
 * Слушатель кликов мульти выбора
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface MultiSelectionItemClickListener {

    /**
     * @SelfDocumented
     */
    void onClickCheckbox(MultiSelectionItem item, int position);

    /**
     * @SelfDocumented
     */
    void onClickItem(MultiSelectionItem item, int position);

}