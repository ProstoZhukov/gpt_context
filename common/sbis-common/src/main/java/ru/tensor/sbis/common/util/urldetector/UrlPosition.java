package ru.tensor.sbis.common.util.urldetector;

/**
 * Created by se.petrova on 02.04.17.
 */
public class UrlPosition {

    private int mStart;
    private int mEnd;

    public UrlPosition(int start, int end) {
        this.mStart = start;
        this.mEnd = end;
    }

    public int getStart() {
        return mStart;
    }

    public void setStart(int start) {
        this.mStart = start;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setEnd(int end) {
        this.mEnd = end;
    }
}
