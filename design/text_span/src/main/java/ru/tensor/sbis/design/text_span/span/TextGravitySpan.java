package ru.tensor.sbis.design.text_span.span;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Легаси код
 *
 * @author am.boldinov
 */
public class TextGravitySpan extends MetricAffectingSpan {

    public final int shift;

    public TextGravitySpan(int shift) {
        this.shift = shift;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.baselineShift = shift;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.baselineShift = shift;
    }
}
