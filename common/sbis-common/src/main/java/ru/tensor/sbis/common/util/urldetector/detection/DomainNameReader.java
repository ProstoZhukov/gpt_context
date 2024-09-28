package ru.tensor.sbis.common.util.urldetector.detection;

/**
 * Created by se.petrova on 3/29/17.
 */

public class DomainNameReader {

    /**
     * The minimum length of a ascii based top level domain.
     */
    private static final int MIN_TOP_LEVEL_DOMAIN = 2;

    /**
     * The maximum length of a ascii based top level domain.
     */
    private static final int MAX_TOP_LEVEL_DOMAIN = 22;

    /**
     * The maximum number that the url can be in a url that looks like:
     * http://123123123123/path
     */
    private static final long MAX_NUMERIC_DOMAIN_VALUE = 4294967295L;

    /**
     * The minimum number the url can be in a url that looks like:
     * http://123123123123/path
     */
    private static final long MIN_NUMERIC_DOMAIN_VALUE = 16843008L;

    /**
     * If the domain name is an ip address, for each part of the address, whats the minimum value?
     */
    private static final int MIN_IP_PART = 0;

    /**
     * If the domain name is an ip address, for each part of the address, whats the maximum value?
     */
    private static final int MAX_IP_PART = 255;

    /**
     * The start of the utf character code table which indicates that this character is an international character.
     * Everything below this value is either a-z,A-Z,0-9 or symbols that are not included in domain name.
     */
    private static final int INTERNATIONAL_CHAR_START = 192;

    /**
     * The maximum length of each label in the domain name.
     */
    private static final int MAX_LABEL_LENGTH = 64;

    /**
     * The maximum number of labels in a single domain name.
     */
    private static final int MAX_NUMBER_LABELS = 127;

    /**
     * The maximum domain name length.
     */
    private static final int MAX_DOMAIN_LENGTH = 255;

    /**
     * Encoded hex dot.
     */
    private static final String HEX_ENCODED_DOT = "2e";

    /**
     * This is the final return state of reading a domain name.
     */
    enum ReaderNextState {
        /**
         * Trying to read the domain name caused it to be invalid.
         */
        InvalidDomainName,
        /**
         * The domain name is found to be valid.
         */
        ValidDomainName,
        /**
         * Finished reading, next step should be to read the fragment.
         */
        ReadFragment,
        /**
         * Finished reading, next step should be to read the path.
         */
        ReadPath,
        /**
         * Finished reading, next step should be to read the port.
         */
        ReadPort,
        /**
         * Finished reading, next step should be to read the query string.
         */
        ReadQueryString,
        /**
         * Trying to read the domain name caused it to be email username.
         */
        ReadEmailUserName
    }

    /**
     * The interface that gets called for each character that's non-matching (to a valid domain name character) in to count
     * the matching quotes and parenthesis correctly.
     */
    interface mCharacterHandler {

        void addCharacter(char character);
    }

    /**
     * The currently written string buffer.
     */
    private StringBuilder mBuffer;

    /**
     * The domain name started with a partial domain name found. This is the original string of the domain name only.
     */
    private String mCurrent;

    /**
     * Detection option of this reader.
     */
    private UrlDetectorOptions mOptions;

    /**
     * Keeps track the number of dots that were found in the domain name.
     */
    private int mDots = 0;

    /**
     * Keeps track of the number of characters since the last "."
     */
    private int mCurrentLabelLength = 0;

    /**
     * Keeps track of the number of characters in the top level domain.
     */
    private int mTopLevelLength = 0;

    /**
     * Keeps track where the domain name started. This is non zero if the buffer starts with
     * http://username:password@...
     */
    private int mStartDomainName = 0;

    /**
     * Keeps track if the entire domain name is numeric.
     */
    private boolean mNumeric = false;

    /**
     * Keeps track if we are seeing an ipv6 type address.
     */
    private boolean mSeenBracket = false;

    /**
     * Keeps track if we have seen a full bracket set "[....]"; used for ipv6 type address.
     */
    private boolean mSeenCompleteBracketSet = false;

    /**
     * Keeps track if we have a zone index in the ipv6 address.
     */
    private boolean mZoneIndex = false;

    /**
     * Contains the input stream to read.
     */
    private final InputTextReader mReader;

    /**
     * Contains the handler for each character match.
     */
    private final mCharacterHandler mCharacterHandler;

    /**
     * Creates a new instance of the DomainNameReader object.
     *
     * @param reader            The input stream to read.
     * @param buffer            The string buffer to use for storing a domain name.
     * @param current           The current string that was thought to be a domain name.
     * @param options           The detector options of this reader.
     * @param mCharacterHandler The handler to call on each non-matching character to count matching quotes and stuff.
     */
    DomainNameReader(InputTextReader reader, StringBuilder buffer, String current, UrlDetectorOptions options,
                     mCharacterHandler mCharacterHandler) {
        mBuffer = buffer;
        mCurrent = current;
        mReader = reader;
        mOptions = options;
        this.mCharacterHandler = mCharacterHandler;
    }

    /**
     * Reads and parses the current string to make sure the domain name started where it was supposed to,
     * and the current domain name is correct.
     *
     * @return The next state to use after reading the current.
     */
    private ReaderNextState readCurrent() {

        if (mCurrent != null) {
            //Handles the case where the string is ".hello"
            if (mCurrent.length() == 1 && CharUtils.isDot(mCurrent.charAt(0))) {
                return ReaderNextState.InvalidDomainName;
            } else if (mCurrent.length() == 3 && mCurrent.equalsIgnoreCase("%" + HEX_ENCODED_DOT)) {
                return ReaderNextState.InvalidDomainName;
            }

            //The location where the domain name started.
            mStartDomainName = mBuffer.length() - mCurrent.length();

            //flag that the domain is currently all numbers and/or dots.
            mNumeric = true;

            //If an invalid char is found, we can just restart the domain from there.
            int newStart = 0;

            char[] currArray = mCurrent.toCharArray();
            int length = currArray.length;

            //hex special case
            boolean isAllHexSoFar = length > 2 && (currArray[0] == '0' && (currArray[1] == 'x' || currArray[1] == 'X'));

            int index = isAllHexSoFar ? 2 : 0;
            boolean done = false;

            while (index < length && !done) {
                //get the current character and update length counts.
                char curr = currArray[index];
                mCurrentLabelLength++;
                mTopLevelLength = mCurrentLabelLength;

                //Is the length of the last part > 64 (plus one since we just incremented)
                if (mCurrentLabelLength > MAX_LABEL_LENGTH) {
                    return ReaderNextState.InvalidDomainName;
                } else if (CharUtils.isDot(curr)) {
                    //found a dot. Increment dot count, and reset last length
                    mDots++;
                    mCurrentLabelLength = 0;
                } else if (curr == '[') {
                    mSeenBracket = true;
                    mNumeric = false;
                } else if (curr == '%' && index + 2 < length && CharUtils.isHex(currArray[index + 1])
                        && CharUtils.isHex(currArray[index + 2])) {
                    //handle url encoded dot
                    if (currArray[index + 1] == '2' && currArray[index + 2] == 'e') {
                        mDots++;
                        mCurrentLabelLength = 0;
                    } else {
                        mNumeric = false;
                    }
                    index += 2;
                } else if (isAllHexSoFar) {
                    //if it's a valid character in the domain that is not numeric
                    if (!CharUtils.isHex(curr)) {
                        mNumeric = false;
                        isAllHexSoFar = false;
                        index--; //backtrack to rerun last character knowing it isn't hex.
                    }
                } else if (CharUtils.isAlpha(curr) || curr == '-' || curr >= INTERNATIONAL_CHAR_START) {
                    mNumeric = false;
                } else if (!CharUtils.isNumeric(curr) && !mOptions.hasFlag(UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN)) {
                    //if its not mNumeric and not alphabetical, then restart searching for a domain from this point.
                    newStart = index + 1;
                    mCurrentLabelLength = 0;
                    mTopLevelLength = 0;
                    mNumeric = true;
                    mDots = 0;
                    done = true;
                }
                index++;
            }

            //An invalid character for the domain was found somewhere in the current buffer.
            //cut the first part of the domain out. For example:
            // http://asdf%asdf.google.com <- asdf.google.com is still valid, so restart from the %
            if (newStart > 0) {

                //make sure the location is not at the end. Otherwise the thing is just invalid.
                if (newStart < mCurrent.length()) {
                    mBuffer.replace(0, mBuffer.length(), mCurrent.substring(newStart));

                    //cut out the previous part, so now the domain name has to be from here.
                    mStartDomainName = 0;
                }

                //now after cutting if the buffer is just "." newStart > current (last character in current is invalid)
                if (newStart >= mCurrent.length() || mBuffer.toString().equals(".")) {
                    return ReaderNextState.InvalidDomainName;
                }
            }
        } else {
            mStartDomainName = mBuffer.length();
        }

        //all else is good, return OK
        return ReaderNextState.ValidDomainName;
    }

    /**
     * Reads the Dns and returns the next state the state machine should take in throwing this out, or continue processing
     * if this is a valid domain name.
     *
     * @return The next state to take.
     */
    ReaderNextState readDomainName() {

        //Read the current, and if its bad, just return.
        if (readCurrent() == ReaderNextState.InvalidDomainName) {
            return ReaderNextState.InvalidDomainName;
        }

        //while not done and not end of string keep reading.
        boolean done = false;
        while (!done && !mReader.eof()) {
            char curr = mReader.read();

            if (curr == '/') {
                //continue by reading the path
                return checkDomainNameValid(ReaderNextState.ReadPath, curr);
            } else if (curr == ':' && (!mSeenBracket || mSeenCompleteBracketSet)) {
                //Don't check for a port if it's in the middle of an ipv6 address
                //continue by reading the port.
                return checkDomainNameValid(ReaderNextState.ReadPort, curr);
            } else if (curr == '?') {
                //continue by reading the query string
                return checkDomainNameValid(ReaderNextState.ReadQueryString, curr);
            } else if (curr == '#') {
                //continue by reading the fragment
                return checkDomainNameValid(ReaderNextState.ReadFragment, curr);
            } else if (CharUtils.isDot(curr)
                    || (curr == '%' && mReader.canReadChars(2) && mReader.peek(2).equalsIgnoreCase(HEX_ENCODED_DOT))) {
                //if the current character is a dot or a urlEncodedDot

                //handles the case: hello..
                if (mCurrentLabelLength < 1) {
                    done = true;
                } else {
                    //append the "." to the domain name
                    mBuffer.append(curr);

                    //if it was not a normal dot, then it is url encoded
                    //read the next two chars, which are the hex representation
                    if (!CharUtils.isDot(curr)) {
                        mBuffer.append(mReader.read());
                        mBuffer.append(mReader.read());
                    }

                    //increment the dots only if it's not part of the zone index and reset the last length.
                    if (!mZoneIndex) {
                        mDots++;
                        mCurrentLabelLength = 0;
                    }

                    //if the length of the last section is longer than or equal to 64, it's too long to be a valid domain
                    if (mCurrentLabelLength >= MAX_LABEL_LENGTH) {
                        return ReaderNextState.InvalidDomainName;
                    }
                }
            } else if (mSeenBracket && (CharUtils.isHex(curr) || curr == ':' || curr == '[' || curr == ']' || curr == '%')
                    && !mSeenCompleteBracketSet) { //if this is an ipv6 address.
                switch (curr) {
                    case ':':
                        mCurrentLabelLength = 0;
                        break;
                    case '[':
                        // if we read another '[', we need to restart by re-reading from this bracket instead.
                        mReader.goBack();
                        return ReaderNextState.InvalidDomainName;
                    case ']':
                        mSeenCompleteBracketSet = true; //means that we already have a complete ipv6 address.
                        mZoneIndex = false; //set this back off so that we can keep counting dots after ipv6 is over.
                        break;
                    case '%': //set flag to subtract subsequent dots because it's part of the zone index
                        mZoneIndex = true;
                        break;
                    default:
                        mCurrentLabelLength++;
                        break;
                }
                mNumeric = false;
                mBuffer.append(curr);
            } else if (CharUtils.isAlphaNumeric(curr) || curr == '-' || curr >= INTERNATIONAL_CHAR_START) {
                //Valid domain name character. Either a-z, A-Z, 0-9, -, or international character
                if (mSeenCompleteBracketSet) {
                    //covers case of [fe80::]www.google.com
                    mReader.goBack();
                    done = true;
                } else {
                    //if its not numeric, remember that; excluded x/X for hex ip addresses.
                    if (curr != 'x' && curr != 'X' && !CharUtils.isNumeric(curr)) {
                        mNumeric = false;
                    }

                    //append to the states.
                    mBuffer.append(curr);
                    mCurrentLabelLength++;
                    mTopLevelLength = mCurrentLabelLength;
                }
            } else if (curr == '[' && !mSeenBracket) {
                mSeenBracket = true;
                mNumeric = false;
                mBuffer.append(curr);
            } else if (curr == '[' && mSeenCompleteBracketSet) { //Case where [::][ ...
                mReader.goBack();
                done = true;
            } else if (curr == '%' && mReader.canReadChars(2) && CharUtils.isHex(mReader.peekChar(0))
                    && CharUtils.isHex(mReader.peekChar(1))) {
                //append to the states.
                mBuffer.append(curr);
                mBuffer.append(mReader.read());
                mBuffer.append(mReader.read());
                mCurrentLabelLength += 3;
                mTopLevelLength = mCurrentLabelLength;
            } else if (curr == '@' && !mBuffer.toString().toLowerCase().startsWith("http://") && !mBuffer.toString().toLowerCase().startsWith("https://") &&
                    !mBuffer.toString().toLowerCase().startsWith("http%3a//") && !mBuffer.toString().toLowerCase().startsWith("https%3a//")) {
                mReader.goBack();
                return ReaderNextState.ReadEmailUserName;
            } else if (curr == '@' && (mBuffer.toString().toLowerCase().startsWith("http://") || mBuffer.toString().toLowerCase().startsWith("https://") ||
                    mBuffer.toString().toLowerCase().startsWith("http%3a//") || mBuffer.toString().toLowerCase().startsWith("https%3a//"))) {
                return ReaderNextState.InvalidDomainName;
            } else {
                //called to increment the count of matching characters
                mCharacterHandler.addCharacter(curr);

                //invalid character, we are done.
                done = true;
            }
        }

        //Check the domain name to make sure its ok.
        return checkDomainNameValid(ReaderNextState.ValidDomainName, null);
    }

    /**
     * Checks the current state of this object and returns if the valid state indicates that the
     * object has a valid domain name. If it does, it will return append the last character
     * and return the validState specified.
     *
     * @param validState The state to return if this check indicates that the dns is ok.
     * @param lastChar   The last character to add if the domain is ok.
     * @return The validState if the domain is valid, else ReaderNextState.InvalidDomainName
     */
    private ReaderNextState checkDomainNameValid(ReaderNextState validState, Character lastChar) {

        boolean valid = false;

        //Max domain length is 255 which includes the trailing "."
        //most of the time this is not included in the url.
        //If the mCurrentLabelLength is not 0 then the last "." is not included so add it.
        //Same with number of labels (or dots including the last)
        int lastDotLength =
                mBuffer.length() > 3 && mBuffer.substring(mBuffer.length() - 3).equalsIgnoreCase("%" + HEX_ENCODED_DOT) ? 3 : 1;

        int domainLength = mBuffer.length() - mStartDomainName + (mCurrentLabelLength > 0 ? lastDotLength : 0);
        int dotCount = mDots + (mCurrentLabelLength > 0 ? 1 : 0);
        if (domainLength >= MAX_DOMAIN_LENGTH || dotCount > MAX_NUMBER_LABELS) {
        } else if (mNumeric) {
            String testDomain = mBuffer.substring(mStartDomainName).toLowerCase();
            valid = isValidIpv4(testDomain);
        } else if (mSeenBracket) {
            String testDomain = mBuffer.substring(mStartDomainName).toLowerCase();
            valid = isValidIpv6(testDomain);
        } else if ((mCurrentLabelLength > 0 && mDots >= 1) || (mDots >= 2 && mCurrentLabelLength == 0)
                || (mOptions.hasFlag(UrlDetectorOptions.ALLOW_SINGLE_LEVEL_DOMAIN) && mDots == 0)) {

            int topStart = mBuffer.length() - mTopLevelLength;
            if (mCurrentLabelLength == 0) {
                topStart--;
            }
            topStart = Math.max(topStart, 0);

            //get the first 4 characters of the top level domain
            String topLevelStart = mBuffer.substring(topStart, topStart + Math.min(4, mBuffer.length() - topStart));

            //There is no size restriction if the top level domain is international (starts with "xn--")
            valid =
                    ((topLevelStart.equalsIgnoreCase("xn--") || (mTopLevelLength >= MIN_TOP_LEVEL_DOMAIN && mTopLevelLength <= MAX_TOP_LEVEL_DOMAIN)));
        }

        if (valid) {
            //if it's valid, add the last character (if specified) and return the valid state.
            if (lastChar != null) {
                mBuffer.append(lastChar);
            }
            return validState;
        }

        //Roll back one char if its invalid to handle: "00:41.<br />"
        //This gets detected as 41.br otherwise.
        mReader.goBack();

        //return invalid state.
        return ReaderNextState.InvalidDomainName;
    }

    /**
     * Handles Hexadecimal, octal, decimal, dotted decimal, dotted hex, dotted octal.
     *
     * @param testDomain the string we're testing
     * @return Returns true if it's a valid ipv4 address
     */
    private boolean isValidIpv4(String testDomain) {
        boolean valid = false;
        if (testDomain.length() > 0) {
            //handling format without dots. Ex: http://2123123123123/path/a, http://0x8242343/aksdjf
            if (mDots == 0) {
                try {
                    long value;
                    if (testDomain.length() > 2 && testDomain.charAt(0) == '0' && testDomain.charAt(1) == 'x') { //hex
                        value = Long.parseLong(testDomain.substring(2), 16);
                    } else if (testDomain.charAt(0) == '0') { //octal
                        value = Long.parseLong(testDomain.substring(1), 8);
                    } else { //decimal
                        value = Long.parseLong(testDomain);
                    }
                    valid = value <= MAX_NUMERIC_DOMAIN_VALUE && value >= MIN_NUMERIC_DOMAIN_VALUE;
                } catch (NumberFormatException e) {
                    valid = false;
                }
            } else if (mDots == 3) {
                //Dotted decimal/hex/octal format
                String[] parts = CharUtils.splitByDot(testDomain);
                valid = true;

                //check each part of the ip and make sure its valid.
                for (int i = 0; i < parts.length && valid; i++) {
                    String part = parts[i];
                    if (part.length() > 0) {
                        String parsedNum;
                        int base;
                        if (part.length() > 2 && part.charAt(0) == '0' && part.charAt(1) == 'x') { //dotted hex
                            parsedNum = part.substring(2);
                            base = 16;
                        } else if (part.charAt(0) == '0') { //dotted octal
                            parsedNum = part.substring(1);
                            base = 8;
                        } else { //dotted decimal
                            parsedNum = part;
                            base = 10;
                        }

                        int section;
                        if (parsedNum.length() == 0) {
                            section = 0;
                        } else {
                            try {
                                section = Integer.parseInt(parsedNum, base);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }
                        if (section < MIN_IP_PART || section > MAX_IP_PART) {
                            valid = false;
                        }
                    } else {
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Sees that there's an open "[", and is now checking for ":"'s and stopping when there is a ']' or invalid character.
     * Handles ipv4 formatted ipv6 addresses, zone indices, truncated notation.
     *
     * @return Returns true if it is a valid ipv6 address
     */
    private boolean isValidIpv6(String testDomain) {
        char[] domainArray = testDomain.toCharArray();

        // Return false if we don't see [....]
        // or if we only have '[]'
        // or if we detect [:8000: ...]; only [::8000: ...] is okay
        if (domainArray.length < 3 || domainArray[domainArray.length - 1] != ']' || domainArray[0] != '['
                || domainArray[1] == ':' && domainArray[2] != ':') {
            return false;
        }

        int numSections = 1;
        int hexDigits = 0;
        char prevChar = 0;

        //used to check ipv4 addresses at the end of ipv6 addresses.
        StringBuilder lastSection = new StringBuilder();
        boolean hexSection = true;

        // If we see a '%'. Example: http://[::ffff:0xC0.0x00.0x02.0xEB%251]
        boolean zoneIndiceMode = false;

        //If doubleColonFlag is true, that means we've already seen one "::"; we're not allowed to have more than one.
        boolean doubleColonFlag = false;

        int index = 0;
        for (; index < domainArray.length; index++) {
            switch (domainArray[index]) {
                case '[': //found beginning of ipv6 address
                    break;
                case '%':
                case ']': //found end of ipv6 address
                    if (domainArray[index] == '%') {
                        //see if there's a urlencoded dot
                        if (domainArray.length - index >= 2 && domainArray[index + 1] == '2' && domainArray[index + 2] == 'e') {
                            lastSection.append("%2e");
                            index += 2;
                            hexSection = false;
                            break;
                        }
                        zoneIndiceMode = true;
                    }
                    if (!hexSection && (!zoneIndiceMode || domainArray[index] == '%')) {
                        if (isValidIpv4(lastSection.toString())) {
                            numSections++; //ipv4 takes up 2 sections.
                        } else {
                            return false;
                        }
                    }
                    break;
                case ':':
                    if (prevChar == ':') {
                        if (doubleColonFlag) { //only allowed to have one "::" in an ipv6 address.
                            return false;
                        }
                        doubleColonFlag = true;
                    }

                    //This means that we reached invalid characters in the previous section
                    if (!hexSection) {
                        return false;
                    }

                    hexSection = true; //reset hex to true
                    hexDigits = 0; //reset count for hex digits
                    numSections++;
                    lastSection.delete(0, lastSection.length()); //clear last section
                    break;
                default:
                    if (zoneIndiceMode) {
                        if (!CharUtils.isUnreserved(domainArray[index])) {
                            return false;
                        }
                    } else {
                        lastSection.append(domainArray[index]); //collect our possible ipv4 address
                        if (hexSection && CharUtils.isHex(domainArray[index])) {
                            hexDigits++;
                        } else {
                            hexSection = false; //non hex digit.
                        }
                    }
                    break;
            }
            if (hexDigits > 4 || numSections > 8) {
                return false;
            }
            prevChar = domainArray[index];
        }

        //numSections != 1 checks for things like: [adf]
        //If there are more than 8 sections for the address or there isn't a double colon, then it's invalid.
        return numSections != 1 && (numSections >= 8 || doubleColonFlag);
    }
}
