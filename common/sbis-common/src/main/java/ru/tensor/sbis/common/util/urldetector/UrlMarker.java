package ru.tensor.sbis.common.util.urldetector;

/**
 * Created by se.petrova on 3/29/17.
 */

public class UrlMarker {

    private String mOriginalUrl;
    private int mSchemeIndex = -1;
    private int mUsernamePasswordIndex = -1;
    private int mHostIndex = -1;
    private int mPortIndex = -1;
    private int mPathIndex = -1;
    private int mQueryIndex = -1;
    private int mFragmentIndex = -1;

    public UrlMarker() {
    }

    public Url createUrl() {
        return new Url(this);
    }

    public void setOriginalUrl(String originalUrl) {
        mOriginalUrl = originalUrl;
    }

    String getOriginalUrl() {
        return mOriginalUrl;
    }

    public void setIndex(UrlPart urlPart, int index) {
        switch (urlPart) {
            case SCHEME:
                mSchemeIndex = index;
                break;
            case USERNAME_PASSWORD:
                mUsernamePasswordIndex = index;
                break;
            case HOST:
                mHostIndex = index;
                break;
            case PORT:
                mPortIndex = index;
                break;
            case PATH:
                mPathIndex = index;
                break;
            case QUERY:
                mQueryIndex = index;
                break;
            case FRAGMENT:
                mFragmentIndex = index;
                break;
            default:
                break;
        }
    }

    /**
     * @param urlPart The part you want the index of
     * @return Returns the index of the part
     */
    public int indexOf(UrlPart urlPart) {
        switch (urlPart) {
            case SCHEME:
                return mSchemeIndex;
            case USERNAME_PASSWORD:
                return mUsernamePasswordIndex;
            case HOST:
                return mHostIndex;
            case PORT:
                return mPortIndex;
            case PATH:
                return mPathIndex;
            case QUERY:
                return mQueryIndex;
            case FRAGMENT:
                return mFragmentIndex;
            default:
                return -1;
        }
    }

    public void unsetIndex(UrlPart urlPart) {
        setIndex(urlPart, -1);
    }

    /**
     * This is used in TestUrlMarker to set indices more easily.
     *
     * @param indices array of indices of size 7
     */
    protected UrlMarker setIndices(int[] indices) {
        if (indices == null || indices.length != 7) {
            throw new IllegalArgumentException("Malformed index array.");
        }
        setIndex(UrlPart.SCHEME, indices[0]);
        setIndex(UrlPart.USERNAME_PASSWORD, indices[1]);
        setIndex(UrlPart.HOST, indices[2]);
        setIndex(UrlPart.PORT, indices[3]);
        setIndex(UrlPart.PATH, indices[4]);
        setIndex(UrlPart.QUERY, indices[5]);
        setIndex(UrlPart.FRAGMENT, indices[6]);
        return this;
    }

}
