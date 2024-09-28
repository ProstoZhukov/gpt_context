package ru.tensor.sbis.common.util.urldetector.detection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.tensor.sbis.common.util.urldetector.Url;
import ru.tensor.sbis.common.util.urldetector.UrlMarker;
import ru.tensor.sbis.common.util.urldetector.UrlPart;
import ru.tensor.sbis.common.util.urldetector.UrlPosition;

/**
 * Created by se.petrova on 3/29/17.
 */

public class UrlDetector {

    /**
     * Contains the string to check for and remove if the scheme is this.
     */
    private static final String HTML_MAILTO = "mailto:";

    /**
     * Valid protocol schemes.
     */
    protected static final Set<String> VALID_SCHEMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "http://", "https://", "ftp://", "ftps://", "http%3a//", "https%3a//", "ftp%3a//", "ftps%3a//")));
    /**
     * Valid domains.
     */
    private static final Set<String> VALID_DOMAINS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "ax",
            "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm",
            "cn", "co", "cr", "cs", "cu", "cv", "cx", "cy", "cz", "dd", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "er", "es", "et", "eu", "fj", "fk", "fm", "fo", "fr",
            "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il",
            "im", "in", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "krd", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk",
            "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "me", "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz",
            "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa",
            "re", "ro", "rs", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "st", "su", "sv", "sy", "sz", "tc", "td", "tf",
            "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf",
            "ws", "ye", "yt", "za", "zm", "zw", "academy", "accountant", "accountants", "active", "actor", "adult", "aero", "agency", "airforce", "apartments", "app", "archi",
            "army", "associates", "asia", "attorney", "auction", "audio", "autos", "biz", "cat", "com", "coop", "edu", "gov", "info", "int", "jobs", "mil", "mobi", "museum",
            "name", "net", "one", "ong", "onl", "online", "ooo", "org", "organic", "partners", "parts", "party", "pharmacy", "photo", "photography", "photos", "physio", "pics",
            "pictures", "feedback", "pink", "pizza", "place", "plumbing", "plus", "poker", "porn", "post", "press", "pro", "productions", "prof", "properties", "property", "qpon",
            "racing", "recipes", "red", "rehab", "ren", "rent", "rentals", "repair", "report", "republican", "rest", "review", "reviews", "rich", "site", "tel", "travel", "xxx",
            "xyz", "yoga", "zone")));

    /**
     * The response of character matching.
     */
    private enum CharacterMatch {
        /**
         * The character was not matched.
         */
        CharacterNotMatched,
        /**
         * A character was matched with requires a stop.
         */
        CharacterMatchStop,
        /**
         * The character was matched which is a start of parentheses.
         */
        CharacterMatchStart
    }

    /**
     * Stores options for detection.
     */
    private final UrlDetectorOptions mOptions;

    /**
     * The input stream to read.
     */
    private final InputTextReader mReader;

    /**
     * Buffer to store temporary urls inside of.
     */
    private StringBuilder mBuffer = new StringBuilder();

    /**
     * Has the scheme been found in this iteration?
     */
    private boolean mHasScheme = false;

    /**
     * If the first character in the url is a quote, then look for matching quote at the end.
     */
    private boolean mQuoteStart = false;

    /**
     * If the first character in the url is a single quote, then look for matching quote at the end.
     */
    private boolean mSingleQuoteStart = false;

    /**
     * If we see a '[', didn't find an ipv6 address, and the bracket option is on, then look for urls inside the brackets.
     */
    private boolean mDontMatchIpv6 = false;

    /**
     * Stores the found urls.
     */
    private Map<UrlPosition, Url> mUrlMap = new HashMap<>();

    /**
     * Keeps the count of special characters used to match quotes and different types of brackets.
     */
    private HashMap<Character, Integer> mCharacterMatch = new HashMap<>();

    /**
     * Keeps track of certain indices to create a Url object.
     */
    private UrlMarker mCurrentUrlMarker = new UrlMarker();

    /**
     * The states to use to continue writing or not.
     */
    private enum ReadEndState {
        /**
         * The current url is valid.
         */
        ValidUrl,
        /**
         * The current url is invalid.
         */
        InvalidUrl
    }

    /**
     * Creates a new UrlDetector object used to find urls inside of text.
     *
     * @param content The content to search inside of.
     * @param options The UrlDetectorOptions to use when detecting the content.
     */
    public UrlDetector(String content, UrlDetectorOptions options) {
        mReader = new InputTextReader(content);
        mOptions = options;
    }

    /**
     * Gets the number of characters that were backtracked while reading the input. This is useful for performance
     * measurement.
     *
     * @return The count of characters that were backtracked while reading.
     */
    public int getBacktracked() {
        return mReader.getBacktrackedCount();
    }

    /**
     * Detects the urls and returns a map of detected url strings with it's positions.
     *
     * @return A map with detected urls and it's positions.
     */
    public Map<UrlPosition, Url> detect() {
        readDefault();
        return mUrlMap;
    }

    /**
     * The default input reader which looks for specific flags to start detecting the url.
     */
    private void readDefault() {
        //Keeps track of the number of characters read to be able to later cut out the domain name.
        int length = 0;

        //until end of string read the contents
        while (!mReader.eof()) {
            //read the next char to process.
            char curr = mReader.read();

            switch (curr) {
                case ' ':
                    //space was found, check if it's a valid single level domain.
                    if (mOptions.hasFlag(UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN) && mBuffer.length() > 0 && mHasScheme) {
                        mReader.goBack();
                        readDomainName(mBuffer.substring(length));
                    }
                    mBuffer.append(curr);
                    readEnd(ReadEndState.InvalidUrl);
                    length = 0;
                    break;
                case '%':
                    if (mReader.canReadChars(2)) {
                        if (mReader.peek(2).equalsIgnoreCase("3a")) {
                            mBuffer.append(curr);
                            mBuffer.append(mReader.read());
                            mBuffer.append(mReader.read());
                            length = processColon(length);
                        } else if (CharUtils.isHex(mReader.peekChar(0)) && CharUtils.isHex(mReader.peekChar(1))) {
                            mBuffer.append(curr);
                            mBuffer.append(mReader.read());
                            mBuffer.append(mReader.read());

                            readDomainName(mBuffer.substring(length));
                            length = 0;
                        }
                    }
                    break;
                case '\u3002': //non-standard dots
                case '\uFF0E':
                case '\uFF61':
                case '.': //"." was found, read the domain name using the start from length.
                    mBuffer.append(curr);
                    readDomainName(mBuffer.substring(length));
                    length = 0;
                    break;
                case '@': //Check the domain name after a username
                    if (mBuffer.length() > 0) {
                        mCurrentUrlMarker.setIndex(UrlPart.USERNAME_PASSWORD, length);
                        mBuffer.append(curr);
                        readDomainName(null);
                        length = 0;
                    }
                    break;
                case '[':
                    if (mDontMatchIpv6) {
                        //Check if we need to match characters. If we match characters and this is a start or stop of range,
                        //either way reset the world and start processing again.
                        if (checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                            readEnd(ReadEndState.InvalidUrl);
                            length = 0;
                        }
                    }
                    int beginning = mReader.getPosition();

                    //if it doesn't have a scheme, clear the buffer.
                    if (!mHasScheme) {
                        mBuffer.delete(0, mBuffer.length());
                    }
                    mBuffer.append(curr);

                    if (!readDomainName(mBuffer.substring(length))) {
                        //if we didn't find an ipv6 address, then check inside the brackets for urls
                        mReader.seek(beginning);
                        mDontMatchIpv6 = true;
                    }
                    length = 0;
                    break;
                case '/':
                    // "/" was found, then we either read a scheme, or if we already read a scheme, then
                    // we are reading a url in the format http://123123123/asdf

                    if (mHasScheme || (mOptions.hasFlag(UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN) && mBuffer.length() > 1)) {
                        //we already have the scheme, so then we already read:
                        //http://something/ <- if something is all numeric then its a valid url.
                        //OR we are searching for single level domains. We have buffer length > 1 condition
                        //to weed out infinite backtrack in cases of html5 roots

                        //unread this "/" and continue to check the domain name starting from the beginning of the domain
                        mReader.goBack();
                        readDomainName(mBuffer.substring(length));
                        length = 0;
                    } else {

                        //we don't have a scheme already, then clear state, then check for html5 root such as: "//google.com/"
                        // remember the state of the quote when clearing state just in case its "//google.com" so its not cleared.
                        readEnd(ReadEndState.InvalidUrl);
                        mBuffer.append(curr);
                        mHasScheme = readHtml5Root();
                        length = mBuffer.length();
                    }
                    break;
                case ':':
                    //add the ":" to the url and check for scheme/username
                    mBuffer.append(curr);
                    length = processColon(length);
                    break;
                default:
                    //Check if we need to match characters. If we match characters and this is a start or stop of range,
                    //either way reset the world and start processing again.
                    if (checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                        readEnd(ReadEndState.InvalidUrl);
                        length = 0;
                    } else {
                        mBuffer.append(curr);
                    }
                    break;
            }
        }
        if (mOptions.hasFlag(UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN) && mBuffer.length() > 0 && mHasScheme) {
            readDomainName(mBuffer.substring(length));
        }
    }

    /**
     * We found a ":" and is now trying to read either scheme, username/password
     *
     * @param length first index of the previous part (could be beginning of the buffer, beginning of the username/password, or beginning
     * @return new index of where the domain starts
     */
    private int processColon(int length) {
        if (mHasScheme) {
            //read it as username/password if it has scheme
            if (!readUserPass(length) && mBuffer.length() > 0) {
                //unread the ":" so that the domain reader can process it
                mReader.goBack();
                mBuffer.delete(mBuffer.length() - 1, mBuffer.length());

                int backtrackOnFail = mReader.getPosition() - mBuffer.length() + length;
                if (!readDomainName(mBuffer.substring(length))) {
                    //go back to length location and restart search
                    mReader.seek(backtrackOnFail);
                    readEnd(ReadEndState.InvalidUrl);
                }
                length = 0;
            }
        } else if (readScheme() && mBuffer.length() > 0) {
            mHasScheme = true;
            length = mBuffer.length(); //set length to be right after the scheme
        } else if (mBuffer.length() > 0 && mOptions.hasFlag(UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN)
                && mReader.canReadChars(1)) { //takes care of case like hi:
            mReader.goBack(); //unread the ":" so readDomainName can take care of the port
            mBuffer.delete(mBuffer.length() - 1, mBuffer.length());
            readDomainName(mBuffer.toString());
        } else {
            readEnd(ReadEndState.InvalidUrl);
            length = 0;
        }

        return length;
    }

    private boolean isValidScheme() {
        String buffer = mBuffer.toString().toLowerCase();
        for (String scheme : VALID_SCHEMES) {
            int pos = buffer.indexOf(scheme);
            if ((pos != -1) && (pos + scheme.length() == buffer.length())) {
                mBuffer.delete(0, pos);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the number of times the current character was seen in the document. Only special characters are tracked.
     *
     * @param curr The character to look for.
     * @return The number of times that character was seen
     */
    private int getCharacterCount(char curr) {
        Integer count = mCharacterMatch.get(curr);
        return count == null ? 0 : count;
    }

    /**
     * Increments the counter for the characters seen and return if this character matches a special character
     * that might require stopping reading the url.
     *
     * @param curr The character to check.
     * @return The state that this character requires.
     */
    private CharacterMatch checkMatchingCharacter(char curr) {

        //This is a quote and we are matching quotes.
        if ((curr == '\"' && mOptions.hasFlag(UrlDetectorOptions.QUOTE_MATCH))
                || (curr == '\'' && mOptions.hasFlag(UrlDetectorOptions.SINGLE_QUOTE_MATCH))) {
            boolean quoteStart;
            if (curr == '\"') {
                quoteStart = mQuoteStart;

                //remember that a double quote was found.
                mQuoteStart = true;
            } else {
                quoteStart = mSingleQuoteStart;

                //remember that a single quote was found.
                mSingleQuoteStart = true;
            }

            //increment the number of quotes found.
            int currVal = getCharacterCount(curr) + 1;
            mCharacterMatch.put(curr, currVal);

            //if there was already a quote found, or the number of quotes is even, return that we have to stop, else its a start.
            return quoteStart || currVal % 2 == 0 ? CharacterMatch.CharacterMatchStop : CharacterMatch.CharacterMatchStart;
        } else if (mOptions.hasFlag(UrlDetectorOptions.BRACKET_AND_QUOTE_MATCH) && (curr == '[' || curr == '{' || curr == '(' || curr == '"' || curr == '\'')) {
            //Look for start of bracket or quote
            mCharacterMatch.put(curr, getCharacterCount(curr) + 1);
            return CharacterMatch.CharacterMatchStart;
        } else if (mOptions.hasFlag(UrlDetectorOptions.XML) && (curr == '<')) {
            //If its html, look for "<"
            mCharacterMatch.put(curr, getCharacterCount(curr) + 1);
            return CharacterMatch.CharacterMatchStart;
        } else if ((mOptions.hasFlag(UrlDetectorOptions.BRACKET_AND_QUOTE_MATCH) && (curr == ']' || curr == '}' || curr == ')' || curr == '"' || curr == '\''))
                || (mOptions.hasFlag(UrlDetectorOptions.XML) && (curr == '>'))) {

            //If we catch a end bracket or quote increment its count and get rid of not ipv6 flag
            int currVal = getCharacterCount(curr) + 1;
            mCharacterMatch.put(curr, currVal);

            //now figure out what the start bracket or quote was associated with the closed bracket or quote.
            char match = '\0';
            switch (curr) {
                case ']':
                    match = '[';
                    break;
                case '}':
                    match = '{';
                    break;
                case ')':
                    match = '(';
                    break;
                case '>':
                    match = '<';
                    break;
                case '"':
                    match = '"';
                    break;
                case '\'':
                    match = '\'';
                    break;
                default:
                    break;
            }

            //If the number of open is greater then the number of closed, return a stop.
            return getCharacterCount(match) > currVal ? CharacterMatch.CharacterMatchStop
                    : CharacterMatch.CharacterMatchStart;
        }

        //Nothing else was found.
        return CharacterMatch.CharacterNotMatched;
    }

    /**
     * Checks if the url is in the format:
     * //google.com/static/js.js
     *
     * @return True if the url is in this format and was matched correctly.
     */
    private boolean readHtml5Root() {
        //end of input then go away.
        if (mReader.eof()) {
            return false;
        }

        //read the next character. If its // then return true.
        char curr = mReader.read();
        if (curr == '/') {
            mBuffer.append(curr);
            return true;
        } else {
            //if its not //, then go back and reset by 1 character.
            mReader.goBack();
            readEnd(ReadEndState.InvalidUrl);
        }
        return false;
    }

    /**
     * Reads the scheme and allows returns true if the scheme is http(s?)://
     *
     * @return True if the scheme was found, else false.
     */
    private boolean readScheme() {
        //Check if we are checking html and the length is longer than mailto:
        if (mOptions.hasFlag(UrlDetectorOptions.HTML) && mBuffer.length() >= HTML_MAILTO.length()) {
            //Check if the string is actually mailto: then just return nothing.
            if (HTML_MAILTO.equalsIgnoreCase(mBuffer.substring(mBuffer.length() - HTML_MAILTO.length()))) {
                return readEnd(ReadEndState.InvalidUrl);
            }
        }

        int originalLength = mBuffer.length();
        int numSlashes = 0;

        while (!mReader.eof()) {
            char curr = mReader.read();

            //if we match a slash, look for a second one.
            if (curr == '/') {
                mBuffer.append(curr);
                if (numSlashes == 1) {
                    //return only if its an approved protocol. This can be expanded to allow others
                    if (isValidScheme()) {
                        mCurrentUrlMarker.setIndex(UrlPart.SCHEME, 0);
                        return true;
                    }
                    return false;
                }
                numSlashes++;
            } else if (curr == ' ' || checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                //if we find a space or end of input, then nothing found.
                mBuffer.append(curr);
                return false;
            } else if (curr == '[') { //if we're starting to see an ipv6 address
                mReader.goBack(); //unread the '[', so that we can start looking for ipv6
                return false;
            } else if (originalLength > 0 || numSlashes > 0 || !CharUtils.isAlpha(curr)) {
                // if it's not a character a-z or A-Z then assume we aren't matching scheme, but instead
                // matching username and password.
                mReader.goBack();
                return readUserPass(0);
            }
        }

        return false;
    }

    /**
     * Reads the input and looks for a username and password.
     * Handles:
     * http://username:password@...
     *
     * @param beginningOfUsername Index of the buffer of where the username began
     * @return True if a valid username and password was found.
     */
    private boolean readUserPass(int beginningOfUsername) {

        //The start of where we are.
        int start = mBuffer.length();

        //keep looping until "done"
        boolean done = false;

        //if we had a dot in the input, then it might be a domain name and not a username and password.
        boolean rollback = false;
        while (!done && !mReader.eof()) {
            char curr = mReader.read();

            // if we hit this, then everything is ok and we are matching a domain name.
            if (curr == '@') {
                mBuffer.append(curr);
                mCurrentUrlMarker.setIndex(UrlPart.USERNAME_PASSWORD, beginningOfUsername);
                return readDomainName("");
            } else if (CharUtils.isDot(curr) || curr == '[') {
                //everything is still ok, just remember that we found a dot or '[' in case we might need to backtrack
                mBuffer.append(curr);
                rollback = true;
            } else if (curr == '#' || curr == ' ' || curr == '/'
                    || checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                //one of these characters indicates we are invalid state and should just return.
                rollback = true;
                done = true;
            } else {
                //all else, just append character assuming its ok so far.
                mBuffer.append(curr);
            }
        }

        if (rollback) {
            //got to here, so there is no username and password. (We didn't find a @)
            int distance = mBuffer.length() - start;
            mBuffer.delete(start, mBuffer.length());

            int currIndex = Math.max(mReader.getPosition() - distance - (done ? 1 : 0), 0);
            mReader.seek(currIndex);

            return false;
        } else {
            return readEnd(ReadEndState.InvalidUrl);
        }
    }

    /**
     * Try to read the current string as a domain name
     *
     * @param current The current string used.
     * @return Whether the domain is valid or not.
     */
    private boolean readDomainName(String current) {
        int hostIndex = current == null ? mBuffer.length() : mBuffer.length() - current.length();
        mCurrentUrlMarker.setIndex(UrlPart.HOST, hostIndex);
        //create the domain name reader and specify the handler that will be called when a quote character
        //or something is found.
        DomainNameReader reader =
                new DomainNameReader(mReader, mBuffer, current, mOptions, this::checkMatchingCharacter);

        //Try to read the dns and act on the response.
        DomainNameReader.ReaderNextState state = reader.readDomainName();
        switch (state) {
            case ValidDomainName:
                return readEnd(ReadEndState.ValidUrl);
            case ReadFragment:
                return readFragment();
            case ReadPath:
                return readPath();
            case ReadPort:
                return readPort();
            case ReadQueryString:
                return readQueryString();
            case ReadEmailUserName:
                return false;
            default:
                return readEnd(ReadEndState.InvalidUrl);
        }
    }

    /**
     * Reads the fragments which is the part of the url starting with #
     *
     * @return If a valid fragment was read true, else false.
     */
    private boolean readFragment() {
        mCurrentUrlMarker.setIndex(UrlPart.FRAGMENT, mBuffer.length() - 1);

        while (!mReader.eof()) {
            char curr = mReader.read();

            //if it's the end or space, then a valid url was read.
            if (curr == ' ' || checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                return readEnd(ReadEndState.ValidUrl);
            } else {
                //otherwise keep appending.
                mBuffer.append(curr);
            }
        }

        //if we are here, anything read is valid.
        return readEnd(ReadEndState.ValidUrl);
    }

    /**
     * Try to read the query string.
     *
     * @return True if the query string was valid.
     */
    private boolean readQueryString() {
        mCurrentUrlMarker.setIndex(UrlPart.QUERY, mBuffer.length() - 1);

        while (!mReader.eof()) {
            char curr = mReader.read();

            if (curr == '#') { //fragment
                mBuffer.append(curr);
                return readFragment();
            } else if (curr == ' ' || checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                //end of query string
                return readEnd(ReadEndState.ValidUrl);
            } else { //all else add to buffer.
                mBuffer.append(curr);
            }
        }
        //a valid url was read.
        return readEnd(ReadEndState.ValidUrl);
    }

    /**
     * Try to read the port of the url.
     *
     * @return True if a valid port was read.
     */
    private boolean readPort() {
        mCurrentUrlMarker.setIndex(UrlPart.PORT, mBuffer.length());
        //The length of the port read.
        int portLen = 0;
        while (!mReader.eof()) {
            //read the next one and remember the length
            char curr = mReader.read();
            portLen++;

            if (curr == '/') {
                //continue to read path
                mBuffer.append(curr);
                return readPath();
            } else if (curr == '?') {
                //continue to read query string
                mBuffer.append(curr);
                return readQueryString();
            } else if (curr == '#') {
                //continue to read fragment.
                mBuffer.append(curr);
                return readFragment();
            } else if (checkMatchingCharacter(curr) == CharacterMatch.CharacterMatchStop || !CharUtils.isNumeric(curr)) {
                //if we got here, then what we got so far is a valid url. don't append the current character.
                mReader.goBack();

                //no port found; it was something like google.com:hello.world
                if (portLen == 1) {
                    //remove the ":" from the end.
                    mBuffer.delete(mBuffer.length() - 1, mBuffer.length());
                }
                mCurrentUrlMarker.unsetIndex(UrlPart.PORT);
                return readEnd(ReadEndState.ValidUrl);
            } else {
                //this is a valid character in the port string.
                mBuffer.append(curr);
            }
        }

        //found a correct url
        return readEnd(ReadEndState.ValidUrl);
    }

    /**
     * Tries to read the path
     *
     * @return True if the path is valid.
     */
    private boolean readPath() {
        mCurrentUrlMarker.setIndex(UrlPart.PATH, mBuffer.length() - 1);
        while (!mReader.eof()) {
            //read the next char
            char curr = mReader.read();

            if (curr == ' ' || checkMatchingCharacter(curr) != CharacterMatch.CharacterNotMatched) {
                //if end of state and we got here, then the url is valid.
                return readEnd(ReadEndState.ValidUrl);
            }

            //append the char
            mBuffer.append(curr);

            //now see if we move to another state.
            if (curr == '?') {
                //if ? read query string
                return readQueryString();
            } else if (curr == '#') {
                //if # read the fragment
                return readFragment();
            }
        }

        //end of input then this url is good.
        return readEnd(ReadEndState.ValidUrl);
    }

    /**
     * The url has been read to here. Remember the url if its valid, and reset state.
     *
     * @param state The state indicating if this url is valid. If its valid it will be added to the list of urls.
     * @return True if the url was valid.
     */
    private boolean readEnd(ReadEndState state) {
        //if the url is valid and greater then 0
        if (state == ReadEndState.ValidUrl && mBuffer.length() > 0) {
            //get the last character. if its a quote, cut it off.
            int len = mBuffer.length();
            if (mQuoteStart && mBuffer.charAt(len - 1) == '\"') {
                mBuffer.delete(len - 1, len);
            }

            //Add the url to the map of good urls.
            if (mBuffer.length() > 0) {
                mCurrentUrlMarker.setOriginalUrl(mBuffer.toString());
                int end = mReader.getPosition();

                //in some cases the last symbol in the buffer might differ from the current reader
                //symbol, (for example, a comma will not be included into the buffer), therefore
                //we need to remove it to ensure proper url positioning
                if (mBuffer.charAt(mBuffer.length() - 1) != mReader.getContent()[end - 1]) {
                    end--;
                }

                //check that url without scheme have right domain
                Url url = mCurrentUrlMarker.createUrl();
                String[] splittedUrl = url.getHost().split("\\.");

                if (!url.hasOriginalScheme() && !VALID_DOMAINS.contains(splittedUrl[splittedUrl.length - 1].toLowerCase())) {
                    state = ReadEndState.InvalidUrl;
                } else {
                    mUrlMap.put(new UrlPosition(end - mBuffer.length(), end), url);
                }
            }
        }

        //clear out the buffer.
        mBuffer.delete(0, mBuffer.length());

        //reset the state of internal objects.
        mQuoteStart = false;
        mHasScheme = false;
        mDontMatchIpv6 = false;
        mCurrentUrlMarker = new UrlMarker();

        //return true if valid.
        return state == ReadEndState.ValidUrl;
    }
}
