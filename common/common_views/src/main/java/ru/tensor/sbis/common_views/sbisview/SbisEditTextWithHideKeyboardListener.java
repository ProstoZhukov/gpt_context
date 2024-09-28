package ru.tensor.sbis.common_views.sbisview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import ru.tensor.sbis.design.TypefaceManager;

/**
 * Created by ss.buvaylink on 17.06.2016.
 */
public class SbisEditTextWithHideKeyboardListener extends AppCompatEditText {

    private OnKeyPreImeListener mOnKeyPreImeListener;

    public SbisEditTextWithHideKeyboardListener(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SbisEditTextWithHideKeyboardListener(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SbisEditTextWithHideKeyboardListener(@NonNull Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (!isInEditMode()) {
            boolean isBold = false;
            if (attrs != null) {
                int[] attributes = new int[]{android.R.attr.textStyle};
                TypedArray attributesArray = context.getTheme().obtainStyledAttributes(attrs, attributes, 0, 0);
                int textStyle = attributesArray.getInt(0, Typeface.NORMAL);
                isBold = (textStyle == Typeface.BOLD);
                attributesArray.recycle();
            }

            setTypeface(isBold ? TypefaceManager.getRobotoBoldFont(context) : TypefaceManager.getRobotoRegularFont(context));
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mOnKeyPreImeListener != null) {
            return mOnKeyPreImeListener.onKeyPreImeEvent(keyCode, event);
        } else {
            return super.onKeyPreIme(keyCode, event);
        }
    }

    /*** @SelfDocumented */
    public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
        mOnKeyPreImeListener = onKeyPreImeListener;
    }

    public interface OnKeyPreImeListener {
        boolean onKeyPreImeEvent(int keyCode, KeyEvent event);
    }
}