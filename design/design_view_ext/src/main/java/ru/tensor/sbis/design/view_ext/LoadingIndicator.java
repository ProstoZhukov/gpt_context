package ru.tensor.sbis.design.view_ext;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Компонент индикатор процесса загрузки.
 *
 * @deprecated не поддерживает темизацию. Нужно использовать ru.tensor.sbis.design.progress.SbisLoadingIndicator
 */
@SuppressWarnings("deprecation")
public class LoadingIndicator extends SbisProgressBar {

    public LoadingIndicator(Context context) {
        super(context);
    }

    public LoadingIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
