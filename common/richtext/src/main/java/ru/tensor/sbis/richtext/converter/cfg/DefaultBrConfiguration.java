package ru.tensor.sbis.richtext.converter.cfg;

/**
 * Используемая по умолчанию конфигурация тегов переноса строк <br>, </br>
 *
 * @author am.boldinov
 */
public class DefaultBrConfiguration implements BrConfiguration {

    @Override
    public int getMaxLineBreakCount() {
        return 2;
    }
}
