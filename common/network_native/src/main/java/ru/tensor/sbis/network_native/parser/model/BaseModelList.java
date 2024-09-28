package ru.tensor.sbis.network_native.parser.model;

import java.util.ArrayList;

/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 03.02.2016.
 */
@SuppressWarnings("unused")
public class BaseModelList extends ArrayList<BaseModel> {

    private boolean mIsHasMore;
    private BaseModel mExtendedParams;

    public boolean isHasMore() {
        return mIsHasMore;
    }

    public void setIsHasMore(boolean isHasMore) {
        mIsHasMore = isHasMore;
    }

    public void setExtendedParams(BaseModel extendedParams) {
        mExtendedParams = extendedParams;
    }

    public BaseModel getExtendedParams() {
        return mExtendedParams;
    }
}
