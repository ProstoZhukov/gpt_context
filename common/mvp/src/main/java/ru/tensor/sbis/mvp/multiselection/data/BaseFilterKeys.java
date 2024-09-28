package ru.tensor.sbis.mvp.multiselection.data;

import ru.tensor.sbis.communication_decl.recipient_selection.FilterKey;

/**
 * Base keys required in {@link SelectionFilter}
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public enum BaseFilterKeys implements FilterKey {

    SEARCH_QUERY("SEARCH_QUERY"),
    ITEMS_COUNT("ITEMS_COUNT"),
    FROM_POSITION("FROM_POSITION"),
    FROM_ITEM("FROM_ITEM"),
    FROM_PULL_TO_REFRESH("FROM_PULL_TO_REFRESH");

    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    private String key;

    BaseFilterKeys(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

}
