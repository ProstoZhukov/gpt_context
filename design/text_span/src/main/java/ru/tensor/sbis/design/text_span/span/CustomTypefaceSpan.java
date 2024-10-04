package ru.tensor.sbis.design.text_span.span;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import java.util.Objects;

import timber.log.Timber;

/**
 * Легаси код
 *
 * @author am.boldinov
 */
@SuppressLint("ParcelCreator")
public class CustomTypefaceSpan extends TypefaceSpan {

    public final Typeface mTypeface;

    public CustomTypefaceSpan(Typeface typeface) {
        super("");
        mTypeface = typeface;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, mTypeface);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, mTypeface);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        if (tf == null) {
            Timber.d("Cannot apply typeface with null value in CustomTypefaceSpan");
            return;
        }

        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomTypefaceSpan that = (CustomTypefaceSpan) o;
        return Objects.equals(mTypeface, that.mTypeface);
    }

    @Override
    public int hashCode() {
        return mTypeface != null ? mTypeface.hashCode() : 0;
    }
}
