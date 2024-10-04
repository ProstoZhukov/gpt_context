package ru.tensor.sbis.design.moneyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * Однострочный TextView с градиентом в прозрачность
 * <p>
 * Если ширина {@link #mTextWidth}, необходимая для отображения установленного текста,
 * больше ширины контейнера, устанавливается градиент
 *
 * @author ev.grigoreva
 */
public class SingleLineGradientTextView extends androidx.appcompat.widget.AppCompatTextView {
    private float mTextWidth;

    public SingleLineGradientTextView(Context context) {
        super(context, null, 0);
        init();
    }

    public SingleLineGradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public SingleLineGradientTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setSingleLine();
        setMaxLines(1);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && getWidth() < mTextWidth) {
            int gradientWidth = getResources().getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.gradient_size);
            int startWidth = getWidth() > gradientWidth ? getWidth() - gradientWidth : 0;
            getPaint().setShader(
                    new LinearGradient(startWidth, 0, getWidth(), 0, getCurrentTextColor(),
                            Color.TRANSPARENT, Shader.TileMode.CLAMP));
        }
    }

    /**
     * Установка текста для показа
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mTextWidth = text != null ? getPaint().measureText(String.valueOf(text)) : 0;
    }
}
