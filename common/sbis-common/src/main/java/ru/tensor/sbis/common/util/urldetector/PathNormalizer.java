package ru.tensor.sbis.common.util.urldetector;

import org.apache.commons.lang3.StringUtils;

import java.util.Stack;

/**
 * Created by se.petrova on 3/29/17.
 */

class PathNormalizer {

    /**
     * Normalizes the path by doing the following:
     * remove special spaces, decoding hex encoded characters,
     * gets rid of extra dots and slashes, and re-encodes it once
     */
    String normalizePath(String path) {

        if (StringUtils.isEmpty(path)) {
            return path;
        }
        path = UrlUtil.decode(path);
        path = sanitizeDotsAndSlashes(path);
        return UrlUtil.encode(path);
    }

    /**
     * 1. Replaces "/./" with "/" recursively.
     * 2. "/blah/asdf/.." -> "/blah"
     * 3. "/blah/blah2/blah3/../../blah4" -> "/blah/blah4"
     * 4. "//" -> "/"
     * 5. Adds a slash at the end if there isn't one
     */
    private static String sanitizeDotsAndSlashes(String path) {
        StringBuilder stringBuilder = new StringBuilder(path);
        Stack<Integer> slashIndexStack = new Stack<>();
        int index = 0;
        while (index < stringBuilder.length() - 1) {
            if (stringBuilder.charAt(index) == '/') {
                slashIndexStack.add(index);
                if (stringBuilder.charAt(index + 1) == '.') {
                    if (index < stringBuilder.length() - 2 && stringBuilder.charAt(index + 2) == '.') {
                        //If it looks like "/../" or ends with "/.."
                        if (index < stringBuilder.length() - 3 && stringBuilder.charAt(index + 3) == '/'
                                || index == stringBuilder.length() - 3) {
                            boolean endOfPath = index == stringBuilder.length() - 3;
                            slashIndexStack.pop();
                            int endIndex = index + 3;
                            // backtrack so we can detect if this / is part of another replacement
                            index = slashIndexStack.empty() ? -1 : slashIndexStack.pop() - 1;
                            int startIndex = endOfPath ? index + 1 : index;
                            stringBuilder.delete(startIndex + 1, endIndex);
                        }
                    } else if (index < stringBuilder.length() - 2 && stringBuilder.charAt(index + 2) == '/'
                            || index == stringBuilder.length() - 2) {
                        boolean endOfPath = index == stringBuilder.length() - 2;
                        slashIndexStack.pop();
                        int startIndex = endOfPath ? index + 1 : index;
                        stringBuilder.delete(startIndex, index + 2); // "/./" -> "/"
                        index--; // backtrack so we can detect if this / is part of another replacement
                    }
                } else if (stringBuilder.charAt(index + 1) == '/') {
                    slashIndexStack.pop();
                    stringBuilder.deleteCharAt(index);
                    index--;
                }
            }
            index++;
        }

        if (stringBuilder.length() == 0) {
            stringBuilder.append("/"); //Every path has at least a slash
        }

        return stringBuilder.toString();
    }
}
