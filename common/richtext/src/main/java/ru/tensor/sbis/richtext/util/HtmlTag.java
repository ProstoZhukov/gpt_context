package ru.tensor.sbis.richtext.util;

/**
 * Список поддерживаемых по умолчанию тегов
 *
 * @author am.boldinov
 */
public interface HtmlTag {

    String BR = "br";
    String P = "p";
    String SPAN = "span";
    String STRONG = "strong";
    String B = "b";
    String EM = "em";
    String A = "a";
    String U = "u";
    String STRIKE = "strike";
    String DEL = "del";
    String S = "s";
    String IFRAME = "iframe";
    String UL = "ul";
    String OL = "ol";
    String LI = "li";
    String BLOCKQUOTE = "blockquote";
    String BLOCKQUOTE_SENDER = "blockquotesender";
    String DECORATED_LINK = "decoratedlink";
    String IMG = "img";
    String DIV = "div";
    String PRE = "pre";
    String H1 = "h1";
    String H2 = "h2";
    String H3 = "h3";
    String H4 = "h4";
    String H5 = "h5";
    String H6 = "h6";
    String TABLE = "table";
    String TABLE_ROW = "tr";
    String TABLE_HEADER = "th";
    String TABLE_CELL = "td";

    /**
     * Список поддерживаемых по умолчанию тегов для документов с расширением .sabydoc
     */
    interface SabyDoc {

        /**
         * Версия файла (документа)
         */
        String VERSION = "version";

        /**
         * Название файла (документа)
         */
        String NAME = "name";

    }

}
