package ru.tensor.sbis.common.util.urldetector;

import java.net.MalformedURLException;

/**
 * Created by se.petrova on 3/29/17.
 */

public class NormalizedUrl extends Url {

    private boolean mIsPopulated = false;
    private byte[] mHostBytes;

    NormalizedUrl(UrlMarker urlMarker) {
        super(urlMarker);
    }

    /**
     * Returns a normalized url given a single url.
     */
    public static NormalizedUrl create(String url) throws MalformedURLException {
        return Url.create(url).normalize();
    }

    @Override
    public String getHost() {
        if (getRawHost() == null) {
            populateHostAndHostBytes();
        }
        return getRawHost();
    }

    @Override
    public String getPath() {
        if (getRawPath() == null) {
            setRawPath(new PathNormalizer().normalizePath(super.getPath()));
        }
        return getRawPath();
    }

    /**
     * Returns the byte representation of the ip address. If the host is not an ip address, it returns null.
     */
    @Override
    public byte[] getHostBytes() {
        if (mHostBytes == null) {
            populateHostAndHostBytes();
        }
        return mHostBytes;
    }

    private void populateHostAndHostBytes() {
        if (!mIsPopulated) {
            HostNormalizer hostNormalizer = new HostNormalizer(super.getHost());
            setRawHost(hostNormalizer.getNormalizedHost());
            mHostBytes = hostNormalizer.getBytes();
            mIsPopulated = true;
        }
    }
}
