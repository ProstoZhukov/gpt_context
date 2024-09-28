package ru.tensor.sbis.richtext.converter.cfg;

/**
 * Конфигурация тегов переноса строк <br>, </br>
 *
 * @author am.boldinov
 */
public interface BrConfiguration {

    /**
     * @return максимальное количество переносов строк в случае если теги идут подряд.
     * Значение не учитывается при рендере контента с обтеканием изображениями и вью.
     */
    int getMaxLineBreakCount();
}
