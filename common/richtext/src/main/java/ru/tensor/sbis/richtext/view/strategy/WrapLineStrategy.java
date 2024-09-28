package ru.tensor.sbis.richtext.view.strategy;

import android.view.View;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.span.view.ViewStubOptions;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * Интерфейс стратегии по обтеканию текста или определенных строк
 *
 * @author am.boldinov
 */
public interface WrapLineStrategy {

    /**
     * Вызывается для применения обтекания к строке.
     * Необходим для наложения спанов к части текста или его модификации.
     * <p>
     * Не рекомендуется изменять параметры View в данном методе, для этого есть {@link RichViewLayout.ViewHolder}
     *
     * @param layout    Расширение {@link android.text.DynamicLayout} для возможности работать с обтекаемыми View
     * @param view      элемент, который будет обтекать текст
     * @param options   опции для конфигурации текста при размещении View
     * @param cursor    курсор для текущей строки, при модификации текста необходимо учитывать добавление новых строк
     *                  и при необходимости перемещать курсор
     * @param spanStart позиция {@link ru.tensor.sbis.richtext.span.view.ViewStubSpan} в тексте,
     *                  считается за начало обтекания, может не совпадать с позицией начала строки
     */
    boolean wrap(@NonNull ViewLayout layout, @NonNull View view, @NonNull ViewStubOptions options,
                 @NonNull LineCursor cursor, int spanStart);

    /**
     * Вызывается для вычисления смещения View и изменения ее {@link RichViewLayout.LayoutParams}.
     * Будет вызван только 1 раз для первой строки обтекания, на основе положения этой строки в тексте
     * можно определить смещение View.
     *
     * @param layoutParams параметры View
     * @param layout       Расширение {@link android.text.DynamicLayout} для возможности работать с обтекаемыми View
     * @param line         номер строки обтекания
     */
    void layout(@NonNull RichViewLayout.LayoutParams layoutParams, @NonNull ViewLayout layout, int line);

    /**
     * Вызывается по завершению обтекания.
     *
     * @param layout       Расширение {@link android.text.DynamicLayout} для возможности работать с обтекаемыми View
     * @param line         последний номер строки, на которой View завершила обтекание
     * @param stretchSpace расстояние, на которое рекомендуется растянуть последнюю строку, для того, чтобы
     *                     она заполняла всё пространство рядом с обтекаемой View {@link ParagraphStretchProcessor}
     */
    void onWrapCompleted(@NonNull ViewLayout layout, int line, int stretchSpace);

}
