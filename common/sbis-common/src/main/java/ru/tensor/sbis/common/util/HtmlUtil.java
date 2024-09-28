package ru.tensor.sbis.common.util;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by developer on 24/03/15.
 */
public class HtmlUtil {

    /**
     * список HTML символов: какой что означает
     */
    private static final Map<String, String> specialChars = new HashMap<String, String>() {
        {
            put("&nbsp;", " ");
            put("&laquo;", "«");
            put("&raquo;", "»");
            put("&quot;", "\"");
            put("&mdash;", "—");
            put("&ndash;", "–");
            put("&lt;", "<");
            put("&gt;", ">");
            put("&copy;", "©");
            put("&reg;", "®");
            put("&trade;", "™");
            put("&ordm;", "º");
            put("&ordf;", "ª");
            put("&permil;", "‰");
            put("&pi;", "π");
            put("&brvbar;", "¦");
            put("&sect;", "§");
            put("&deg;", "°");
            put("&micro;", "µ");
            put("&para;", "¶");
            put("&hellip;", "…");
            put("&oline;", "‾");
            put("&acute;", "´");
            put("&times;", "×");
            put("&divide;", "÷");
            put("&plusmn;", "±");
            put("&sup1;", "¹");
            put("&sup2;", "²");
            put("&sup3;", "³");
            put("&not;", "¬");
            put("&frac14;", "¼");
            put("&frac12;", "½");
            put("&frac34;", "¾");
            put("&frasl;", "⁄");
            put("&minus;", "−");
            put("&le;", "≤");
            put("&ge;", "≥");
            put("&asymp;", "≈");
            put("&ne;", "≠");
            put("&equiv;", "≡");
            put("&radic;", "√");
            put("&infin;", "∞");
            put("&sum;", "∑");
            put("&prod;", "∏");
            put("&part;", "∂");
            put("&int;", "∫");
            put("&forall;", "∀");
            put("&exist;", "∃");
            put("&empty;", "∅");
            put("&Oslash;", "Ø");
            put("&isin;", "∈");
            put("&notin;", "∉");
            put("&ni;", "∋");
            put("&sub;", "⊂");
            put("&sup;", "⊃");
            put("&nsub;", "⊄");
            put("&sube;", "⊆");
            put("&supe;", "⊇");
            put("&oplus;", "⊕");
            put("&otimes;", "⊗");
            put("&perp;", "⊥");
            put("&ang;", "∠");
            put("&and;", "∧");
            put("&or;", "∨");
            put("&cap;", "∩");
            put("&cup;", "∪");
            put("&euro;", "€");
            put("&cent;", "¢");
            put("&pound;", "£");
            put("&current;", "¤");
            put("&yen;", "¥");
            put("&fnof;", "ƒ");
            put("&bull;", "•");
            put("&middot;", "·");
            put("&spades;", "♠");
            put("&clubs;", "♣");
            put("&hearts;", "♥");
            put("&diams;", "♦");
            put("&loz;", "◊");
            put("&amp;", "&");
            put("&prime;", "′");
            put("&Prime;", "″");
            put("&lsquo;", "‘");
            put("&rsquo;", "’");
            put("&sbquo;", "‚");
            put("&ldquo;", "“");
            put("&rdquo;", "”");
            put("&bdquo;", "„");
            put("&larr;", "←");
            put("&uarr;", "↑");
            put("&rarr;", "→");
            put("&darr;", "↓");
            put("&harr;", "↔");
            put("&lArr;", "⇐");
            put("&uArr;", "⇑");
            put("&rArr;", "⇒");
            put("&dArr;", "⇓");
            put("&hArr;", "⇔");
        }
    };

    /**
     * Удаляет из строки HTML тэги. Спец.символы преобразуются в читаемый формат.
     *
     * @param textWithHtml Строка, которую нужно почистить.
     * @return Строка без html тэгов
     */
    public static String removeHtmlTags(@NonNull String textWithHtml) {
        String result = stripHtmlTags(textWithHtml);

        return stripSpecialChars(result);
    }

    /**
     * Преобразует html символы в читаемый формат.
     *
     * @param source Строка, которую нужно почистить.
     * @return Строка без html тэгов
     */
    public static String stripSpecialChars(String source) {
        for (Map.Entry<String, String> charEntry : specialChars.entrySet()) {
            source = source.replace(charEntry.getKey(), charEntry.getValue());
        }

        return source;
    }

    /**
     * Удаляет HTML тэги. Используем не regex, так как он медленный.
     *
     * @param source Строка, которую нужно почистить.
     * @return Строка без html тэгов
     */
    @NonNull
    private static String stripHtmlTags(@NonNull String source) {
        char[] array = new char[source.length()];
        int arrayIndex = 0;
        boolean inside = false;

        for (int i = 0; i < source.length(); i++) {
            char let = source.charAt(i);
            if (let == '<') {
                inside = true;
                continue;
            }
            if (let == '>') {
                inside = false;
                continue;
            }
            if (!inside) {
                array[arrayIndex] = let;
                arrayIndex++;
            }
        }
        return new String(array, 0, arrayIndex);
    }
}

