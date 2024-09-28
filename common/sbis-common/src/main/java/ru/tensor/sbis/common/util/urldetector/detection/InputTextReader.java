package ru.tensor.sbis.common.util.urldetector.detection;

/**
 * Created by se.petrova on 3/29/17.
 */

public class InputTextReader {

    /**
     * The number of times something can be backtracked is this multiplier times the length of the string.
     */
    private static final int MAX_BACKTRACK_MULTIPLIER = 10;

    /**
     * The content to read.
     */
    private final char[] mContent;

    /**
     * The current position in the content we are looking at.
     */
    private int mIndex = 0;

    /**
     * Contains the amount of characters that were backtracked. This is used for performance analysis.
     */
    private int mBacktracked = 0;

    /**
     * When detecting for exceeding the backtrack limit, make sure the text is at least 20 characters.
     */
    private final static int MINIMUM_BACKTRACK_LENGTH = 20;

    /**
     * Creates a new instance of the InputTextReader using the content to read.
     *
     * @param content The content to read.
     */
    public InputTextReader(String content) {
        mContent = content.toCharArray();
    }

    /**
     * Reads a single char from the content stream and increments the index.
     *
     * @return The next available character.
     */
    public char read() {
        char chr = mContent[mIndex++];
        return CharUtils.isWhiteSpace(chr) ? ' ' : chr;
    }

    /**
     * Peeks at the next number of chars and returns as a string without incrementing the current index.
     *
     * @param numberChars The number of chars to peek.
     */
    String peek(int numberChars) {
        return new String(mContent, mIndex, numberChars);
    }

    /**
     * Gets the character in the array offset by the current index.
     *
     * @param offset The number of characters to offset.
     * @return The character at the location of the index plus the provided offset.
     */
    char peekChar(int offset) {
        if (!canReadChars(offset)) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return mContent[mIndex + offset];
    }

    /**
     * Returns true if the reader has more the specified number of chars.
     *
     * @param numberChars The number of chars to see if we can read.
     * @return True if we can read this number of chars, else false.
     */
    boolean canReadChars(int numberChars) {
        return mContent.length >= mIndex + numberChars;
    }

    /**
     * Checks if the current stream is at the end.
     *
     * @return True if the stream is at the end and no more can be read.
     */
    public boolean eof() {
        return mContent.length <= mIndex;
    }

    /**
     * Gets the current position in the stream.
     *
     * @return The index to the current position.
     */
    public int getPosition() {
        return mIndex;
    }

    /**
     * Gets the original text.
     *
     * @return The char array witch contains original text.
     */
    public char[] getContent() {
        return mContent;
    }

    /**
     * Gets the total number of characters that were backtracked when reading.
     */
    int getBacktrackedCount() {
        return mBacktracked;
    }

    /**
     * Moves the index to the specified position.
     *
     * @param position The position to set the index to.
     */
    void seek(int position) {
        int backtrackLength = Math.max(mIndex - position, 0);
        mBacktracked += backtrackLength;
        mIndex = position;
        checkBacktrackLoop(backtrackLength);
    }

    /**
     * Goes back a single character.
     */
    void goBack() {
        mBacktracked++;
        mIndex--;
        checkBacktrackLoop(1);
    }

    private void checkBacktrackLoop(int backtrackLength) {
        if (mBacktracked > (mContent.length * MAX_BACKTRACK_MULTIPLIER)) {
            if (backtrackLength < MINIMUM_BACKTRACK_LENGTH) {
                backtrackLength = MINIMUM_BACKTRACK_LENGTH;
            }

            int start = Math.max(mIndex, 0);
            if (start + backtrackLength > mContent.length) {
                backtrackLength = mContent.length - start;
            }

            String badText = new String(mContent, start, backtrackLength);
            throw new NegativeArraySizeException("Backtracked max amount of characters. Endless loop detected. Bad Text: '"
                    + badText + "'");
        }
    }
}
