package ru.tensor.sbis.design.text_span;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import ru.tensor.sbis.design.TypefaceManager;

/**
 * Поле ввода с кастомным шрифтом
 */
public class SbisEditText extends AppCompatEditText {
    private OnKeyPreImeListener mOnKeyPreImeListener;

    public SbisEditText(Context context) {
        super(context);
        initialize(context, null);
    }

    public SbisEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SbisEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (isInEditMode())
            return;
        boolean isBold = false;
        if (attrs != null) {
            int[] attributes = new int[]{android.R.attr.textStyle};
            TypedArray attributesArray = context.getTheme().obtainStyledAttributes(attrs, attributes, 0, 0);
            int textStyle = attributesArray.getInt(0, Typeface.NORMAL);
            isBold = (textStyle == Typeface.BOLD);
            attributesArray.recycle();
        }

        setTypeface(isBold ? TypefaceManager.getRobotoBoldFont(getContext()) : TypefaceManager.getRobotoRegularFont(getContext()));
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mOnKeyPreImeListener != null) {
            mOnKeyPreImeListener.onKeyPreImeEvent(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // User has pressed Back key. So hide the keyboard and clear search view focus
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
                clearFocus();
            }
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * Слушатель событий onKeyPreIme
     */
    public interface OnKeyPreImeListener {
        /**
         * Обработка события клавиатуры.
         */
        void onKeyPreImeEvent(int keyCode, KeyEvent event);
    }

    /**
     * Установка слушателя событий onKeyPreIme
     */
    public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
        mOnKeyPreImeListener = onKeyPreImeListener;
    }
}
