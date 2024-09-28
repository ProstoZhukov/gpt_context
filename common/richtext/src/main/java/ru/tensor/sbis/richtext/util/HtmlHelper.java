package ru.tensor.sbis.richtext.util;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;

import kotlin.Pair;
import ru.tensor.sbis.common.util.PreviewerUrlUtil;
import ru.tensor.sbis.richtext.span.StandartLineHeightSpan;
import timber.log.Timber;

/**
 * Утилита для работы с html цветами, шрифтами и текстом
 *
 * @author am.boldinov
 */
public class HtmlHelper {

    /**
     * Предикат для пропуска пробелов
     */
    @NonNull
    public static final Predicate<Character> IGNORE_SPACE_PREDICATE = character ->
            character != '\n' && isSpaceCharacter(character);
    /**
     * Предикат без пропуска символов
     */
    @NonNull
    public static final Predicate<Character> NO_IGNORE_PREDICATE = character -> false;
    public static final char EMPTY_CONTENT_SYMBOL = '\u2063';
    public static final char VIEW_SYMBOL = '\u2061';
    public static final char NBSP = 160; // неразрывный пробел

    private static final double HTML_CONVERT_FONT_SIZE_FACTOR = 0.75;

    @NonNull
    private static final HashMap<String, Integer> sColorNameMap = new HashMap<>();

    static {
        sColorNameMap.put("black", -16777216);
        sColorNameMap.put("darkgray", Color.DKGRAY);
        sColorNameMap.put("gray", -8355712);
        sColorNameMap.put("lightgray", Color.LTGRAY);
        sColorNameMap.put("white", Color.WHITE);
        sColorNameMap.put("red", -65536);
        sColorNameMap.put("green", -16744448);
        sColorNameMap.put("blue", -16776961);
        sColorNameMap.put("yellow", Color.YELLOW);
        sColorNameMap.put("cyan", Color.CYAN);
        sColorNameMap.put("magenta", Color.MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", Color.DKGRAY);
        sColorNameMap.put("grey", -8355712);
        sColorNameMap.put("lightgrey", Color.LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", -8388480);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);
    }

    /**
     * Поддерживаемы типы шрифтов
     */
    public enum FontType {
        PT("pt"),
        PX("px");

        private final String type;

        FontType(String type) {
            this.type = type;
        }

        /**
         * Возвращает строковое представление типа шрифта
         */
        public String type() {
            return type;
        }

        /**
         * Определяет тип шрифта из строки
         */
        @Nullable
        public static FontType detect(@NonNull String input) {
            for (FontType value : values()) {
                if (input.contains(value.type())) {
                    return value;
                }
            }
            return null;
        }
    }

    /**
     * Определяет цвет по строковому представлению
     */
    @Nullable
    @ColorInt
    public static Integer parseColor(@NonNull String colorName) {
        Integer color = sColorNameMap.get(colorName.toLowerCase());
        if (color == null) {
            try {
                return Color.parseColor(colorName);
            } catch (IllegalArgumentException e) {
                // ignore unknown color
            }
        }
        return color;
    }

    /**
     * Определяет размер шрифта по строковому представлению
     */
    @Nullable
    public static Integer parseFontSize(@NonNull Context context, @NonNull String fontSize) {
        try {
            final FontType fontType = FontType.detect(fontSize);
            if (fontType == FontType.PX) { // пока поддержка только px
                final Number parsed = NumberFormat.getInstance().parse(fontSize.replace(".", ","));
                if (parsed != null) {
                    final float pxValue = convertFontSize(parsed.floatValue(), fontType, FontType.PX);
                    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pxValue, context.getResources().getDisplayMetrics());
                }
            }
        } catch (ParseException e) {
            Timber.d(e);
        }
        return null;
    }

    /**
     * Определяет является ли строка переносом в заданном интервале
     */
    public static boolean isBreakLine(@NonNull Spanned text, int start, int end) {
        return start == end - 1 && text.charAt(start) == CharUtils.LF
                || start == end - 2 && text.charAt(start) == ' ' && text.charAt(start + 1) == CharUtils.LF;
    }


    /**
     * Добавляет перенос строки, аналогично {@link HtmlHelper#appendLineBreak(Editable, int)},
     * но пропускает пробелы между переносами при поиске, например \n  \n
     */
    public static void appendLineBreakIgnoreSpace(@NonNull Editable text, int offsetCheck) {
        if (text.length() == 0) {
            return;
        }
        if (checkNeedAppendLineBreak(text, offsetCheck, IGNORE_SPACE_PREDICATE)) {
            text.append(StringUtils.LF);
        }
    }

    /**
     * Добавляет перенос строки в текст с возможностью проверки наличия предыдущих переносов.
     *
     * @param text        входящая строка
     * @param offsetCheck смещение для проверки предыдущих переносов относительно длины текста.
     *                    Если передано значение > 0 и они присутствуют на этих позициях с конца, то перенос добавлен не будет.
     */
    public static void appendLineBreak(@NonNull Editable text, int offsetCheck) {
        if (text.length() == 0) {
            return;
        }
        if (checkNeedAppendLineBreak(text, offsetCheck, NO_IGNORE_PREDICATE)) {
            text.append(StringUtils.LF);
        }
    }

    /**
     * Добавляет перенос строки в текст и изменяет его высоту на необходимую.
     * В случае если переданная высота <= 0 то будет использоваться дефолтная высота двух \n
     *
     * @param text   входящая строка
     * @param height высота
     */
    public static void appendLineBreakHeight(@NonNull Editable text, int height) {
        if (text.length() > 0) {
            HtmlHelper.appendLineBreakIgnoreSpace(text, 2);
            HtmlHelper.appendLineBreakIgnoreSpace(text, 2); // добавляем 2 переноса строки, чтоб один из них изменить по высоте
            if (height > 0) {
                final int end = text.length();
                text.setSpan(new StandartLineHeightSpan(height), end - 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static boolean checkNeedAppendLineBreak(@NonNull Editable text, int offsetCheck,
                                                    @NonNull Predicate<Character> ignorePredicate) {
        boolean result = offsetCheck == 0;
        int depth = 0;
        for (int i = 0; i < offsetCheck; i++) {
            final int index = text.length() - (i + 1 + depth);
            if (index < 0) {
                break;
            }
            final char ch = text.charAt(index);
            if (ch != '\n') {
                if (ignorePredicate.apply(ch)) {
                    i--;
                    depth++;
                } else {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Является ли последний символ строки переносом
     */
    public static boolean checkLastIsLineBreak(@NonNull Editable text) {
        return !checkNeedAppendLineBreak(text, 1, NO_IGNORE_PREDICATE);
    }

    /**
     * Осуществляет поиск следующего символа
     *
     * @param text            исходный текст
     * @param from            от какой позиции необходимо вернуть символ
     * @param ascent          направление, в котором необходимо осуществить поиск
     * @param ignorePredicate предикат для пропуска ненужных символов
     * @return пару, символ и позиция символа в тексте
     */
    @Nullable
    public static Pair<Character, Integer> nextCharacter(@NonNull Editable text, int from, boolean ascent, @Nullable Predicate<Character> ignorePredicate) {
        while (from >= -1 && from < text.length()) {
            if (ascent) {
                from++;
            } else {
                from--;
            }
            if (from < 0 || from == text.length()) {
                return null;
            }
            final char ch = text.charAt(from);
            if (ignorePredicate == null || !ignorePredicate.apply(ch)) {
                return new Pair<>(ch, from);
            }
        }
        return null;
    }

    /**
     * Является ли символ пробелом
     */
    public static boolean isSpaceCharacter(char character) {
        return Character.isWhitespace(character) || Character.isSpaceChar(character);
    }

    /**
     * Конвертирует размер шрифта в зависимости от типа
     */
    @SuppressWarnings("WeakerAccess")
    public static float convertFontSize(float inputSize, @NonNull FontType fromType, @NonNull FontType toType) {
        switch (fromType) {
            case PT:
                if (toType == FontType.PX) {
                    return (float) ((double) inputSize / HTML_CONVERT_FONT_SIZE_FACTOR);
                }
                break;
            case PX:
                if (toType == FontType.PT) {
                    return (float) ((double) inputSize * HTML_CONVERT_FONT_SIZE_FACTOR);
                }
                break;
        }
        return inputSize;
    }

    @Nullable
    public static String parseCssStyleValue(@Nullable String style, @NonNull String key) {
        if (style != null) {
            final String[] globalAttrs = style.split(";");
            for (String globalAttr : globalAttrs) {
                final String[] attrs = globalAttr.split(":");
                if (attrs.length == 2 && attrs[0].trim().equals(key)) {
                    return attrs[1].trim();
                }
            }
        }
        return null;
    }

    @Nullable
    public static String formatImageUrl(@NonNull Context context, @Nullable String url) {
        return formatImageUrl(context, url, null);
    }

    @Nullable
    public static String formatImageUrl(@NonNull Context context, @Nullable String url, @Nullable Integer size) {
        if (size == null) {
            final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            size = Math.min(metrics.widthPixels, metrics.heightPixels);
        }
        return PreviewerUrlUtil.formatImageUrl(url, size, size, PreviewerUrlUtil.ScaleMode.PROGRESSIVE);
    }
}
