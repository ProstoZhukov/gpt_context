package ru.tensor.sbis.communicator.contacts_registry.ui.spinner;

import ru.tensor.sbis.communicator.declaration.model.EntitledItem;

/**
 * Created by aa.mironychev on 09.06.17.
 */

public enum ContactSortOrder implements EntitledItem {

    BY_LAST_MESSAGE_DATE(
            ru.tensor.sbis.communicator.design.R.string.communicator_spinner_item_sort_by_date,
            ru.tensor.sbis.communicator.design.R.string.communicator_spinner_header_sort_by_date
    ),
    BY_NAME(
            ru.tensor.sbis.communicator.design.R.string.communicator_spinner_item_sort_by_fio,
            ru.tensor.sbis.communicator.design.R.string.communicator_spinner_header_sort_by_fio
    );

    private final int mItemStringRes;
    private final int mHeaderStringRes;

    ContactSortOrder(int itemStringRes,
                     int headerStringRes) {
        mItemStringRes = itemStringRes;
        mHeaderStringRes = headerStringRes;
    }

    @Override
    public int getTitleRes() {
        return mItemStringRes;
    }

    public Integer getFilterTitleRes() {
        return mHeaderStringRes;
    }

}
