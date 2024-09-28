package ru.tensor.sbis.richtext.view.strategy;

import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.span.view.ViewStubOptions;

/**
 * Интерфейс стратегии для предварительного вычисления вставки обтекаемого контента.
 * Вычисления производятся в момент потоковой обработки текста на этапе подготовки данных.
 *
 * @author am.boldinov
 */
public interface PrefetchStrategy {

    /**
     * Выполняет предварительные вычисления по обтеканию контента.
     * В данном методе рекомендуется при необходимости модифицировать текст и вставить на
     * нужные позиции Span для обтекания.
     *
     * @param text     редактируемый текст
     * @param position позиция начала обтекания
     * @param options  опции для конфигурации текста при размещении контента
     * @return новая позиция начала обтекания, при модификации текста может поменяться.
     */
    int prefetch(@NonNull Editable text, int position, @NonNull ViewStubOptions options);

    /**
     * Сообщает о вычислении размеров привязанной к стратегии View.
     * Вызывается перед измерением текста, поэтому посчитанные размеры можно применить
     * к существующим Span.
     *
     * @param view обтекаемая текстом View
     * @param widthMeasureSpec требование по режиму отображения и доступной ширине, которое накладывает родительский элемент при измерении
     */
    void onViewMeasured(@NonNull View view, int widthMeasureSpec);

}
