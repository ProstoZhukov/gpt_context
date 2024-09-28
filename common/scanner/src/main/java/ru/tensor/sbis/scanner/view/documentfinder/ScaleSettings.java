package ru.tensor.sbis.scanner.view.documentfinder;

import me.relex.photodraweeview.IAttacher;

/**
 * @author am.boldinov
 */
class ScaleSettings {

    private float mMinScale = IAttacher.DEFAULT_MIN_SCALE;
    private float mMidScale = IAttacher.DEFAULT_MID_SCALE;
    private float mMaxScale = IAttacher.DEFAULT_MAX_SCALE;
    private float mOrigMinScale = mMinScale;
    private float mOrigMidScale = mMidScale;
    private float mOrigMaxScale = mMaxScale;

    void setMaximum(float maximumScale) {
        checkZoomLevels(mMinScale, mMidScale, maximumScale);
        mMaxScale = maximumScale;
        mOrigMaxScale = maximumScale;
    }

    void setMedium(float mediumScale) {
        checkZoomLevels(mMinScale, mediumScale, mMaxScale);
        mMidScale = mediumScale;
        mOrigMidScale = mediumScale;
    }

    void setMinimum(float minimumScale) {
        checkZoomLevels(minimumScale, mMidScale, mMaxScale);
        mMinScale = minimumScale;
        mOrigMinScale = minimumScale;
    }

    void postMaximum(float maximumScaleFactor) {
        mMaxScale = mOrigMaxScale - maximumScaleFactor;
    }

    void postMedium(float mediumScaleFactor) {
        mMidScale = mOrigMidScale - mediumScaleFactor;
    }

    void postMinimum(float minimumScaleFactor) {
        mMinScale = mOrigMinScale - minimumScaleFactor;
    }

    void postAll(float scaleFactor) {
        postMinimum(scaleFactor);
        postMedium(scaleFactor);
        postMaximum(scaleFactor);
    }

    float getMinimum() {
        return mMinScale;
    }

    float getMedium() {
        return mMidScale;
    }

    float getMaximum() {
        return mMaxScale;
    }

    private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException("MinZoom has to be less than MidZoom");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException("MidZoom has to be less than MaxZoom");
        }
    }
}
