package ru.tensor.sbis.common.util.urldetector;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import ru.tensor.sbis.common.util.urldetector.detection.UrlDetector;
import ru.tensor.sbis.common.util.urldetector.detection.UrlDetectorOptions;

/**
 * Created by se.petrova on 3/29/17.
 */

public class Url {

    private static final String DEFAULT_SCHEME = "http";
    private static final Map<String, Integer> SCHEME_PORT_MAP;

    static {
        SCHEME_PORT_MAP = new HashMap<>();
        SCHEME_PORT_MAP.put("http", 80);
        SCHEME_PORT_MAP.put("https", 443);
        SCHEME_PORT_MAP.put("ftp", 21);
    }

    private UrlMarker mUrlMarker;
    private String mScheme;
    private String mUsername;
    private String mPassword;
    private String mHost;
    private int mPort = 0;
    private String mPath;
    private String mQuery;
    private String mFragment;
    private String mOriginalUrl;

    protected Url(UrlMarker urlMarker) {
        mUrlMarker = urlMarker;
        mOriginalUrl = urlMarker.getOriginalUrl();
    }

    /**
     * Returns a url given a single url.
     */
    public static Url create(String url) throws MalformedURLException {
        String formattedString = UrlUtil.removeSpecialSpaces(url.trim().replace(" ", "%20"));
        Map<UrlPosition, Url> urls = new UrlDetector(formattedString, UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN).detect();
        if (urls.size() == 1) {
            return urls.values().iterator().next();
        } else if (urls.size() == 0) {
            throw new MalformedURLException("We couldn't find any urls in string: " + url);
        } else {
            throw new MalformedURLException("We found more than one url in string: " + url);
        }
    }

    /**
     * Returns a normalized url given a url object
     */
    public NormalizedUrl normalize() {
        return new NormalizedUrl(mUrlMarker);
    }

    @Override
    public String toString() {
        return this.getFullUrl();
    }

    public boolean hasOriginalScheme() {
        return getOriginalUrl().toLowerCase().startsWith("http");
    }

    /**
     * Note that this includes the fragment
     *
     * @return Formats the url to: [scheme]://[username]:[password]@[host]:[port]/[path]?[query]#[fragment]
     */
    public String getFullUrl() {
        return (getFullUrlWithoutFragment() + StringUtils.defaultString(getFragment())).toLowerCase();
    }

    /**
     * @return Formats the url to: [scheme]://[username]:[password]@[host]:[port]/[path]?[query]
     */
    public String getFullUrlWithoutFragment() {
        StringBuilder url = new StringBuilder();
        if (!StringUtils.isEmpty(getScheme())) {
            url.append(getScheme());
            url.append(":");
        }
        url.append("//");

        if (!StringUtils.isEmpty(getUsername())) {
            url.append(getUsername());
            if (!StringUtils.isEmpty(getPassword())) {
                url.append(":");
                url.append(getPassword());
            }
            url.append("@");
        }

        url.append(getHost());
        if (getPort() > 0 && getPort() != SCHEME_PORT_MAP.get(getScheme())) {
            url.append(":");
            url.append(getPort());
        }

        url.append(getPath());
        url.append(getQuery());

        return url.toString();
    }

    public String getScheme() {
        if (mScheme == null) {
            if (exists(UrlPart.SCHEME)) {
                mScheme = getPart(UrlPart.SCHEME);
                int index = mScheme.indexOf(":");
                if (index != -1) {
                    mScheme = mScheme.substring(0, index);
                }
            } else if (!mOriginalUrl.startsWith("//")) {
                mScheme = DEFAULT_SCHEME;
            }
        }
        return StringUtils.defaultString(mScheme);
    }

    public String getUsername() {
        if (mUsername == null) {
            populateUsernamePassword();
        }
        return StringUtils.defaultString(mUsername);
    }

    public String getPassword() {
        if (mPassword == null) {
            populateUsernamePassword();
        }
        return StringUtils.defaultString(mPassword);
    }

    public String getHost() {
        if (mHost == null) {
            mHost = getPart(UrlPart.HOST);
            if (exists(UrlPart.PORT)) {
                mHost = mHost.substring(0, mHost.length() - 1);
            }
        }
        return mHost;
    }

    /**
     * port = 0 means it hasn't been set yet. port = -1 means there is no port
     */
    public int getPort() {
        if (mPort == 0) {
            String portString = getPart(UrlPart.PORT);
            if (portString != null && !portString.isEmpty()) {
                try {
                    mPort = Integer.parseInt(portString);
                } catch (NumberFormatException e) {
                    mPort = -1;
                }
            } else if (SCHEME_PORT_MAP.containsKey(getScheme())) {
                mPort = SCHEME_PORT_MAP.get(getScheme());
            } else {
                mPort = -1;
            }
        }
        return mPort;
    }

    public String getPath() {
        if (mPath == null) {
            mPath = exists(UrlPart.PATH) ? getPart(UrlPart.PATH) : "/";
        }
        return mPath;
    }

    public String getQuery() {
        if (mQuery == null) {
            mQuery = getPart(UrlPart.QUERY);
        }
        return StringUtils.defaultString(mQuery);
    }

    public String getFragment() {
        if (mFragment == null) {
            mFragment = getPart(UrlPart.FRAGMENT);
        }
        return StringUtils.defaultString(mFragment);
    }

    /**
     * Always returns null for non normalized urls.
     */
    public byte[] getHostBytes() {
        return null;
    }

    public String getOriginalUrl() {
        return mOriginalUrl;
    }

    private void populateUsernamePassword() {
        if (exists(UrlPart.USERNAME_PASSWORD)) {
            String usernamePassword = getPart(UrlPart.USERNAME_PASSWORD);
            String[] usernamePasswordParts = usernamePassword.substring(0, usernamePassword.length() - 1).split(":");
            if (usernamePasswordParts.length == 1) {
                mUsername = usernamePasswordParts[0];
            } else if (usernamePasswordParts.length == 2) {
                mUsername = usernamePasswordParts[0];
                mPassword = usernamePasswordParts[1];
            }
        }
    }

    /**
     * @param urlPart The url part we are checking for existence
     * @return Returns true if the part exists.
     */
    private boolean exists(UrlPart urlPart) {
        return urlPart != null && mUrlMarker.indexOf(urlPart) >= 0;
    }

    /**
     * For example, in http://yahoo.com/lala/, nextExistingPart(UrlPart.HOST) would return UrlPart.PATH
     *
     * @param urlPart The current url part
     * @return Returns the next part; if there is no existing next part, it returns null
     */
    private UrlPart nextExistingPart(UrlPart urlPart) {
        UrlPart nextPart = urlPart.getNextPart();
        if (exists(nextPart)) {
            return nextPart;
        } else if (nextPart == null) {
            return null;
        } else {
            return nextExistingPart(nextPart);
        }
    }

    /**
     * @param part The part that we want. Ex: host, path
     */
    private String getPart(UrlPart part) {
        if (!exists(part)) {
            return null;
        }

        UrlPart nextPart = nextExistingPart(part);
        if (nextPart == null) {
            return mOriginalUrl.substring(mUrlMarker.indexOf(part));
        }
        return mOriginalUrl.substring(mUrlMarker.indexOf(part), mUrlMarker.indexOf(nextPart));
    }

    public boolean isEmail() {
        return !getScheme().isEmpty() && !getUsername().isEmpty() && getPassword().isEmpty() && !getHost().isEmpty() &&
                getPort() == 80 && getPath().equals("/") && getQuery().isEmpty() && getFragment().isEmpty();
    }

    protected void setRawPath(String path) {
        mPath = path;
    }

    protected void setRawHost(String host) {
        mHost = host;
    }

    protected String getRawPath() {
        return mPath;
    }

    protected String getRawHost() {
        return mHost;
    }

    protected UrlMarker getUrlMarker() {
        return mUrlMarker;
    }

    public boolean isFtp() {
        return getScheme().equalsIgnoreCase("ftp") || getScheme().equalsIgnoreCase("ftps");
    }
}
