package ru.tensor.sbis.design.text_span.text.expandable.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import ru.tensor.sbis.design.text_span.text.expandable.AbstractExpandableTextView;

/**
 * Реализация ExpandableTextView без кнопки (состояние изменяется при клике на контент).
 *
 * @author am.boldinov
 */
public class SimpleExpandableTextView extends AbstractExpandableTextView {

    public SimpleExpandableTextView(Context context) {
        this(context, null);
    }

    public SimpleExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Задаем тип кнопки - Без кнопки
        super.setButtonType(BUTTON_TYPE_NONE);
        // Добавляем поддержку изменения состоянию view при клике на контент
        setExpandOnContentClick(true);
    }

    @Override
    @Deprecated
    public void setButtonType(int buttonType) {
        /* Изменение типа кнопки не поддерживается */
    }

    @SuppressWarnings("unused")
    @Override
    protected int getExpandButtonHeight() {
        return 0;
    }

    @SuppressWarnings("unused")
    @Override
    protected int getCollapseButtonHeight() {
        return 0;
    }

    @SuppressWarnings("unused")
    @Override
    protected void drawButton(Canvas canvas, Rect rect) {
        /* Кнопку отрисовывать не нужно */
    }

}
