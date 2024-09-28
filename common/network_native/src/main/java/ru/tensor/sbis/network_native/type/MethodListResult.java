package ru.tensor.sbis.network_native.type;

/**
 * Legacy-код
 * <p>
 * Created by da.rodionov on 22.07.15.
 */
@SuppressWarnings("unused")
public class MethodListResult {

    private RecordSet records;
    private boolean hasMore;

    public MethodListResult(RecordSet records, boolean hasMore) {
        this.records = records;
        this.hasMore = hasMore;
    }

    public RecordSet getRecords() {
        return records;
    }

    public void setRecords(RecordSet records) {
        this.records = records;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
