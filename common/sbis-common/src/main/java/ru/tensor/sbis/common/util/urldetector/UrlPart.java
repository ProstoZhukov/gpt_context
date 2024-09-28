package ru.tensor.sbis.common.util.urldetector;

/**
 * Created by se.petrova on 3/29/17.
 */

public enum UrlPart {
    FRAGMENT(null),
    QUERY(FRAGMENT),
    PATH(QUERY),
    PORT(PATH),
    HOST(PORT),
    USERNAME_PASSWORD(HOST),
    SCHEME(USERNAME_PASSWORD);

    /**
     * This is the next url part that follows.
     */
    private UrlPart mNextPart;

    UrlPart(UrlPart nextPart) {
        mNextPart = nextPart;
    }

    public UrlPart getNextPart() {
        return mNextPart;
    }
}
