package ru.tensor.sbis.mvp.interactor;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;

import ru.tensor.sbis.common.util.ScanUtil;

/**
 * Реализация интерактора для работы с файлом
 *
 * @author sa.nikitin, am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public class FileInteractor extends BaseInteractor {

    @NonNull
    private final Context mContext;

    public FileInteractor(@NonNull Context context) {
        super();
        mContext = context;
    }

    /**
     * Функция позволяет запустить сканирование файла.
     */
    public void scanFile(@NonNull File file) {
        ScanUtil.scanFile(mContext, file);
    }
}
